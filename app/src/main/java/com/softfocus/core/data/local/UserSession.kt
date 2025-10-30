package com.softfocus.core.data.local

import android.content.Context
import com.softfocus.features.auth.domain.models.User
import com.softfocus.features.auth.domain.models.UserType

class UserSession(context: Context) {
    private val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveUser(user: User) {
        prefs.edit()
            .putString(KEY_USER_ID, user.id)
            .putString(KEY_EMAIL, user.email)
            .putString(KEY_USER_TYPE, user.userType.name)
            .putBoolean(KEY_IS_VERIFIED, user.isVerified)
            .putString(KEY_TOKEN, user.token)
            .putString(KEY_FULL_NAME, user.fullName)
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
            fullName = prefs.getString(KEY_FULL_NAME, null)
        )
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_USER_TYPE = "user_type"
        private const val KEY_IS_VERIFIED = "user_is_verified"
        private const val KEY_TOKEN = "user_token"
        private const val KEY_FULL_NAME = "user_full_name"
    }
}
