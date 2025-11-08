package com.softfocus.core.utils

import android.content.Context
import coil3.imageLoader
import com.softfocus.core.data.local.LocalUserDataSource
import com.softfocus.core.data.local.UserSession
import com.softfocus.features.admin.presentation.di.AdminPresentationModule
import com.softfocus.features.auth.domain.models.User
import com.softfocus.features.library.data.di.LibraryDataModule
import com.softfocus.features.notifications.presentation.di.NotificationPresentationModule
import com.softfocus.features.psychologist.presentation.di.PsychologistPresentationModule
import com.softfocus.features.therapy.presentation.di.TherapyPresentationModule

/**
 * Utility class to manage user sessions.
 * Handles login persistence (like Instagram/Facebook) and logout cleanup.
 */
object SessionManager {

    /**
     * Clears all user session data from local storage and memory.
     * This should be called when user explicitly logs out or session expires.
     */
    fun logout(context: Context) {
        // Clear UserSession (user info, token, etc.)
        val userSession = UserSession(context)
        userSession.clear()

        // Clear LocalUserDataSource (therapeutic relationship status)
        val localUserDataSource = LocalUserDataSource(context)
        localUserDataSource.clear()

        AdminPresentationModule.clearAuthToken()
        NotificationPresentationModule.clearAuthToken()
        PsychologistPresentationModule.clearAuthToken()

        // Clear library repository singleton
        LibraryDataModule.clearRepository()

        // Clear Coil image cache (memory and disk)
        clearImageCache(context)
    }

    /**
     * Clears Coil image cache from memory and disk.
     */
    private fun clearImageCache(context: Context) {
        try {
            val imageLoader = context.imageLoader
            imageLoader.memoryCache?.clear()
            imageLoader.diskCache?.clear()
        } catch (e: Exception) {
            // Ignore if Coil is not initialized yet
        }
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
