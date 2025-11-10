package com.softfocus.features.notifications.data.models.response

import com.google.gson.annotations.SerializedName

data class PreferenceListResponseDto(
    @SerializedName("preferences")
    val preferences: List<NotificationPreferenceResponseDto>?
)