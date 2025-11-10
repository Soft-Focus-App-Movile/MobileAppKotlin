package com.softfocus.features.notifications.domain.models

data class NotificationTemplate(
    val id: String,
    val type: NotificationType,
    val title: String,
    val body: String,
    val variables: List<String>
)