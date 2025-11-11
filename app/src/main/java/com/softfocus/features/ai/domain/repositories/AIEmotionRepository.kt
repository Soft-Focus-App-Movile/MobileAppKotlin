package com.softfocus.features.ai.domain.repositories

import com.softfocus.features.ai.domain.models.EmotionAnalysis
import java.io.File

interface AIEmotionRepository {
    suspend fun analyzeEmotion(imageFile: File, autoCheckIn: Boolean = true): Result<EmotionAnalysis>
}
