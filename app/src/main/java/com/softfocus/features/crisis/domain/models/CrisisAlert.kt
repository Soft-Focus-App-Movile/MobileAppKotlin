package com.softfocus.features.crisis.domain.models

data class CrisisAlert(
    val id: String,
    val patientId: String,
    val patientName: String,
    val patientPhotoUrl: String?,
    val psychologistId: String,
    val severity: String,
    val status: String,
    val triggerSource: String,
    val triggerReason: String?,
    val location: Location?,
    val emotionalContext: EmotionalContext?,
    val psychologistNotes: String?,
    val createdAt: String,
    val attendedAt: String?,
    val resolvedAt: String?
)

data class Location(
    val latitude: Double?,
    val longitude: Double?,
    val displayString: String
)

data class EmotionalContext(
    val lastDetectedEmotion: String?,
    val lastEmotionDetectedAt: String?,
    val emotionSource: String?
)
