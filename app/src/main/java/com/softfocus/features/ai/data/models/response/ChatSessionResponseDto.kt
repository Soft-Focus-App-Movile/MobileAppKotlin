package com.softfocus.features.ai.data.models.response

import com.google.gson.annotations.SerializedName
import com.softfocus.features.ai.domain.models.ChatSession
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class ChatSessionResponseDto(
    @SerializedName("sessionId")
    val sessionId: String,
    @SerializedName("startedAt")
    val startedAt: String,
    @SerializedName("lastMessageAt")
    val lastMessageAt: String,
    @SerializedName("messageCount")
    val messageCount: Int,
    @SerializedName("isActive")
    val isActive: Boolean,
    @SerializedName("lastMessagePreview")
    val lastMessagePreview: String?
) {
    fun toDomain(): ChatSession {
        return ChatSession(
            sessionId = sessionId,
            startedAt = parseDateTime(startedAt),
            lastMessageAt = parseDateTime(lastMessageAt),
            messageCount = messageCount,
            isActive = isActive,
            lastMessagePreview = lastMessagePreview
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

data class ChatHistoryResponseDto(
    @SerializedName("sessions")
    val sessions: List<ChatSessionResponseDto>,
    @SerializedName("totalCount")
    val totalCount: Int
)
