package com.softfocus.features.notifications.data.models.response

import com.google.gson.annotations.SerializedName

data class NotificationListResponseDto(
    @SerializedName("notifications")
    val notifications: List<NotificationResponseDto>,
    @SerializedName("total")
    val total: Int,
    @SerializedName("page")
    val page: Int,
    @SerializedName("pageSize")
    val size: Int
)