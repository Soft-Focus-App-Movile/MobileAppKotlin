package com.softfocus.features.auth.data.models.request

import okhttp3.MultipartBody
import okhttp3.RequestBody

/**
 * Request DTO for psychologist registration.
 * Endpoint: POST /api/v1/auth/register/psychologist
 * Content-Type: multipart/form-data
 */
data class RegisterPsychologistRequestDto(
    val firstName: RequestBody,
    val lastName: RequestBody,
    val email: RequestBody,
    val password: RequestBody,
    val professionalLicense: RequestBody,
    val yearsOfExperience: RequestBody,
    val collegiateRegion: RequestBody,
    val university: RequestBody,
    val graduationYear: RequestBody,
    val acceptsPrivacyPolicy: RequestBody,
    val licenseDocument: MultipartBody.Part,
    val diplomaDocument: MultipartBody.Part,
    val dniDocument: MultipartBody.Part,
    val specialties: RequestBody? = null, // comma-separated string, optional
    val certificationDocuments: List<MultipartBody.Part>? = null // optional
)
