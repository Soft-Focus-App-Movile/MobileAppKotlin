package com.softfocus.features.search.domain.models

data class Psychologist(
    val id: String,
    val fullName: String,
    val profileImageUrl: String?,
    val professionalBio: String?,
    val specialties: List<String>,
    val yearsOfExperience: Int,
    val city: String?,
    val languages: List<String>?,
    val isAcceptingNewPatients: Boolean,
    val averageRating: Double?,
    val totalReviews: Int,
    val allowsDirectMessages: Boolean,
    val targetAudience: List<String>?,
    val email: String?,
    val phone: String?,
    val whatsApp: String?,
    val corporateEmail: String?,
    // Additional profile fields
    val university: String?,
    val graduationYear: Int?,
    val degree: String?,
    val licenseNumber: String?,
    val professionalCollege: String?,
    val collegeRegion: String?
)
