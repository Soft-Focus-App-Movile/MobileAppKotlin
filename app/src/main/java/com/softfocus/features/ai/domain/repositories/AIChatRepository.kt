package com.softfocus.features.ai.domain.repositories

import com.softfocus.features.ai.domain.models.AIUsageStats
import com.softfocus.features.ai.domain.models.ChatMessage
import com.softfocus.features.ai.domain.models.ChatSession

data class ChatResponse(
    val message: ChatMessage,
    val sessionId: String
)

interface AIChatRepository {
    suspend fun sendMessage(message: String, sessionId: String?): Result<ChatResponse>
    suspend fun getUsageStats(): Result<AIUsageStats>
    suspend fun getChatSessions(pageSize: Int = 20): Result<List<ChatSession>>
    suspend fun getSessionMessages(sessionId: String, limit: Int = 50): Result<List<ChatMessage>>
}
