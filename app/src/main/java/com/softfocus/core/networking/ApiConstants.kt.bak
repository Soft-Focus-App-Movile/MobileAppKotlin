package com.softfocus.core.networking

object ApiConstants {

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
        const val LOGIN = "auth/login"
        const val REGISTER = "auth/register"
        const val REGISTER_GENERAL = "auth/register/general"
        const val REGISTER_PSYCHOLOGIST = "auth/register/psychologist"
        const val SOCIAL_LOGIN = "auth/social-login"
        const val OAUTH = "auth/oauth"
        const val OAUTH_VERIFY = "auth/oauth/verify"
        const val OAUTH_COMPLETE_REGISTRATION = "auth/oauth/complete-registration"
        const val FORGOT_PASSWORD = "auth/forgot-password"
        const val RESET_PASSWORD = "auth/reset-password"
    }

    // User endpoints
    object Users {
        const val BASE = "users"
        const val PROFILE = "users/profile"
        const val BY_ID = "users/{id}"
        const val VERIFY_PSYCHOLOGIST = "users/{id}/verify"
        const val CHANGE_STATUS = "users/{id}/status"
        const val PSYCHOLOGIST_INVITATION_CODE = "users/psychologist/invitation-code"
        const val PSYCHOLOGIST_COMPLETE_PROFILE = "users/psychologist/complete"
        const val PSYCHOLOGIST_PROFESSIONAL_DATA = "users/psychologist/professional"
        const val PSYCHOLOGIST_VERIFICATION = "users/psychologist/verification"
        const val PSYCHOLOGIST_STATS = "users/psychologist/stats"
        const val PSYCHOLOGIST_PATIENT = "users/psychologist/patient/{id}"

        // Public psychologist directory endpoints
        const val PSYCHOLOGISTS_DIRECTORY = "users/psychologists/directory"
        const val PSYCHOLOGIST_DETAIL = "users/psychologists/{id}"

        fun getById(id: String) = BY_ID.replace("{id}", id)
        fun verifyPsychologist(id: String) = VERIFY_PSYCHOLOGIST.replace("{id}", id)
        fun changeStatus(id: String) = CHANGE_STATUS.replace("{id}", id)
        fun getPsychologistDetail(id: String) = PSYCHOLOGIST_DETAIL.replace("{id}", id)
    }

    // Therapy endpoints
    object Therapy {
        const val MY_RELATIONSHIP = "therapy/my-relationship"
        const val CONNECT = "therapy/connect"
        const val PATIENTS = "therapy/patients"
        const val DISCONNECT = "therapy/disconnect/{relationshipId}"

        fun disconnect(relationshipId: String) = DISCONNECT.replace("{relationshipId}", relationshipId)
    }

    object Chat {
        const val HISTORY = "chat/history"
        const val SEND = "chat/send"

        const val LAST_MESSAGE = "chat/last-received"
    }

    // AI endpoints
    object AI {
        const val CHAT_MESSAGE = "ai/chat/message"
        const val CHAT_USAGE = "ai/chat/usage"
        const val CHAT_SESSIONS = "ai/chat/sessions"
        const val CHAT_SESSION_MESSAGES = "ai/chat/sessions/{sessionId}/messages"
        const val EMOTION_ANALYZE = "ai/emotion/analyze"
        const val EMOTION_USAGE = "ai/emotion/usage"

        fun getChatSessionMessages(sessionId: String) = CHAT_SESSION_MESSAGES.replace("{sessionId}", sessionId)
    }

    // Tracking endpoints
    object Tracking {
        // Check-ins
        const val CHECK_INS = "tracking/check-ins"
        const val CHECK_IN_BY_ID = "tracking/check-ins/{id}"
        const val CHECK_IN_TODAY = "tracking/check-ins/today"
        const val PATIENT_CHECK_INS_HISTORY = "tracking/check-ins/patient/{userId}"

        // Emotional Calendar
        const val EMOTIONAL_CALENDAR = "tracking/emotional-calendar"
        const val EMOTIONAL_CALENDAR_BY_DATE = "tracking/emotional-calendar/{date}"

        // Dashboard
        const val DASHBOARD = "tracking/dashboard"

        // Helper functions for dynamic parameters
        fun getCheckInById(id: String) = CHECK_IN_BY_ID.replace("{id}", id)
        fun getEmotionalCalendarByDate(date: String) = EMOTIONAL_CALENDAR_BY_DATE.replace("{date}", date)
    }

    // Notification endpoints
    object Notifications {
        const val BASE = "notifications"
        const val BY_USER_ID = "notifications/{userId}"
        const val DETAIL = "notifications/detail/{notificationId}"
        const val MARK_AS_READ = "notifications/{notificationId}/read"
        const val MARK_ALL_READ = "notifications/read-all"
        const val DELETE = "notifications/{notificationId}"
        const val UNREAD_COUNT = "notifications/unread-count"

        fun getByUserId(userId: String) = BY_USER_ID.replace("{userId}", userId)
        fun getDetail(notificationId: String) = DETAIL.replace("{notificationId}", notificationId)
        fun markAsRead(notificationId: String) = MARK_AS_READ.replace("{notificationId}", notificationId)
        fun delete(notificationId: String) = DELETE.replace("{notificationId}", notificationId)
    }

    // Preferences endpoints
    object Preferences {
        const val BASE = "preferences"
        const val RESET = "preferences/reset"
    }

    // Crisis endpoints
    object Crisis {
        const val ALERT = "crisis/alert"
        const val ALERTS_BY_PATIENT = "crisis/alerts/patient"
        const val ALERTS_BY_PSYCHOLOGIST = "crisis/alerts"
        const val ALERT_BY_ID = "crisis/alerts/{id}"
        const val UPDATE_STATUS = "crisis/alerts/{id}/status"
        const val UPDATE_SEVERITY = "crisis/alerts/{id}/severity"

        fun getAlertById(id: String) = ALERT_BY_ID.replace("{id}", id)
        fun updateStatus(id: String) = UPDATE_STATUS.replace("{id}", id)
        fun updateSeverity(id: String) = UPDATE_SEVERITY.replace("{id}", id)
    }

    // Library endpoints
    object Library {
        const val BASE = "library"

        // Content Search endpoints
        const val SEARCH = "library/search"
        const val CONTENT_BY_ID = "library/{contentId}"

        // Favorites endpoints (General and Patient only)
        const val FAVORITES = "library/favorites"
        const val FAVORITE_BY_ID = "library/favorites/{favoriteId}"

        // Assignment endpoints - Patient side
        const val ASSIGNED_CONTENT = "library/assignments/assigned"
        const val COMPLETE_ASSIGNMENT = "library/assignments/assigned/{assignmentId}/complete"

        // Assignment endpoints - Psychologist side
        const val ASSIGNMENTS = "library/assignments"
        const val PSYCHOLOGIST_ASSIGNMENTS = "library/assignments/by-psychologist"

        // Recommendation endpoints
        const val RECOMMEND_PLACES = "library/recommendations/places"
        const val RECOMMEND_CONTENT = "library/recommendations/content"
        const val RECOMMEND_BY_EMOTION = "library/recommendations/emotion/{emotion}"

        // Helper functions for dynamic parameters
        fun getContentById(contentId: String) = CONTENT_BY_ID.replace("{contentId}", contentId)
        fun deleteFavorite(favoriteId: String) = FAVORITE_BY_ID.replace("{favoriteId}", favoriteId)
        fun completeAssignment(assignmentId: String) = COMPLETE_ASSIGNMENT.replace("{assignmentId}", assignmentId)
        fun recommendByEmotion(emotion: String) = RECOMMEND_BY_EMOTION.replace("{emotion}", emotion)
    }
}