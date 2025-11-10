package com.softfocus.features.admin.domain.models

data class PsychologistDetail(
    val id: String,
    val email: String,
    val fullName: String,
    val firstName: String?,
    val lastName: String?,
    val userType: String,
    val phone: String?,
    val profileImageUrl: String?,
    val isActive: Boolean,
    val createdAt: String,
    val licenseNumber: String,
    val professionalCollege: String,
    val collegeRegion: String?,
    val university: String?,
    val graduationYear: Int?,
    val specialties: List<Specialty>,
    val yearsOfExperience: Int,
    val isVerified: Boolean,
    val verificationDate: String?,
    val verifiedBy: String?,
    val verificationNotes: String?,
    val licenseDocumentUrl: String?,
    val diplomaCertificateUrl: String?,
    val identityDocumentUrl: String?,
    val additionalCertificatesUrls: List<String>?
)

data class Specialty(
    val name: String
)
