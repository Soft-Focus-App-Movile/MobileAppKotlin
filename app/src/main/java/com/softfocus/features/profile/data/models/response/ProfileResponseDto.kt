package com.softfocus.features.profile.data.models.response

import com.google.gson.annotations.SerializedName
import com.softfocus.features.auth.domain.models.User
import com.softfocus.features.auth.domain.models.UserType

/**
 * DTO for Profile API response
 * Matches backend UserProfileResource structure
 */
data class ProfileResponseDto(
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

    @SerializedName("isVerified")
    val isVerified: Boolean = false,

    @SerializedName("lastLogin")
    val lastLogin: String? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("updatedAt")
    val updatedAt: String? = null
) {
    /**
     * Converts DTO to domain model
     */
    fun toDomain(token: String?): User {
        return User(
            id = id,
            email = email,
            userType = try {
                UserType.valueOf(userType.uppercase())
            } catch (e: Exception) {
                UserType.GENERAL
            },
            isVerified = isVerified,
            token = token,
            fullName = fullName,
            firstName = firstName,
            lastName = lastName,
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
            isProfilePublic = isProfilePublic
        )
    }
}
