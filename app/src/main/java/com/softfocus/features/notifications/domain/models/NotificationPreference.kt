package com.softfocus.features.notifications.domain.models

data class NotificationPreference(
    val id: String,
    val userId: String,
    val notificationType: NotificationType,
    val isEnabled: Boolean,
    val schedule: NotificationSchedule?,
    val deliveryMethod: DeliveryMethod
)