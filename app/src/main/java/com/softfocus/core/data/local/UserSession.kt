package com.softfocus.core.data.local

import android.content.Context
import com.softfocus.features.auth.domain.models.User
import com.softfocus.features.auth.domain.models.UserType

class UserSession(context: Context) {
    private val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveUser(user: User) {
        // Calculate token expiration time (7 days from now)
        val expirationTime = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L) // 7 days in milliseconds

        prefs.edit()
            .putString(KEY_USER_ID, user.id)
            .putString(KEY_EMAIL, user.email)
            .putString(KEY_USER_TYPE, user.userType.name)
            .putBoolean(KEY_IS_VERIFIED, user.isVerified)
            .putString(KEY_TOKEN, user.token)
            .putLong(KEY_TOKEN_EXPIRATION, expirationTime)
            .putString(KEY_FULL_NAME, user.fullName)
            .putString(KEY_FIRST_NAME, user.firstName)
            .putString(KEY_LAST_NAME, user.lastName)
            .putString(KEY_DATE_OF_BIRTH, user.dateOfBirth)
            .putString(KEY_GENDER, user.gender)
            .putString(KEY_PHONE, user.phone)
            .putString(KEY_PROFILE_IMAGE_URL, user.profileImageUrl)
            .putString(KEY_BIO, user.bio)
            .putString(KEY_COUNTRY, user.country)
            .putString(KEY_CITY, user.city)
            .putStringSet(KEY_INTERESTS, user.interests?.toSet())
            .putStringSet(KEY_MENTAL_HEALTH_GOALS, user.mentalHealthGoals?.toSet())
            .putBoolean(KEY_EMAIL_NOTIFICATIONS, user.emailNotifications)
            .putBoolean(KEY_PUSH_NOTIFICATIONS, user.pushNotifications)
            .putBoolean(KEY_IS_PROFILE_PUBLIC, user.isProfilePublic)
            .apply()
    }

    fun getUser(): User? {
        val id = prefs.getString(KEY_USER_ID, null) ?: return null
        val email = prefs.getString(KEY_EMAIL, null) ?: return null
        val userTypeString = prefs.getString(KEY_USER_TYPE, null) ?: return null
        val userType = try {
            UserType.valueOf(userTypeString)
        } catch (e: Exception) {
            UserType.GENERAL
        }

        return User(
            id = id,
            email = email,
            userType = userType,
            isVerified = prefs.getBoolean(KEY_IS_VERIFIED, false),
            token = prefs.getString(KEY_TOKEN, null),
            fullName = prefs.getString(KEY_FULL_NAME, null),
            firstName = prefs.getString(KEY_FIRST_NAME, null),
            lastName = prefs.getString(KEY_LAST_NAME, null),
            dateOfBirth = prefs.getString(KEY_DATE_OF_BIRTH, null),
            gender = prefs.getString(KEY_GENDER, null),
            phone = prefs.getString(KEY_PHONE, null),
            profileImageUrl = prefs.getString(KEY_PROFILE_IMAGE_URL, null),
            bio = prefs.getString(KEY_BIO, null),
            country = prefs.getString(KEY_COUNTRY, null),
            city = prefs.getString(KEY_CITY, null),
            interests = prefs.getStringSet(KEY_INTERESTS, null)?.toList(),
            mentalHealthGoals = prefs.getStringSet(KEY_MENTAL_HEALTH_GOALS, null)?.toList(),
            emailNotifications = prefs.getBoolean(KEY_EMAIL_NOTIFICATIONS, true),
            pushNotifications = prefs.getBoolean(KEY_PUSH_NOTIFICATIONS, true),
            isProfilePublic = prefs.getBoolean(KEY_IS_PROFILE_PUBLIC, false)
        )
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    /**
     * Checks if the stored token has expired.
     * @return true if token is expired, false if still valid
     */
    fun isTokenExpired(): Boolean {
        val expirationTime = prefs.getLong(KEY_TOKEN_EXPIRATION, 0L)
        if (expirationTime == 0L) {
            // No expiration time stored, consider expired for safety
            return true
        }
        return System.currentTimeMillis() >= expirationTime
    }

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_USER_TYPE = "user_type"
        private const val KEY_IS_VERIFIED = "user_is_verified"
        private const val KEY_TOKEN = "user_token"
        private const val KEY_TOKEN_EXPIRATION = "user_token_expiration"
        private const val KEY_FULL_NAME = "user_full_name"
        private const val KEY_FIRST_NAME = "user_first_name"
        private const val KEY_LAST_NAME = "user_last_name"
        private const val KEY_DATE_OF_BIRTH = "user_date_of_birth"
        private const val KEY_GENDER = "user_gender"
        private const val KEY_PHONE = "user_phone"
        private const val KEY_PROFILE_IMAGE_URL = "user_profile_image_url"
        private const val KEY_BIO = "user_bio"
        private const val KEY_COUNTRY = "user_country"
        private const val KEY_CITY = "user_city"
        private const val KEY_INTERESTS = "user_interests"
        private const val KEY_MENTAL_HEALTH_GOALS = "user_mental_health_goals"
        private const val KEY_EMAIL_NOTIFICATIONS = "user_email_notifications"
        private const val KEY_PUSH_NOTIFICATIONS = "user_push_notifications"
        private const val KEY_IS_PROFILE_PUBLIC = "user_is_profile_public"
    }
}
