package com.softfocus.features.auth.data.models.request

/**
 * Request DTO for general user registration.
 * Endpoint: POST /api/v1/auth/register/general
 */
data class RegisterGeneralUserRequestDto(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val acceptsPrivacyPolicy: Boolean
)