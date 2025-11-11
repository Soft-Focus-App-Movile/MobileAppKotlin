package com.softfocus.features.ai.presentation.emotion

import com.softfocus.features.ai.domain.models.EmotionAnalysis

data class EmotionDetectionState(
    val isLoading: Boolean = false,
    val emotionAnalysis: EmotionAnalysis? = null,
    val error: String? = null,
    val capturedImagePath: String? = null,
    val hasCheckInToday: Boolean = false,
    val isCheckingTodayCheckIn: Boolean = true
)
