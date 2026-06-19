package com.softfocus.features.therapy.data.remote

import android.content.Context
import android.util.Log
import android.view.SurfaceView
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
    }

    fun initialize(appId: String, isVideo: Boolean) {
        if (engine != null) return
        try {
            val config = RtcEngineConfig().apply {
                mContext = context
                mAppId = appId
                mEventHandler = eventHandler
            }
            engine = RtcEngine.create(config)
            engine?.apply {
                enableAudio()
                if (isVideo) {
                    enableVideo()
                    startPreview()
                } else {
                    disableVideo()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Agora engine", e)
            onError?.invoke(e.message ?: "No se pudo iniciar el motor de Agora")
        }
    }

    // A plain SurfaceView works as an Agora VideoCanvas surface across SDK versions.
    fun createRendererView(): SurfaceView = SurfaceView(context.applicationContext)

    fun setupLocalVideo(surface: SurfaceView) {
        engine?.setupLocalVideo(VideoCanvas(surface, VideoCanvas.RENDER_MODE_HIDDEN, 0))
    }

    fun setupRemoteVideo(surface: SurfaceView, uid: Int) {
        engine?.setupRemoteVideo(VideoCanvas(surface, VideoCanvas.RENDER_MODE_HIDDEN, uid))
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

    fun leaveAndDestroy() {
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
