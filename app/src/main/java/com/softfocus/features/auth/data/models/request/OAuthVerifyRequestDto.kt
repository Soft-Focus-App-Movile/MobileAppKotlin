package com.softfocus.features.auth.data.models.request

import com.google.gson.annotations.SerializedName

/**
 * Request DTO for verifying OAuth token with backend.
 * This is the first step in OAuth authentication flow.
 */
data class OAuthVerifyRequestDto(
    @SerializedName("provider")
    val provider: String,  // "Google" or "Facebook"

    @SerializedName("accessToken")
    val accessToken: String,

    @SerializedName("refreshToken")
    val refreshToken: String? = null,

    @SerializedName("expiresAt")
    val expiresAt: String? = null
)
