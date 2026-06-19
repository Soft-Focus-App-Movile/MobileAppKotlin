package com.softfocus.features.therapy.domain.models

/**
 * Credentials and metadata to join an Agora call channel.
 */
data class CallAccess(
    val callId: String,
    val channelName: String,
    val appId: String,
    val token: String,
    val userAccount: String,
    val callType: String,
    val mode: String,
    val status: String,
    val inviteeIds: List<String> = emptyList()
) {
    val isVideo: Boolean get() = callType.equals("Video", ignoreCase = true)
}
