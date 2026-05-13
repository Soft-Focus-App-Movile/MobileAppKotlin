package com.softfocus.fakes.ai

import com.softfocus.features.ai.domain.models.AIUsageStats
import com.softfocus.features.ai.domain.models.ChatMessage
import com.softfocus.features.ai.domain.models.ChatSession
import com.softfocus.features.ai.domain.models.MessageRole
import com.softfocus.features.ai.domain.repositories.AIChatRepository
import com.softfocus.features.ai.domain.repositories.ChatResponse
import java.time.LocalDateTime

class FakeAIChatRepository : AIChatRepository {

    var sendMessageResult: Result<ChatResponse> = Result.success(defaultChatResponse())
    var getUsageStatsResult: Result<AIUsageStats> = Result.success(defaultUsageStats())
    var getChatSessionsResult: Result<List<ChatSession>> = Result.success(emptyList())
    var getSessionMessagesResult: Result<List<ChatMessage>> = Result.success(emptyList())

    var sendMessageCallCount = 0
    var lastSentMessage: String? = null

    override suspend fun sendMessage(message: String, sessionId: String?): Result<ChatResponse> {
        sendMessageCallCount++
        lastSentMessage = message
        return sendMessageResult
    }

    override suspend fun getUsageStats(): Result<AIUsageStats> = getUsageStatsResult

    override suspend fun getChatSessions(pageSize: Int): Result<List<ChatSession>> =
        getChatSessionsResult

    override suspend fun getSessionMessages(sessionId: String, limit: Int): Result<List<ChatMessage>> =
        getSessionMessagesResult

    fun reset() {
        sendMessageResult = Result.success(defaultChatResponse())
        getUsageStatsResult = Result.success(defaultUsageStats())
        sendMessageCallCount = 0
        lastSentMessage = null
    }

    companion object {
        fun defaultChatResponse() = ChatResponse(
            message = ChatMessage(
                role = MessageRole.ASSISTANT,
                content = "Hola, soy Focus AI. ¿En qué puedo ayudarte?",
                timestamp = LocalDateTime.now()
            ),
            sessionId = "session-123"
        )

        fun defaultUsageStats() = AIUsageStats(
            chatMessagesUsed = 2,
            chatMessagesLimit = 10,
            facialAnalysisUsed = 0,
            facialAnalysisLimit = 5,
            remainingMessages = 8,
            remainingAnalyses = 5,
            currentWeek = "2026-W20",
            resetsAt = LocalDateTime.now().plusDays(7),
            plan = "Free"
        )

        fun lowRemainingStats() = AIUsageStats(
            chatMessagesUsed = 8,
            chatMessagesLimit = 10,
            facialAnalysisUsed = 0,
            facialAnalysisLimit = 5,
            remainingMessages = 2,
            remainingAnalyses = 5,
            currentWeek = "2026-W20",
            resetsAt = LocalDateTime.now().plusDays(7),
            plan = "Free"
        )
    }
}
