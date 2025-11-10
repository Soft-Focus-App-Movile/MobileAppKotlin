package com.softfocus.features.notifications.data.models.response

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
    fun toDomain(): NotificationPreference {
        return NotificationPreference(
            id = id,
            userId = userId,
            // Convertir "checkin-reminder" -> CHECKIN_REMINDER
            notificationType = parseNotificationType(notificationType),
            isEnabled = isEnabled,
            schedule = schedule?.toDomain(),
            // Convertir "push" -> PUSH
            deliveryMethod = parseDeliveryMethod(deliveryMethod)
        )
    }

    private fun parseNotificationType(type: String): NotificationType {
        return try {
            // Convertir "checkin-reminder" -> "CHECKIN_REMINDER"
            val normalized = type.uppercase().replace("-", "_")
            NotificationType.valueOf(normalized)
        } catch (e: IllegalArgumentException) {
            // Si falla, usar INFO como fallback
            NotificationType.INFO
        }
    }

    private fun parseDeliveryMethod(method: String): DeliveryMethod {
        return try {
            DeliveryMethod.valueOf(method.uppercase())
        } catch (e: IllegalArgumentException) {
            // Si falla, usar PUSH como fallback
            DeliveryMethod.PUSH
        }
    }
}
