package com.softfocus.features.ai.data.models.response

import com.google.gson.annotations.SerializedName
import com.softfocus.features.ai.domain.models.EmotionAnalysis
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class EmotionAnalysisResponseDto(
    @SerializedName("analysisId")
    val analysisId: String,

    @SerializedName("emotion")
    val emotion: String,

    @SerializedName("confidence")
    val confidence: Double,

    @SerializedName("allEmotions")
    val allEmotions: Map<String, Double>,

    @SerializedName("analyzedAt")
    val analyzedAt: String,

    @SerializedName("checkInCreated")
    val checkInCreated: Boolean,

    @SerializedName("checkInId")
    val checkInId: String?
) {
    fun toDomain(): EmotionAnalysis {
        return EmotionAnalysis(
            analysisId = analysisId,
            emotion = emotion,
            confidence = confidence,
            allEmotions = allEmotions,
            analyzedAt = parseTimestamp(analyzedAt),
            checkInCreated = checkInCreated,
            checkInId = checkInId
        )
    }

    private fun parseTimestamp(timestamp: String): LocalDateTime {
        return try {
            LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME)
        } catch (e: Exception) {
            LocalDateTime.now()
        }
    }
}
