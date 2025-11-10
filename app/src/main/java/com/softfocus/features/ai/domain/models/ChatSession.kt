package com.softfocus.features.ai.domain.models

import java.time.LocalDateTime

data class ChatSession(
    val sessionId: String,
    val startedAt: LocalDateTime,
    val lastMessageAt: LocalDateTime,
    val messageCount: Int,
    val isActive: Boolean,
    val lastMessagePreview: String? = null
)
