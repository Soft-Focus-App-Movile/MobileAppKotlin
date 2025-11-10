package com.softfocus.features.search.data.models.response

import com.google.gson.annotations.SerializedName
import com.softfocus.features.search.domain.models.Psychologist

data class PsychologistResponseDto(
    @SerializedName("id") val id: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("profileImageUrl") val profileImageUrl: String?,
    @SerializedName("professionalBio") val professionalBio: String?,
    @SerializedName("specialties") val specialties: List<String>,
    @SerializedName("yearsOfExperience") val yearsOfExperience: Int,
    @SerializedName("city") val city: String?,
    @SerializedName("languages") val languages: List<String>?,
    @SerializedName("isAcceptingNewPatients") val isAcceptingNewPatients: Boolean,
    @SerializedName("averageRating") val averageRating: Double?,
    @SerializedName("totalReviews") val totalReviews: Int,
    @SerializedName("allowsDirectMessages") val allowsDirectMessages: Boolean,
    @SerializedName("targetAudience") val targetAudience: List<String>?,
    @SerializedName("email") val email: String?,
    @SerializedName("phone") val phone: String?,
    @SerializedName("whatsApp") val whatsApp: String?,
    @SerializedName("corporateEmail") val corporateEmail: String?,
    // Additional profile fields
    @SerializedName("university") val university: String?,
    @SerializedName("graduationYear") val graduationYear: Int?,
    @SerializedName("degree") val degree: String?,
    @SerializedName("licenseNumber") val licenseNumber: String?,
    @SerializedName("professionalCollege") val professionalCollege: String?,
    @SerializedName("collegeRegion") val collegeRegion: String?
) {
    fun toDomain(): Psychologist {
        return Psychologist(
            id = id,
            fullName = fullName,
            profileImageUrl = profileImageUrl,
            professionalBio = professionalBio,
            specialties = specialties,
            yearsOfExperience = yearsOfExperience,
            city = city,
            languages = languages,
            isAcceptingNewPatients = isAcceptingNewPatients,
            averageRating = averageRating,
            totalReviews = totalReviews,
            allowsDirectMessages = allowsDirectMessages,
            targetAudience = targetAudience,
            email = email,
            phone = phone,
            whatsApp = whatsApp,
            corporateEmail = corporateEmail,
            university = university,
            graduationYear = graduationYear,
            degree = degree,
            licenseNumber = licenseNumber,
            professionalCollege = professionalCollege,
            collegeRegion = collegeRegion
        )
    }
}

data class PsychologistDirectoryResponseDto(
    @SerializedName("psychologists") val psychologists: List<PsychologistResponseDto>,
    @SerializedName("pagination") val pagination: PaginationDto,
    @SerializedName("filters") val filters: FiltersDto?
)

data class PaginationDto(
    @SerializedName("page") val page: Int,
    @SerializedName("pageSize") val pageSize: Int,
    @SerializedName("totalCount") val totalCount: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("hasNextPage") val hasNextPage: Boolean,
    @SerializedName("hasPreviousPage") val hasPreviousPage: Boolean
)

data class FiltersDto(
    @SerializedName("specialties") val specialties: List<String>?,
    @SerializedName("city") val city: String?,
    @SerializedName("minRating") val minRating: Double?,
    @SerializedName("isAcceptingNewPatients") val isAcceptingNewPatients: Boolean?,
    @SerializedName("languages") val languages: List<String>?,
    @SerializedName("searchTerm") val searchTerm: String?,
    @SerializedName("sortBy") val sortBy: String?,
    @SerializedName("sortDescending") val sortDescending: Boolean?
)
