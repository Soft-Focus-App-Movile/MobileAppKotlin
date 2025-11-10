package com.softfocus.features.ai.presentation.chat

import com.softfocus.features.ai.domain.models.AIUsageStats
import com.softfocus.features.ai.domain.models.ChatMessage

data class AIChatState(
    val messages: List<ChatMessage> = emptyList(),
    val currentMessage: String = "",
    val sessionId: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val usageStats: AIUsageStats? = null,
    val showLimitWarning: Boolean = false
)
