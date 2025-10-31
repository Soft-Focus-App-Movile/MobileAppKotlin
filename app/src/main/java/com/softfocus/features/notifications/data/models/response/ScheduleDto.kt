package com.softfocus.features.notifications.data.models.response

import android.os.Build
import androidx.annotation.RequiresApi
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
    val daysOfWeek: List<Int>
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toDomain(): NotificationSchedule {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return NotificationSchedule(
            startTime = LocalTime.parse(startTime, formatter),
            endTime = LocalTime.parse(endTime, formatter),
            daysOfWeek = daysOfWeek
        )
    }
}