package com.softfocus.features.therapy.domain.models

/**
 * An incoming call pushed to the callee over the /callHub "IncomingCall" event.
 */
data class IncomingCallInfo(
    val callId: String,
    val channelName: String,
    val callType: String,   // "Audio" | "Video"
    val mode: String,       // "Direct" | "Group"
    val appId: String,
    val callerId: String,
    val callerName: String,
    val callerRole: String
) {
    val isVideo: Boolean get() = callType.equals("Video", ignoreCase = true)
}
