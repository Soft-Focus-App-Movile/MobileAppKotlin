package com.softfocus.features.ai.domain.models

import java.time.LocalDateTime

data class EmotionAnalysis(
    val analysisId: String,
    val emotion: String,
    val confidence: Double,
    val allEmotions: Map<String, Double>,
    val analyzedAt: LocalDateTime,
    val checkInCreated: Boolean,
    val checkInId: String?
)
