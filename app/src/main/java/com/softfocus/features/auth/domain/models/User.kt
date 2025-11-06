package com.softfocus.features.auth.domain.models

/**
 * Represents a user in the Soft Focus platform.
 *
 * This is a domain entity containing only business logic data,
 * without any framework dependencies (Android, Retrofit, Room, etc.).
 *
 * @property id Unique identifier for the user
 * @property email User's email address
 * @property userType Type of user (GENERAL, PATIENT, PSYCHOLOGIST, ADMIN)
 * @property isVerified Whether the user's account has been verified by administrators
 * @property token Authentication token for API requests (nullable for pre-login states)
 * @property fullName User's full name (nullable until profile completion)
 * @property firstName User's first name
 * @property lastName User's last name
 * @property dateOfBirth User's date of birth (ISO 8601 format)
 * @property gender User's gender (Male, Female, Other, PreferNotToSay)
 * @property phone User's phone number
 * @property profileImageUrl URL to user's profile image
 * @property bio User's biography/description
 * @property country User's country
 * @property city User's city
 * @property interests List of user's interests
 * @property mentalHealthGoals List of user's mental health goals
 * @property emailNotifications Whether email notifications are enabled
 * @property pushNotifications Whether push notifications are enabled
 * @property isProfilePublic Whether the profile is public
 */
data class User(
    val id: String,
    val email: String,
    val userType: UserType,
    val isVerified: Boolean = false,
    val token: String? = null,
    val fullName: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
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
    val isProfilePublic: Boolean = false
)