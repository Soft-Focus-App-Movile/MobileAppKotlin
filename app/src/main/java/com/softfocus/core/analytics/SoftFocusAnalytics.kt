package com.softfocus.core.analytics

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics

/**
 * Punto de acceso central a Firebase Analytics.
 *
 * Es un singleton (object) para que pueda usarse tanto desde ViewModels creados
 * manualmente (AIPresentationModule, TherapyPresentationModule, etc.) como desde
 * los que usan Hilt. Firebase se auto-inicializa con google-services.json, por lo
 * que no se necesita Context.
 */
object SoftFocusAnalytics {

    private val firebaseAnalytics: FirebaseAnalytics by lazy { Firebase.analytics }

    fun logEvent(name: String, params: Bundle.() -> Unit = {}) {
        firebaseAnalytics.logEvent(name, Bundle().apply(params))
    }

    // ============================================================
    // AUTH
    // ============================================================

    fun logLogin(method: String, userType: String?) {
        logEvent(FirebaseAnalytics.Event.LOGIN) {
            putString(FirebaseAnalytics.Param.METHOD, method)
            userType?.let { putString("user_type", it) }
        }
    }

    fun logLoginFailed(method: String, reason: String?) {
        logEvent("login_failed") {
            putString(FirebaseAnalytics.Param.METHOD, method)
            putString("reason", reason?.take(100) ?: "unknown")
        }
    }

    fun logSignUp(method: String, userType: String) {
        logEvent(FirebaseAnalytics.Event.SIGN_UP) {
            putString(FirebaseAnalytics.Param.METHOD, method)
            putString("user_type", userType)
        }
    }

    /** Asocia los eventos siguientes al usuario logueado. */
    fun setUser(userId: String?, userType: String?) {
        firebaseAnalytics.setUserId(userId)
        firebaseAnalytics.setUserProperty("user_type", userType)
    }

    fun clearUser() {
        firebaseAnalytics.setUserId(null)
        firebaseAnalytics.setUserProperty("user_type", null)
    }

    // ============================================================
    // CHAT CON IA
    // ============================================================

    fun logAiChatMessageSent(sessionId: String?) {
        logEvent("ai_chat_message_sent") {
            putString("session_id", sessionId ?: "new_session")
        }
    }

    fun logAiChatResponseReceived(sessionId: String?) {
        logEvent("ai_chat_response_received") {
            putString("session_id", sessionId ?: "unknown")
        }
    }

    fun logAiChatError(reason: String?) {
        logEvent("ai_chat_error") {
            putString("reason", reason?.take(100) ?: "unknown")
        }
    }

    fun logAiChatNewConversation() {
        logEvent("ai_chat_new_conversation")
    }

    fun logAiChatSessionLoaded(sessionId: String) {
        logEvent("ai_chat_session_loaded") {
            putString("session_id", sessionId)
        }
    }

    // ============================================================
    // DETECCIÓN DE EMOCIONES (IA)
    // ============================================================

    fun logEmotionAnalysisCompleted(dominantEmotion: String?) {
        logEvent("emotion_analysis_completed") {
            putString("dominant_emotion", dominantEmotion ?: "unknown")
        }
    }

    fun logEmotionAnalysisFailed(reason: String?) {
        logEvent("emotion_analysis_failed") {
            putString("reason", reason?.take(100) ?: "unknown")
        }
    }

    // ============================================================
    // CHAT CON PSICÓLOGO (ambos lados)
    // ============================================================

    fun logTherapyChatOpened(role: String) {
        logEvent("therapy_chat_opened") {
            putString("role", role) // patient | psychologist
        }
    }

    fun logTherapyChatMessageSent(role: String) {
        logEvent("therapy_chat_message_sent") {
            putString("role", role)
        }
    }

    // ============================================================
    // LLAMADAS (Agora)
    // ============================================================

    fun logCallInitiated(callType: String) {
        logEvent("call_initiated") {
            putString("call_type", callType) // Video | Audio
        }
    }

    fun logCallAnswered(callType: String) {
        logEvent("call_answered") {
            putString("call_type", callType)
        }
    }

    fun logCallEnded(callType: String, durationSeconds: Long?) {
        logEvent("call_ended") {
            putString("call_type", callType)
            durationSeconds?.let { putLong("duration_seconds", it) }
        }
    }

    fun logCallFailed(callType: String, reason: String?) {
        logEvent("call_failed") {
            putString("call_type", callType)
            putString("reason", reason?.take(100) ?: "unknown")
        }
    }

    // ============================================================
    // BIBLIOTECA: RECOMENDACIONES Y TAREAS
    // ============================================================

    /** Recomendaciones de contenido mostradas según la emoción del check-in diario. */
    fun logRecommendationsShown(emotion: String) {
        logEvent("recommendations_shown") {
            putString("emotion", emotion)
        }
    }

    /** El paciente abre su lista de tareas (contenido asignado). */
    fun logAssignmentsViewed(pendingCount: Int, completedCount: Int) {
        logEvent("assignments_viewed") {
            putLong("pending_count", pendingCount.toLong())
            putLong("completed_count", completedCount.toLong())
        }
    }

    /** El paciente marca una tarea como completada. */
    fun logAssignmentCompleted(assignmentId: String) {
        logEvent("assignment_completed") {
            putString("assignment_id", assignmentId)
        }
    }

    /** El psicólogo asigna contenido a uno o más pacientes. */
    fun logContentAssigned(patientsCount: Int, contentCount: Int, successCount: Int, errorCount: Int) {
        logEvent("content_assigned") {
            putLong("patients_count", patientsCount.toLong())
            putLong("content_count", contentCount.toLong())
            putLong("success_count", successCount.toLong())
            putLong("error_count", errorCount.toLong())
        }
    }

    // ============================================================
    // TRACKING / CALENDARIO EMOCIONAL
    // ============================================================

    fun logCheckInCreated(emotionalLevel: Int, energyLevel: Int) {
        logEvent("check_in_created") {
            putLong("emotional_level", emotionalLevel.toLong())
            putLong("energy_level", energyLevel.toLong())
        }
    }

    fun logEmotionalCalendarEntryCreated(emotion: String?) {
        logEvent("emotional_calendar_entry") {
            putString("emotion", emotion ?: "unknown")
        }
    }

    fun logQuickMoodEntry(emotion: String?) {
        logEvent("quick_mood_entry") {
            putString("emotion", emotion ?: "unknown")
        }
    }
}
