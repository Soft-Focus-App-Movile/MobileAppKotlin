package com.softfocus.features.notifications.data.models.request

import com.google.gson.annotations.SerializedName

data class UpdatePreferencesRequestDto(
    @SerializedName("preferences")
    val preferences: List<NotificationPreferenceDto>
)