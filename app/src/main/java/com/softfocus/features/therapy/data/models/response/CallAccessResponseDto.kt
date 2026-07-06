package com.softfocus.features.therapy.data.models.response

import com.softfocus.features.therapy.domain.models.CallAccess

/**
 * Response from initiate / answer / token endpoints: everything needed to join the Agora channel.
 */
data class CallAccessResponseDto(
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
    fun toDomain(): CallAccess = CallAccess(
        callId = callId,
        channelName = channelName,
        appId = appId,
        token = token,
        userAccount = userAccount,
        callType = callType,
        mode = mode,
        status = status,
        inviteeIds = inviteeIds
    )
}
