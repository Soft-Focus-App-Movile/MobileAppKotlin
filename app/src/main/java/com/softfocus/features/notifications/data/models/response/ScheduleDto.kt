package com.softfocus.features.notifications.data.models.response

import com.google.gson.annotations.SerializedName
import com.softfocus.features.notifications.domain.models.NotificationSchedule
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class ScheduleDto(
    @SerializedName("start_time")
    val startTime: String,
    @SerializedName("end_time")
    val endTime: String,
    @SerializedName("days_of_week")
    val daysOfWeek: List<Int>?
) {
    fun toDomain(): NotificationSchedule {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return NotificationSchedule(
            startTime = try {
                LocalTime.parse(startTime, formatter)
            } catch (e: Exception) {
                LocalTime.of(9, 0) // Fallback
            },
            endTime = try {
                LocalTime.parse(endTime, formatter)
            } catch (e: Exception) {
                LocalTime.of(22, 0) // Fallback
            },
            daysOfWeek = daysOfWeek ?: listOf(1, 2, 3, 4, 5, 6, 7)
        )
    }
}