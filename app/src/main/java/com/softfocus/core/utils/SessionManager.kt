package com.softfocus.core.utils

import android.content.Context
import com.softfocus.core.data.local.LocalUserDataSource
import com.softfocus.core.data.local.UserSession
import com.softfocus.features.auth.domain.models.User
import com.softfocus.features.auth.domain.models.UserType

/**
 * Utility class to manage user sessions.
 * Handles login persistence (like Instagram/Facebook) and logout cleanup.
 */
object SessionManager {

    /**
     * Clears all user session data from local storage.
     * This should be called when user explicitly logs out.
     */
    fun logout(context: Context) {
        // Clear UserSession (user info, token, etc.)
        val userSession = UserSession(context)
        userSession.clear()

        // Clear LocalUserDataSource (therapeutic relationship status)
        val localUserDataSource = LocalUserDataSource(context)
        localUserDataSource.clear()

        // Note: Auth tokens in modules are static and will be overwritten on next login
    }

    /**
     * Checks if there's an active session (user logged in).
     * Used for auto-login functionality.
     */
    fun hasActiveSession(context: Context): Boolean {
        val userSession = UserSession(context)
        return userSession.getUser() != null
    }

    /**
     * Gets the current logged-in user, if any.
     */
    fun getCurrentUser(context: Context): User? {
        val userSession = UserSession(context)
        return userSession.getUser()
    }
}
