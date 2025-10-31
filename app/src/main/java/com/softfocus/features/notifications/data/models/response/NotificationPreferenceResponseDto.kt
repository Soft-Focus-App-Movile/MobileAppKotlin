package com.softfocus.features.notifications.data.models.response

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.annotations.SerializedName
import com.softfocus.features.notifications.domain.models.*
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class NotificationPreferenceResponseDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("notification_type")
    val notificationType: String,
    @SerializedName("is_enabled")
    val isEnabled: Boolean,
    @SerializedName("schedule")
    val schedule: ScheduleDto?,
    @SerializedName("delivery_method")
    val deliveryMethod: String
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toDomain(): NotificationPreference {
        return NotificationPreference(
            id = id,
            userId = userId,
            notificationType = NotificationType.valueOf(notificationType.uppercase()),
            isEnabled = isEnabled,
            schedule = schedule?.toDomain(),
            deliveryMethod = DeliveryMethod.valueOf(deliveryMethod.uppercase())
        )
    }
}