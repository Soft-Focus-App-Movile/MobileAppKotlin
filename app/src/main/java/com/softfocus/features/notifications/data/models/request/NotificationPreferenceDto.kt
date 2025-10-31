package com.softfocus.features.notifications.data.models.request

import com.google.gson.annotations.SerializedName

data class NotificationPreferenceDto(
    @SerializedName("notification_type")
    val notificationType: String,
    @SerializedName("is_enabled")
    val isEnabled: Boolean,
    @SerializedName("schedule")
    val schedule: NotificationScheduleDto?,
    @SerializedName("delivery_method")
    val deliveryMethod: String
)