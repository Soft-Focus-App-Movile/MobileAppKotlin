package com.softfocus.core.networking

object ApiConstants {
    // 10.0.2.2 is the special IP for Android emulator to access host machine's localhost
    const val BASE_URL = "http://98.90.172.251:5000/api/v1/"

    // Google OAuth Client IDs
    // Web Client ID - Used for serverClientId in Credential Manager and backend verification
    const val GOOGLE_WEB_CLIENT_ID = "456468181765-quas8eebf9rfjg33ovhn42efqu4uqcag.apps.googleusercontent.com"

    // Android Client ID - Registered with SHA-1 fingerprint
    const val GOOGLE_ANDROID_CLIENT_ID = "456468181765-c6kjl3vsdc0a7d6mqjfgg3aihnuj23tb.apps.googleusercontent.com"

    // For Credential Manager API, we use the WEB client ID as serverClientId
    const val GOOGLE_SERVER_CLIENT_ID = GOOGLE_WEB_CLIENT_ID

    // Auth endpoints
    object Auth {
        const val REGISTER = "auth/register"
        const val LOGIN = "auth/login"
        const val OAUTH = "auth/oauth"
        const val FORGOT_PASSWORD = "auth/forgot-password"
        const val RESET_PASSWORD = "auth/reset-password"
    }

    // User endpoints
    object Users {
        const val PROFILE = "users/profile"
    }

    // AI endpoints
    object AI {
        const val CHAT_MESSAGE = "ai/chat/message"
        const val CHAT_USAGE = "ai/chat/usage"
        const val EMOTION_ANALYZE = "ai/emotion/analyze"
        const val EMOTION_USAGE = "ai/emotion/usage"
    }

    object Notifications {
        const val BASE = "notifications"
        const val BY_ID = "notifications/{id}"
        const val MARK_AS_READ = "notifications/{id}/read"
        const val MARK_ALL_READ = "notifications/read-all"
        const val UNREAD_COUNT = "notifications/unread-count"
        const val PREFERENCES = "notifications/preferences"
        const val PREFERENCES_RESET = "notifications/preferences/reset"
    }
}
