package com.softfocus.features.therapy.data.models.response

import com.google.gson.annotations.SerializedName
import com.softfocus.features.therapy.domain.models.PatientProfile

/**
 * DTO para la respuesta del endpoint: users/psychologist/patient/{id}
 * Mapea el 'UserProfileResource' del backend.
 */
data class PatientProfileResponseDto(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("userType") val userType: String, // Asumo que el Enum UserType se serializa como String
    @SerializedName("dateOfBirth") val dateOfBirth: String?, // Se recibe como String
    @SerializedName("gender") val gender: String?,
    @SerializedName("phone") val phone: String?,
    @SerializedName("profileImageUrl") val profileImageUrl: String?,
    @SerializedName("bio") val bio: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("interests") val interests: List<String>?,
    @SerializedName("mentalHealthGoals") val mentalHealthGoals: List<String>?,
    @SerializedName("emailNotifications") val emailNotifications: Boolean,
    @SerializedName("pushNotifications") val pushNotifications: Boolean,
    @SerializedName("isProfilePublic") val isProfilePublic: Boolean,
    @SerializedName("isActive") val isActive: Boolean,
    @SerializedName("lastLogin") val lastLogin: String?, // Se recibe como String
    @SerializedName("createdAt") val createdAt: String, // Se recibe como String
    @SerializedName("updatedAt") val updatedAt: String  // Se recibe como String
)

/**
 * Convierte el DTO de la red a un modelo de dominio limpio.
 */
fun PatientProfileResponseDto.toDomain(): PatientProfile {
    return PatientProfile(
        id = this.id,
        fullName = this.fullName,
        profilePhotoUrl = this.profileImageUrl ?: "",
        dateOfBirth = this.dateOfBirth
    )
}