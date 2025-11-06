package com.softfocus.features.profile.data.models.response

import com.google.gson.annotations.SerializedName
import com.softfocus.features.profile.domain.models.PsychologistProfile

/**
 * DTO for Complete Psychologist Profile API response
 * Matches backend PsychologistCompleteProfileResource structure
 */
data class PsychologistCompleteProfileResponseDto(
    // Basic User Data
    @SerializedName("id")
    val id: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("fullName")
    val fullName: String,

    @SerializedName("firstName")
    val firstName: String? = null,

    @SerializedName("lastName")
    val lastName: String? = null,

    @SerializedName("userType")
    val userType: String,

    @SerializedName("dateOfBirth")
    val dateOfBirth: String? = null,

    @SerializedName("gender")
    val gender: String? = null,

    @SerializedName("phone")
    val phone: String? = null,

    @SerializedName("profileImageUrl")
    val profileImageUrl: String? = null,

    @SerializedName("bio")
    val bio: String? = null,

    @SerializedName("country")
    val country: String? = null,

    @SerializedName("city")
    val city: String? = null,

    @SerializedName("interests")
    val interests: List<String>? = null,

    @SerializedName("mentalHealthGoals")
    val mentalHealthGoals: List<String>? = null,

    @SerializedName("emailNotifications")
    val emailNotifications: Boolean = true,

    @SerializedName("pushNotifications")
    val pushNotifications: Boolean = true,

    @SerializedName("isProfilePublic")
    val isProfilePublic: Boolean = false,

    @SerializedName("isActive")
    val isActive: Boolean = true,

    @SerializedName("lastLogin")
    val lastLogin: String? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("updatedAt")
    val updatedAt: String? = null,

    // Verification Data
    @SerializedName("licenseNumber")
    val licenseNumber: String,

    @SerializedName("professionalCollege")
    val professionalCollege: String,

    @SerializedName("collegeRegion")
    val collegeRegion: String? = null,

    @SerializedName("specialties")
    val specialties: List<String>,

    @SerializedName("yearsOfExperience")
    val yearsOfExperience: Int,

    @SerializedName("university")
    val university: String? = null,

    @SerializedName("graduationYear")
    val graduationYear: Int? = null,

    @SerializedName("degree")
    val degree: String? = null,

    @SerializedName("licenseDocumentUrl")
    val licenseDocumentUrl: String? = null,

    @SerializedName("diplomaCertificateUrl")
    val diplomaCertificateUrl: String? = null,

    @SerializedName("identityDocumentUrl")
    val identityDocumentUrl: String? = null,

    @SerializedName("additionalCertificatesUrls")
    val additionalCertificatesUrls: List<String>? = null,

    @SerializedName("isVerified")
    val isVerified: Boolean = false,

    @SerializedName("verificationDate")
    val verificationDate: String? = null,

    @SerializedName("verifiedBy")
    val verifiedBy: String? = null,

    @SerializedName("verificationNotes")
    val verificationNotes: String? = null,

    // Professional Data
    @SerializedName("professionalBio")
    val professionalBio: String? = null,

    @SerializedName("isAcceptingNewPatients")
    val isAcceptingNewPatients: Boolean = true,

    @SerializedName("maxPatientsCapacity")
    val maxPatientsCapacity: Int? = null,

    @SerializedName("currentPatientsCount")
    val currentPatientsCount: Int? = null,

    @SerializedName("targetAudience")
    val targetAudience: List<String>? = null,

    @SerializedName("languages")
    val languages: List<String>? = null,

    @SerializedName("businessName")
    val businessName: String? = null,

    @SerializedName("businessAddress")
    val businessAddress: String? = null,

    @SerializedName("bankAccount")
    val bankAccount: String? = null,

    @SerializedName("paymentMethods")
    val paymentMethods: String? = null,

    @SerializedName("currency")
    val currency: String? = null,

    @SerializedName("isProfileVisibleInDirectory")
    val isProfileVisibleInDirectory: Boolean = true,

    @SerializedName("allowsDirectMessages")
    val allowsDirectMessages: Boolean = true,

    @SerializedName("averageRating")
    val averageRating: Double? = null,

    @SerializedName("totalReviews")
    val totalReviews: Int? = null
) {
    /**
     * Converts DTO to domain model
     */
    fun toDomain(): PsychologistProfile {
        return PsychologistProfile(
            id = id,
            email = email,
            fullName = fullName,
            firstName = firstName,
            lastName = lastName,
            userType = userType,
            dateOfBirth = dateOfBirth,
            gender = gender,
            phone = phone,
            profileImageUrl = profileImageUrl,
            bio = bio,
            country = country,
            city = city,
            interests = interests,
            mentalHealthGoals = mentalHealthGoals,
            emailNotifications = emailNotifications,
            pushNotifications = pushNotifications,
            isProfilePublic = isProfilePublic,
            isActive = isActive,
            lastLogin = lastLogin,
            createdAt = createdAt,
            updatedAt = updatedAt,
            licenseNumber = licenseNumber,
            professionalCollege = professionalCollege,
            collegeRegion = collegeRegion,
            specialties = specialties,
            yearsOfExperience = yearsOfExperience,
            university = university,
            graduationYear = graduationYear,
            degree = degree,
            licenseDocumentUrl = licenseDocumentUrl,
            diplomaCertificateUrl = diplomaCertificateUrl,
            identityDocumentUrl = identityDocumentUrl,
            additionalCertificatesUrls = additionalCertificatesUrls,
            isVerified = isVerified,
            verificationDate = verificationDate,
            verifiedBy = verifiedBy,
            verificationNotes = verificationNotes,
            professionalBio = professionalBio,
            isAcceptingNewPatients = isAcceptingNewPatients,
            maxPatientsCapacity = maxPatientsCapacity,
            currentPatientsCount = currentPatientsCount,
            targetAudience = targetAudience,
            languages = languages,
            businessName = businessName,
            businessAddress = businessAddress,
            bankAccount = bankAccount,
            paymentMethods = paymentMethods,
            currency = currency,
            isProfileVisibleInDirectory = isProfileVisibleInDirectory,
            allowsDirectMessages = allowsDirectMessages,
            averageRating = averageRating,
            totalReviews = totalReviews
        )
    }
}
