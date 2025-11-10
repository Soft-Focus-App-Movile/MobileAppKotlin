package com.softfocus.core.networking

import android.content.Context
import android.util.Log
import com.softfocus.core.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that automatically handles 401 Unauthorized responses.
 * When a 401 error is detected (expired or invalid token), it:
 * 1. Clears all user session data
 * 2. Logs the user out completely
 *
 * Navigation to login screen should be handled by the UI layer detecting
 * the cleared session state.
 */
class Auth401Interceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        // Check if response is 401 Unauthorized
        if (response.code == 401) {
            Log.w(TAG, "401 Unauthorized detected - Token expired or invalid")
            Log.w(TAG, "Clearing all session data and logging out user")

            // Clear all session data (SharedPreferences, static tokens, cache, etc.)
            try {
                SessionManager.logout(context)
                Log.i(TAG, "Session data cleared successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error clearing session data", e)
            }

            // Note: Navigation to login screen will be handled automatically
            // when the app detects there's no active session
        }

        return response
    }

    companion object {
        private const val TAG = "Auth401Interceptor"
    }
}
