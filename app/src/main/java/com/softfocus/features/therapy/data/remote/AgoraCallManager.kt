package com.softfocus.features.therapy.data.remote

import android.content.Context
import android.util.Log
import android.view.TextureView
import android.view.View
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas

/**
 * Thin wrapper around the Agora RtcEngine (SDK 4.x, package io.agora.rtc2) for a single call.
 * The audio/video media flows peer-to-peer through Agora; this class only manages the engine
 * lifecycle, channel join/leave and local controls. UI state is surfaced back through the
 * callback properties, which the ViewModel sets.
 */
class AgoraCallManager(private val context: Context) {

    private var engine: RtcEngine? = null

    // Callbacks set by the ViewModel.
    var onJoinSuccess: (() -> Unit)? = null
    var onRemoteUserJoined: ((uid: Int) -> Unit)? = null
    var onRemoteUserLeft: ((uid: Int) -> Unit)? = null
    var onError: ((message: String) -> Unit)? = null

    private val eventHandler = object : IRtcEngineEventHandler() {
        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            Log.d(TAG, "Joined channel $channel as uid=$uid")
            onJoinSuccess?.invoke()
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            Log.d(TAG, "Remote user joined: $uid")
            onRemoteUserJoined?.invoke(uid)
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            Log.d(TAG, "Remote user offline: $uid (reason=$reason)")
            onRemoteUserLeft?.invoke(uid)
        }

        override fun onError(err: Int) {
            Log.e(TAG, "Agora error: $err")
            onError?.invoke("Agora error code $err")
        }

        override fun onPermissionError(permission: Int) {
            // 0 = RECORD_AUDIO, 1 = CAMERA
            Log.e(TAG, "onPermissionError: $permission (0=mic, 1=camera)")
        }

        override fun onCameraReady() {
            Log.d(TAG, "onCameraReady")
        }

        override fun onLocalVideoStateChanged(source: Constants.VideoSourceType?, state: Int, error: Int) {
            // state: 0=Stopped 1=Capturing 2=Encoding; error != 0 means a failure (e.g. no permission/device busy)
            Log.d(TAG, "onLocalVideoStateChanged: state=$state error=$error")
        }

        override fun onFirstLocalVideoFrame(source: Constants.VideoSourceType?, width: Int, height: Int, elapsed: Int) {
            Log.d(TAG, "onFirstLocalVideoFrame: ${width}x$height")
        }

        override fun onRemoteVideoStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) {
            Log.d(TAG, "onRemoteVideoStateChanged: uid=$uid state=$state reason=$reason")
        }
    }

    fun initialize(appId: String, isVideo: Boolean) {
        if (engine != null) return
        Log.d(TAG, "initialize: appId=${appId.take(8)}… isVideo=$isVideo")
        try {
            // Capture the app context OUTSIDE the apply block: inside it, `context` would resolve to
            // RtcEngineConfig's own (null) field, not this class's context — which was the bug that
            // made RtcEngine.create receive a null context and fail silently (no audio/video ever).
            val appContext = context.applicationContext
            val handler = eventHandler
            val config = RtcEngineConfig().apply {
                mContext = appContext
                mAppId = appId
                mEventHandler = handler
            }
            engine = RtcEngine.create(config)
            Log.d(TAG, "RtcEngine.create -> engine != null: ${engine != null}")
            engine?.apply {
                enableAudio()
                // Route audio to the loudspeaker for video calls; earpiece for voice calls.
                setDefaultAudioRoutetoSpeakerphone(isVideo)
                if (isVideo) {
                    val rv = enableVideo()
                    val rp = startPreview() // start capturing now; the view is bound once Compose creates it
                    Log.d(TAG, "video enabled: enableVideo=$rv startPreview=$rp")
                } else {
                    disableVideo()
                }
            }
        } catch (t: Throwable) {
            // Catch Throwable (not just Exception) so native-lib failures (UnsatisfiedLinkError) surface.
            Log.e(TAG, "Error initializing Agora engine: ${t.javaClass.simpleName}: ${t.message}", t)
            onError?.invoke(t.message ?: "No se pudo iniciar el motor de Agora")
        }
    }

    // TextureView (not SurfaceView) composes correctly inside Compose's AndroidView — a plain
    // SurfaceView gets hidden behind the Compose window background due to surface z-ordering.
    fun createRendererView(): View = TextureView(context.applicationContext)

    fun setupLocalVideo(view: View) {
        val r = engine?.setupLocalVideo(VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, 0))
        engine?.startPreview()
        Log.d(TAG, "setupLocalVideo -> $r")
    }

    fun setupRemoteVideo(view: View, uid: Int) {
        engine?.setupRemoteVideo(VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, uid))
    }

    fun join(token: String, channelName: String, userAccount: String, isVideo: Boolean) {
        val options = ChannelMediaOptions().apply {
            channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            publishMicrophoneTrack = true
            publishCameraTrack = isVideo
            autoSubscribeAudio = true
            autoSubscribeVideo = isVideo
        }
        engine?.joinChannelWithUserAccount(token, channelName, userAccount, options)
    }

    fun setMicMuted(muted: Boolean) {
        engine?.muteLocalAudioStream(muted)
    }

    fun setCameraEnabled(enabled: Boolean) {
        engine?.enableLocalVideo(enabled)
        engine?.muteLocalVideoStream(!enabled)
    }

    fun switchCamera() {
        engine?.switchCamera()
    }

    fun setSpeakerphoneOn(on: Boolean) {
        engine?.setEnableSpeakerphone(on)
    }

    private var destroyed = false

    fun leaveAndDestroy() {
        if (destroyed) return
        destroyed = true
        try {
            engine?.stopPreview()
            engine?.leaveChannel()
        } catch (e: Exception) {
            Log.e(TAG, "Error leaving channel", e)
        } finally {
            engine = null
            // Must run off the IRtcEngineEventHandler thread; safe to call here from main/VM scope.
            RtcEngine.destroy()
        }
    }

    companion object {
        private const val TAG = "AgoraCallManager"
    }
}
