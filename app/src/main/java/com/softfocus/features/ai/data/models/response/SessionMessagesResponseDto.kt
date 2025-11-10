package com.softfocus.features.ai.data.models.response

import com.google.gson.annotations.SerializedName
import com.softfocus.features.ai.domain.models.ChatMessage
import com.softfocus.features.ai.domain.models.MessageRole
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class SessionMessagesResponseDto(
    @SerializedName("sessionId")
    val sessionId: String,
    @SerializedName("messages")
    val messages: List<ChatMessageItemDto>
)

data class ChatMessageItemDto(
    @SerializedName("role")
    val role: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("timestamp")
    val timestamp: String,
    @SerializedName("suggestedQuestions")
    val suggestedQuestions: List<String> = emptyList()
) {
    fun toDomain(): ChatMessage {
        return ChatMessage(
            role = if (role == "user") MessageRole.USER else MessageRole.ASSISTANT,
            content = content,
            timestamp = parseDateTime(timestamp),
            suggestedQuestions = suggestedQuestions
        )
    }

    private fun parseDateTime(dateTimeStr: String): LocalDateTime {
        return try {
            val instant = Instant.parse(dateTimeStr)
            LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        } catch (e: Exception) {
            LocalDateTime.now()
        }
    }
}
