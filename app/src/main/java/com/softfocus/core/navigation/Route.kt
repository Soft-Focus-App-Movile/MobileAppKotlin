package com.softfocus.core.navigation

/**
 * Sealed class representing all navigation routes in the app.
 *
 * Each route corresponds to a screen or destination in the navigation graph.
 * Using a sealed class ensures type safety and compile-time checking for routes.
 */
sealed class
Route(val path: String) {

    // Auth routes
    data object Splash : Route("splash")
    data object Login : Route("login")
    data object Register : Route("register")
    data object AccountReview : Route("account_review")
    data object AccountSuccess : Route("account_success")
    data object AccountDenied : Route("account_denied")

    // Main app routes (for future implementation)
    data object Home : Route("home")
    data object Profile : Route("profile")
    data object Tracking : Route("tracking")
    data object Crisis : Route("crisis")
    data object Library : Route("library")
    data object AIWelcome : Route("ai_welcome")
    data object AIChat : Route("ai_chat_screen/{initialMessage}?sessionId={sessionId}") {
        fun createRoute(initialMessage: String? = null, sessionId: String? = null): String {
            return if (sessionId != null) {
                "ai_chat_screen/null?sessionId=$sessionId"
            } else if (initialMessage != null) {
                val encodedMessage = java.net.URLEncoder.encode(initialMessage, "UTF-8")
                "ai_chat_screen/$encodedMessage"
            } else {
                "ai_chat_screen/null"
            }
        }
    }

    data object ConnectPsychologist : Route("connect_psychologist")

    data object AdminUsers : Route("admin_users")
    data object VerifyPsychologist : Route("verify_psychologist")

    data object Notifications : Route("notifications")
    data object NotificationPreferences : Route("notification_preferences")

    companion object {
        /**
         * Returns all auth-related routes.
         */
        val authRoutes = listOf(
            Splash,
            Login,
            Register,
            AccountReview,
            AccountSuccess,
            AccountDenied
        )
    }
}
