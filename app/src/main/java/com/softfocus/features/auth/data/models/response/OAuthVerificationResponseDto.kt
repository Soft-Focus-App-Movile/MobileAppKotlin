package com.softfocus.features.auth.data.models.response

import com.google.gson.annotations.SerializedName

/**
 * Response DTO from OAuth verification endpoint.
 * Contains user info and whether registration is needed.
 */
data class OAuthVerificationResponseDto(
    @SerializedName("email")
    val email: String,

    @SerializedName("fullName")
    val fullName: String,

    @SerializedName("provider")
    val provider: String,

    @SerializedName("tempToken")
    val tempToken: String,

    @SerializedName("needsRegistration")
    val needsRegistration: Boolean,

    @SerializedName("existingUserType")
    val existingUserType: String? = null
)
