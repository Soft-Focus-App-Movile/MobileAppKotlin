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

    data object Permissions : Route("permissions")

    // Main app routes (for future implementation)
    data object Home : Route("home")
    data object Tracking : Route("tracking")
    data object Crisis : Route("crisis")

    // Tracking routes - NEW
    data object Diary : Route("diary")
    data object CheckInForm : Route("check_in_form")
    data object Progress : Route("progress")

    // Library routes - General
    data object LibraryGeneralBrowse : Route("library_general_browse")
    data object LibraryGeneralDetail : Route("library_general_detail/{contentId}") {
        fun createRoute(contentId: String): String = "library_general_detail/$contentId"
    }

    // Search Psychologist routes
    data object SearchPsychologist : Route("search_psychologist")
    data object PsychologistDetail : Route("psychologist_detail/{psychologistId}") {
        fun createRoute(psychologistId: String): String = "psychologist_detail/$psychologistId"
    }

    // AI routes

    data object Library : Route("library")

    // General/Patient profile routes
    data object GeneralProfile : Route("general_profile")
    data object PatientProfile : Route("patient_profile")
    data object EditProfile : Route("edit_profile")

    // Psychologist profile routes
    data object PsychologistProfile : Route("psychologist_profile")
    data object PsychologistEditProfile : Route("psychologist_edit_profile")
    data object ProfessionalData : Route("professional_data")
    data object InvitationCode : Route("invitation_code")
    data object PsychologistPlan : Route("psychologist_plan")
    data object PsychologistStats : Route("psychologist_stats")

    data object CrisisAlerts : Route("crisis_alerts")

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