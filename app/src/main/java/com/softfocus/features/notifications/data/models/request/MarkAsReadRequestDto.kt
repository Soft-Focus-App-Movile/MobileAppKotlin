package com.softfocus.features.notifications.data.models.request

import com.google.gson.annotations.SerializedName

data class MarkAsReadRequestDto(
    @SerializedName("notification_id")
    val notificationId: String
)