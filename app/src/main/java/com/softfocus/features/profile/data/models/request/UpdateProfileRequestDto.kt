package com.softfocus.features.profile.data.models.request

import com.google.gson.annotations.SerializedName

/**
 * DTO for Update Profile API request
 * Matches backend UpdateUserProfileResource structure
 */
data class UpdateProfileRequestDto(
    @SerializedName("FullName")
    val fullName: String,

    @SerializedName("FirstName")
    val firstName: String?,

    @SerializedName("LastName")
    val lastName: String?,

    @SerializedName("DateOfBirth")
    val dateOfBirth: String? = null,

    @SerializedName("Gender")
    val gender: String? = null,

    @SerializedName("Phone")
    val phone: String? = null,

    @SerializedName("Bio")
    val bio: String? = null,

    @SerializedName("Country")
    val country: String? = null,

    @SerializedName("City")
    val city: String? = null,

    @SerializedName("Interests")
    val interests: List<String>? = null,

    @SerializedName("MentalHealthGoals")
    val mentalHealthGoals: List<String>? = null,

    @SerializedName("EmailNotifications")
    val emailNotifications: Boolean? = null,

    @SerializedName("PushNotifications")
    val pushNotifications: Boolean? = null,

    @SerializedName("IsProfilePublic")
    val isProfilePublic: Boolean? = null,

    @SerializedName("ProfileImageUrl")
    val profileImageUrl: String? = null
)
