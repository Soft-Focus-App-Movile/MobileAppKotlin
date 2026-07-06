package com.softfocus.features.therapy.data.remote

import android.util.Log
import com.google.gson.Gson
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import com.softfocus.core.data.local.UserSession
import com.softfocus.core.networking.ApiConstants
import com.softfocus.features.therapy.domain.models.IncomingCallInfo
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit

/** Signaling events for the call the current user is actively in (caller or answerer). */
sealed class CallSignalEvent {
    abstract val callId: String
    data class Accepted(override val callId: String, val userName: String?) : CallSignalEvent()
    data class Rejected(override val callId: String) : CallSignalEvent()
    data class Ended(override val callId: String) : CallSignalEvent()
}

/**
 * Connects to the backend "/callHub" and listens for call signaling so the app can ring the callee
 * from anywhere. Exposes the current incoming call as a StateFlow that a global overlay observes.
 *
 * Server events handled:
 *   - "IncomingCall" -> a call is ringing this user.
 *   - "CallEnded"    -> the caller cancelled/ended; dismiss the incoming ring if it matches.
 */
class CallSignalRService(
    private val userSession: UserSession
) {
    private var hubConnection: HubConnection? = null
    private val gson = Gson()
    private var isManuallyStopping = false

    private val _incomingCall = MutableStateFlow<IncomingCallInfo?>(null)
    val incomingCall: StateFlow<IncomingCallInfo?> = _incomingCall.asStateFlow()

    // Events for the active call (accepted / rejected / ended), consumed by the CallViewModel.
    private val _callEvents = MutableSharedFlow<CallSignalEvent>(extraBufferCapacity = 16)
    val callEvents: SharedFlow<CallSignalEvent> = _callEvents.asSharedFlow()

    fun initConnection() {
        if (hubConnection != null) return
        val token = userSession.getUser()?.token ?: run {
            Log.e(TAG, "No se pudo iniciar CallHub: token nulo.")
            return
        }

        try {
            hubConnection = HubConnectionBuilder.create(ApiConstants.Calls.HUB_URL)
                .withAccessTokenProvider(Single.defer {
                    Single.just(token.removePrefix("Bearer "))
                })
                .build()

            registerHandlers()

            hubConnection?.onClosed { exception ->
                if (isManuallyStopping) {
                    isManuallyStopping = false
                    return@onClosed
                }
                if (exception != null) {
                    Log.w(TAG, "CallHub perdido: ${exception.message}. Reintentando en 5s...")
                    try {
                        Thread.sleep(5000)
                        startConnection()
                    } catch (e: InterruptedException) {
                        Log.e(TAG, "Reintento interrumpido", e)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error construyendo CallHub: ${e.message}", e)
        }
    }

    private fun registerHandlers() {
        hubConnection?.on(
            "IncomingCall",
            { payload: Map<*, *> ->
                try {
                    val json = gson.toJson(payload)
                    Log.d(TAG, "IncomingCall: $json")
                    val dto = gson.fromJson(json, IncomingCallPayload::class.java)
                    _incomingCall.value = IncomingCallInfo(
                        callId = dto.callId.orEmpty(),
                        channelName = dto.channelName.orEmpty(),
                        callType = dto.callType ?: "Video",
                        mode = dto.mode ?: "Direct",
                        appId = dto.appId.orEmpty(),
                        callerId = dto.caller?.id.orEmpty(),
                        callerName = dto.caller?.name ?: "Llamada entrante",
                        callerRole = dto.caller?.role.orEmpty()
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error parseando IncomingCall: ${e.message}", e)
                }
            },
            Map::class.java
        )

        hubConnection?.on(
            "CallAccepted",
            { payload: Map<*, *> ->
                try {
                    val dto = gson.fromJson(gson.toJson(payload), CallAcceptedPayload::class.java)
                    Log.d(TAG, "CallAccepted: callId=${dto.callId}")
                    dto.callId?.let { _callEvents.tryEmit(CallSignalEvent.Accepted(it, dto.user?.name)) }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parseando CallAccepted: ${e.message}", e)
                }
            },
            Map::class.java
        )

        hubConnection?.on(
            "CallRejected",
            { payload: Map<*, *> ->
                try {
                    val dto = gson.fromJson(gson.toJson(payload), CallEndedPayload::class.java)
                    Log.d(TAG, "CallRejected: callId=${dto.callId}")
                    dto.callId?.let {
                        _callEvents.tryEmit(CallSignalEvent.Rejected(it))
                        if (_incomingCall.value?.callId == it) _incomingCall.value = null
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parseando CallRejected: ${e.message}", e)
                }
            },
            Map::class.java
        )

        hubConnection?.on(
            "CallEnded",
            { payload: Map<*, *> ->
                try {
                    val json = gson.toJson(payload)
                    val dto = gson.fromJson(json, CallEndedPayload::class.java)
                    Log.d(TAG, "CallEnded: callId=${dto.callId}")
                    dto.callId?.let {
                        _callEvents.tryEmit(CallSignalEvent.Ended(it))
                        // If the call that ended is the one we're ringing for, dismiss the ring.
                        if (_incomingCall.value?.callId == it) _incomingCall.value = null
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parseando CallEnded: ${e.message}", e)
                }
            },
            Map::class.java
        )
    }

    fun startConnection() {
        if (hubConnection == null) initConnection()
        if (hubConnection?.connectionState == HubConnectionState.DISCONNECTED) {
            try {
                hubConnection?.start()?.blockingAwait(5, TimeUnit.SECONDS)
                Log.d(TAG, "CallHub conectado. ID: ${hubConnection?.connectionId}")
            } catch (e: Exception) {
                Log.e(TAG, "Error conectando CallHub: ${e.message}", e)
            }
        }
    }

    fun stopConnection() {
        isManuallyStopping = true
        hubConnection?.stop()
        hubConnection = null
        _incomingCall.value = null
    }

    /** Clear the current incoming-call ring (after accept/reject/handled). */
    fun clearIncomingCall() {
        _incomingCall.value = null
    }

    // --- Wire payloads ---
    private data class IncomingCallPayload(
        val callId: String?,
        val channelName: String?,
        val callType: String?,
        val mode: String?,
        val appId: String?,
        val caller: Caller?
    ) {
        data class Caller(val id: String?, val name: String?, val role: String?)
    }

    private data class CallEndedPayload(
        val callId: String?,
        val endedBy: String?,
        val status: String?
    )

    private data class CallAcceptedPayload(
        val callId: String?,
        val channelName: String?,
        val user: User?
    ) {
        data class User(val id: String?, val name: String?)
    }

    companion object {
        private const val TAG = "CallSignalRService"
    }
}
