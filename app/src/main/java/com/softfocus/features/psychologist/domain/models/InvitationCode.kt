package com.softfocus.features.psychologist.domain.models

data class InvitationCode(
    val code: String,
    val expiresAt: String,
    val timeUntilExpiration: String
)
