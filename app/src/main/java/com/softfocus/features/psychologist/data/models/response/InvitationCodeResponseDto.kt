package com.softfocus.features.psychologist.data.models.response

import com.google.gson.annotations.SerializedName
import com.softfocus.features.psychologist.domain.models.InvitationCode

data class InvitationCodeResponseDto(
    @SerializedName("invitationCode")
    val code: String,
    val generatedAt: String,
    val expiresAt: String,
    val isExpired: Boolean,
    val timeUntilExpiration: String
) {
    fun toDomain() = InvitationCode(
        code = code,
        expiresAt = expiresAt,
        timeUntilExpiration = timeUntilExpiration
    )
}
