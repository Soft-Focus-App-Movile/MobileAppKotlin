package com.softfocus.features.ai.data.models.response

import com.google.gson.annotations.SerializedName
import com.softfocus.features.ai.domain.models.ChatMessage
import com.softfocus.features.ai.domain.models.ExerciseRecommendation
import com.softfocus.features.ai.domain.models.MessageRole
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class ChatMessageResponseDto(
    @SerializedName("sessionId")
    val sessionId: String,

    @SerializedName("reply")
    val reply: String,

    @SerializedName("suggestedQuestions")
    val suggestedQuestions: List<String>,

    @SerializedName("recommendedExercises")
    val recommendedExercises: List<ExerciseRecommendationDto>,

    @SerializedName("crisisDetected")
    val crisisDetected: Boolean,

    @SerializedName("timestamp")
    val timestamp: String
) {
    fun toDomain(): ChatMessage {
        return ChatMessage(
            role = MessageRole.ASSISTANT,
            content = reply,
            timestamp = parseTimestamp(timestamp),
            suggestedQuestions = suggestedQuestions,
            recommendedExercises = recommendedExercises.map { it.toDomain() },
            crisisDetected = crisisDetected
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

data class ExerciseRecommendationDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("duration")
    val duration: String
) {
    fun toDomain(): ExerciseRecommendation {
        return ExerciseRecommendation(
            id = id,
            title = title,
            duration = duration
        )
    }
}
