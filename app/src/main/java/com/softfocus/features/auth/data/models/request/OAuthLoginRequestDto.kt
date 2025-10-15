package com.softfocus.features.auth.data.models.request

import com.google.gson.annotations.SerializedName

/**
 * Request DTO for OAuth login (for existing users).
 */
data class OAuthLoginRequestDto(
    @SerializedName("provider")
    val provider: String,

    @SerializedName("token")
    val token: String
)
