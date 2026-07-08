package com.softfocus.features.therapy.presentation.call

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.features.therapy.data.remote.AgoraCallManager
import com.softfocus.features.therapy.data.remote.CallSignalEvent
import com.softfocus.features.therapy.data.remote.CallSignalRService
import com.softfocus.features.therapy.domain.models.CallAccess
import com.softfocus.features.therapy.domain.usecases.AnswerCallUseCase
import com.softfocus.features.therapy.domain.usecases.EndCallUseCase
import com.softfocus.features.therapy.domain.usecases.InitiateCallUseCase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class CallPhase { Connecting, Ringing, InCall, Ended, Error }

data class CallUiState(
    val phase: CallPhase = CallPhase.Connecting,
    val isVideo: Boolean = true,
    val calleeName: String = "",
    val callId: String? = null,
    val remoteUid: Int? = null,
    val micMuted: Boolean = false,
    val cameraOn: Boolean = true,
    val speakerOn: Boolean = true,
    val errorMessage: String? = null
)

/**
 * Drives a single call: starts it (patient → psychologist), wires the Agora engine, exposes the
 * in-call controls and tears everything down on hang up.
 */
class CallViewModel(
    private val initiateCallUseCase: InitiateCallUseCase,
    private val answerCallUseCase: AnswerCallUseCase,
    private val endCallUseCase: EndCallUseCase,
    private val callSignalRService: CallSignalRService,
    val agora: AgoraCallManager,
    calleeName: String,
    isVideo: Boolean,
    /** When set, this is an incoming call to answer instead of an outgoing call to start. */
    private val incomingCallId: String? = null,
    /** When a psychologist calls a specific patient, this is that patient's id. Patients leave it null. */
    private val targetUserId: String? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        CallUiState(
            isVideo = isVideo,
            calleeName = calleeName,
            cameraOn = isVideo,
            speakerOn = isVideo // video → loudspeaker, audio → earpiece by default
        )
    )
    val uiState: StateFlow<CallUiState> = _uiState.asStateFlow()

    private var access: CallAccess? = null

    private var signalingObserved = false

    /** Entry point once permissions are granted: answers an incoming call or starts an outgoing one. */
    fun start() {
        observeSignaling()
        if (incomingCallId != null) answerIncomingCall() else startOutgoingCall()
    }

    /**
     * Reacts to call signaling from the backend (over /callHub) for THIS call:
     * accepted -> connected, rejected/ended -> close. This is what drives the call state,
     * independent of the Agora media layer.
     */
    private fun observeSignaling() {
        if (signalingObserved) return
        signalingObserved = true
        viewModelScope.launch {
            callSignalRService.callEvents.collect { event ->
                val myCallId = _uiState.value.callId ?: return@collect
                if (event.callId != myCallId) return@collect
                when (event) {
                    is CallSignalEvent.Accepted ->
                        _uiState.update { if (it.phase == CallPhase.Ringing) it.copy(phase = CallPhase.InCall) else it }
                    is CallSignalEvent.Rejected -> endLocally("Llamada rechazada")
                    is CallSignalEvent.Ended -> endLocally(null)
                }
            }
        }
    }

    /** Tears down the call locally (the other party ended/rejected; no backend call needed). */
    private fun endLocally(message: String?) {
        if (_uiState.value.phase == CallPhase.Ended) return
        agora.leaveAndDestroy()
        _uiState.update { it.copy(phase = CallPhase.Ended, errorMessage = message ?: it.errorMessage) }
    }

    /** Starts an outgoing call to the patient's psychologist. Idempotent. */
    private fun startOutgoingCall() {
        if (_uiState.value.callId != null || _uiState.value.phase == CallPhase.Error) return
        viewModelScope.launch {
            _uiState.update { it.copy(phase = CallPhase.Connecting) }
            val callType = if (_uiState.value.isVideo) "Video" else "Audio"
            initiateCallUseCase(callType = callType, mode = "Direct", targetUserId = targetUserId)
                .onSuccess { ca ->
                    access = ca
                    _uiState.update { it.copy(callId = ca.callId, phase = CallPhase.Ringing) }
                    joinChannel(ca)
                }
                .onFailure { e ->
                    _uiState.update { it.copy(phase = CallPhase.Error, errorMessage = e.message) }
                }
        }
    }

    /** Accepts an incoming call: fetches join credentials and joins the channel. */
    private fun answerIncomingCall() {
        val id = incomingCallId ?: return
        if (_uiState.value.callId != null || _uiState.value.phase == CallPhase.Error) return
        viewModelScope.launch {
            _uiState.update { it.copy(phase = CallPhase.Connecting) }
            answerCallUseCase(id)
                .onSuccess { ca ->
                    access = ca
                    // The user just accepted, so they're already in the call.
                    _uiState.update { it.copy(callId = ca.callId, phase = CallPhase.InCall) }
                    joinChannel(ca)
                }
                .onFailure { e ->
                    _uiState.update { it.copy(phase = CallPhase.Error, errorMessage = e.message) }
                }
        }
    }

    private fun joinChannel(ca: CallAccess) {
        agora.onRemoteUserJoined = { uid ->
            _uiState.update { it.copy(remoteUid = uid, phase = CallPhase.InCall) }
        }
        agora.onRemoteUserLeft = {
            // The other side hung up → end on our side too.
            hangUp()
        }
        agora.onError = { msg ->
            _uiState.update { it.copy(errorMessage = msg) }
        }
        agora.initialize(ca.appId, ca.isVideo)
        agora.setSpeakerphoneOn(_uiState.value.speakerOn)
        agora.join(ca.token, ca.channelName, ca.userAccount, ca.isVideo)
    }

    fun toggleMic() {
        val muted = !_uiState.value.micMuted
        agora.setMicMuted(muted)
        _uiState.update { it.copy(micMuted = muted) }
    }

    fun toggleCamera() {
        val on = !_uiState.value.cameraOn
        agora.setCameraEnabled(on)
        _uiState.update { it.copy(cameraOn = on) }
    }

    fun switchCamera() = agora.switchCamera()

    fun toggleSpeaker() {
        val on = !_uiState.value.speakerOn
        agora.setSpeakerphoneOn(on)
        _uiState.update { it.copy(speakerOn = on) }
    }

    fun hangUp() {
        if (_uiState.value.phase == CallPhase.Ended) return
        val id = _uiState.value.callId
        viewModelScope.launch {
            if (id != null) endCallUseCase(id)
            agora.leaveAndDestroy()
            _uiState.update { it.copy(phase = CallPhase.Ended) }
        }
    }

    // --- Helpers used by the Composable to render Agora video (TextureView) ---
    fun createRendererView(): View = agora.createRendererView()
    fun setupLocalVideo(view: View) = agora.setupLocalVideo(view)
    fun setupRemoteVideo(view: View, uid: Int) = agora.setupRemoteVideo(view, uid)

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCleared() {
        val id = _uiState.value.callId
        if (_uiState.value.phase != CallPhase.Ended) {
            agora.leaveAndDestroy()
            // Best-effort: cerrar la sesión en el backend aunque el ViewModel ya se esté
            // destruyendo (salida abrupta / app cerrada / back sin colgar). Si no se cierra,
            // el usuario queda "en llamada" en el servidor y el próximo initiate devuelve 400.
            // Usamos GlobalScope porque viewModelScope ya está cancelado en onCleared.
            if (id != null) {
                GlobalScope.launch { runCatching { endCallUseCase(id) } }
            }
        }
        super.onCleared()
    }
}
