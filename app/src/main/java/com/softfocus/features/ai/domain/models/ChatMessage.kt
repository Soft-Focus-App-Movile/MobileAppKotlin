package com.softfocus.features.ai.domain.models

import java.time.LocalDateTime

data class ChatMessage(
    val role: MessageRole,
    val content: String,
    val timestamp: LocalDateTime,
    val suggestedQuestions: List<String> = emptyList(),
    val recommendedExercises: List<ExerciseRecommendation> = emptyList(),
    val crisisDetected: Boolean = false
)

enum class MessageRole {
    USER,
    ASSISTANT
}

data class ExerciseRecommendation(
    val id: String,
    val title: String,
    val duration: String
)
