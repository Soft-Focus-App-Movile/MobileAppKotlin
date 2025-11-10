package com.softfocus.features.auth.data.models.request

data class RegisterRequestDto(
    val email: String,
    val password: String,
    val fullName: String,
    val userType: String,
    // Campos adicionales para psicólogo (opcionales)
    val professionalLicense: String? = null,
    val specialties: List<String>? = null  // Backend expects string array for registration
)
