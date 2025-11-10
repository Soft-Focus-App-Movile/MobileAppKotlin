package com.softfocus.features.notifications.data.models.request

import com.google.gson.annotations.SerializedName

data class NotificationScheduleDto(
    @SerializedName("start_time")
    val startTime: String, // "HH:mm" format
    @SerializedName("end_time")
    val endTime: String,   // "HH:mm" format
    @SerializedName("days_of_week")
    val daysOfWeek: List<Int> // 1-7 (Monday to Sunday)
)