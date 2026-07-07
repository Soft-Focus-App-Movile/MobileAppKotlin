package com.softfocus.core.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

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
    data object ForgotPassword : Route("forgot_password")
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
    data object PrivacyPolicy : Route("privacy_policy")
    data object HelpSupport : Route("help_support")

    // Psychologist profile routes
    data object PsychologistProfile : Route("psychologist_profile")
    data object PsychologistEditProfile : Route("psychologist_edit_profile")
    data object ProfessionalData : Route("professional_data")
    data object InvitationCode : Route("invitation_code")
    data object PsychologistPlan : Route("psychologist_plan")
    data object PsychologistStats : Route("psychologist_stats")

    data object MyPlan : Route("my_plan")
    data object PatientPlan : Route("patient_plan")

    // --- RUTAS DE THERAPY (PSICÓLOGO) ---
    object PsychologistPatientList : Route("psychologist_patient_list")

    data object PsychologistPatientDetail : Route("psychologist_patient_detail/{patientId}/{relationshipId}/{startDate}?profilePhotoUrl={profilePhotoUrl}") {
        fun createRoute(
            patientId: String,
            relationshipId: String,
            startDate: String, // La obtenemos de PatientDirectory
            profilePhotoUrl: String?
        ): String {
            val charset = StandardCharsets.UTF_8.name()
            // Codificamos solo la fecha por si acaso
            val encodedDate = URLEncoder.encode(startDate, charset)
            val encodedPhotoUrl = profilePhotoUrl?.let { URLEncoder.encode(it, charset) } ?: "null"

            return "psychologist_patient_detail/$patientId/$relationshipId/$encodedDate?profilePhotoUrl=$encodedPhotoUrl"
        }
    }

    data object PsychologistPatientChat : Route("patient_chat/{patientId}/{relationshipId}/{patientName}?profilePhotoUrl={profilePhotoUrl}") {
        fun createRoute(
            patientId: String,
            relationshipId: String,
            patientName: String,
            profilePhotoUrl: String?
        ): String {
            val charset = StandardCharsets.UTF_8.name()
            val encodedName = URLEncoder.encode(patientName, charset)
            val encodedPhotoUrl = profilePhotoUrl?.let { URLEncoder.encode(it, charset) } ?: "null"

            return "patient_chat/$patientId/$relationshipId/$encodedName?profilePhotoUrl=$encodedPhotoUrl"
        }
    }

    data object PatientPsychologistChat : Route("psychologist_chat")

    data object PsychologistChatProfile : Route("psychologist_chat_profile")

    // Pantalla "Tareas" del paciente (mismas cards que ve el psicólogo)
    data object PatientTasks : Route("patient_tasks")

    // Call screen (Agora voice/video). callType = "Video" | "Audio".
    // - callId is set only when answering an incoming call; null/absent means an outgoing call.
    // - targetUserId is set when a psychologist calls a specific patient; patients omit it
    //   (the backend resolves their psychologist automatically).
    data object Call : Route("call/{callType}/{calleeName}?avatarUrl={avatarUrl}&callId={callId}&targetUserId={targetUserId}") {
        fun createRoute(
            callType: String,
            calleeName: String,
            avatarUrl: String?,
            targetUserId: String? = null
        ): String = build(callType, calleeName, avatarUrl, callId = null, targetUserId = targetUserId)

        fun createAnswerRoute(
            callId: String,
            callType: String,
            calleeName: String,
            avatarUrl: String? = null
        ): String = build(callType, calleeName, avatarUrl, callId = callId, targetUserId = null)

        private fun build(
            callType: String,
            calleeName: String,
            avatarUrl: String?,
            callId: String?,
            targetUserId: String?
        ): String {
            val charset = StandardCharsets.UTF_8.name()
            val encodedName = URLEncoder.encode(calleeName, charset)
            val encodedAvatar = avatarUrl?.let { URLEncoder.encode(it, charset) } ?: "null"
            return "call/$callType/$encodedName?avatarUrl=$encodedAvatar&callId=${callId ?: "null"}&targetUserId=${targetUserId ?: "null"}"
        }
    }

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
    data object EmotionDetection : Route("emotion_detection")

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
            ForgotPassword,
            AccountReview,
            AccountSuccess,
            AccountDenied
        )
    }
}