package com.softfocus.features.ai.data.models.response

import com.google.gson.annotations.SerializedName
import com.softfocus.features.ai.domain.models.AIUsageStats
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class AIUsageStatsResponseDto(
    @SerializedName("chatMessagesUsed")
    val chatMessagesUsed: Int,

    @SerializedName("chatMessagesLimit")
    val chatMessagesLimit: Int,

    @SerializedName("facialAnalysisUsed")
    val facialAnalysisUsed: Int,

    @SerializedName("facialAnalysisLimit")
    val facialAnalysisLimit: Int,

    @SerializedName("remainingMessages")
    val remainingMessages: Int,

    @SerializedName("remainingAnalyses")
    val remainingAnalyses: Int,

    @SerializedName("currentWeek")
    val currentWeek: String,

    @SerializedName("resetsAt")
    val resetsAt: String,

    @SerializedName("plan")
    val plan: String
) {
    fun toDomain(): AIUsageStats {
        return AIUsageStats(
            chatMessagesUsed = chatMessagesUsed,
            chatMessagesLimit = chatMessagesLimit,
            facialAnalysisUsed = facialAnalysisUsed,
            facialAnalysisLimit = facialAnalysisLimit,
            remainingMessages = remainingMessages,
            remainingAnalyses = remainingAnalyses,
            currentWeek = currentWeek,
            resetsAt = parseTimestamp(resetsAt),
            plan = plan
        )
    }

    private fun parseTimestamp(timestamp: String): LocalDateTime {
        return try {
            LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME)
        } catch (e: Exception) {
            LocalDateTime.now().plusDays(7)
        }
    }
}
