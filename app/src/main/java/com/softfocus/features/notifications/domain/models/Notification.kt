package com.softfocus.features.notifications.domain.models

import java.time.LocalDateTime

data class Notification(
    val id: String,
    val userId: String,
    val type: NotificationType,
    val title: String,
    val content: String,
    val priority: Priority,
    val status: DeliveryStatus,
    val scheduledAt: LocalDateTime?,
    val deliveredAt: LocalDateTime?,
    val readAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val metadata: Map<String, String> = emptyMap()
)