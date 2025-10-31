package com.softfocus.features.notifications.data.models.response

import com.google.gson.annotations.SerializedName

data class UnreadCountResponseDto(
    @SerializedName("unread_count")
    val unreadCount: Int
)