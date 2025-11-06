package com.softfocus.features.profile.domain.models

/**
 * Domain model for assigned psychologist information shown in patient profile
 */
data class AssignedPsychologist(
    val id: String,
    val fullName: String,
    val profileImageUrl: String?,
    val professionalBio: String?,
    val specialties: List<String>?
)
