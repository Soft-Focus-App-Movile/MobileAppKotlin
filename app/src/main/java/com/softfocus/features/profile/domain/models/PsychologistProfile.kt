package com.softfocus.features.profile.domain.models

import java.time.LocalDateTime

/**
 * Complete psychologist profile domain model
 * Contains all user data, verification data, and professional data
 */
data class PsychologistProfile(
    // Basic User Data
    val id: String,
    val email: String,
    val fullName: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val userType: String,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val phone: String? = null,
    val profileImageUrl: String? = null,
    val bio: String? = null,
    val country: String? = null,
    val city: String? = null,
    val interests: List<String>? = null,
    val mentalHealthGoals: List<String>? = null,
    val emailNotifications: Boolean = true,
    val pushNotifications: Boolean = true,
    val isProfilePublic: Boolean = false,
    val isActive: Boolean = true,
    val lastLogin: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,

    // Verification Data
    val licenseNumber: String,
    val professionalCollege: String,
    val collegeRegion: String? = null,
    val specialties: List<String>,
    val yearsOfExperience: Int,
    val university: String? = null,
    val graduationYear: Int? = null,
    val degree: String? = null,
    val licenseDocumentUrl: String? = null,
    val diplomaCertificateUrl: String? = null,
    val identityDocumentUrl: String? = null,
    val additionalCertificatesUrls: List<String>? = null,
    val isVerified: Boolean = false,
    val verificationDate: String? = null,
    val verifiedBy: String? = null,
    val verificationNotes: String? = null,

    // Professional Data
    val professionalBio: String? = null,
    val isAcceptingNewPatients: Boolean = true,
    val maxPatientsCapacity: Int? = null,
    val currentPatientsCount: Int? = null,
    val targetAudience: List<String>? = null,
    val languages: List<String>? = null,
    val businessName: String? = null,
    val businessAddress: String? = null,
    val bankAccount: String? = null,
    val paymentMethods: String? = null,
    val currency: String? = null,
    val isProfileVisibleInDirectory: Boolean = true,
    val allowsDirectMessages: Boolean = true,
    val averageRating: Double? = null,
    val totalReviews: Int? = null
)
