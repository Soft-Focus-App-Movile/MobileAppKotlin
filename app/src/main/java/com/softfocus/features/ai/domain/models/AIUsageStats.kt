package com.softfocus.features.ai.domain.models

import java.time.LocalDateTime

data class AIUsageStats(
    val chatMessagesUsed: Int,
    val chatMessagesLimit: Int,
    val facialAnalysisUsed: Int,
    val facialAnalysisLimit: Int,
    val remainingMessages: Int,
    val remainingAnalyses: Int,
    val currentWeek: String,
    val resetsAt: LocalDateTime,
    val plan: String // "Free" or "Premium"
)
