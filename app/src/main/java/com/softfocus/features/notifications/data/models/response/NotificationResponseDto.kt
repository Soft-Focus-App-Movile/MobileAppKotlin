package com.softfocus.features.notifications.data.models.response

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.annotations.SerializedName
import com.softfocus.features.notifications.domain.models.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class NotificationResponseDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("userId")  // ✅ Cambia a camelCase
    val userId: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("priority")
    val priority: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("deliveryMethod")  // ✅ Este campo faltaba
    val deliveryMethod: String,

    @SerializedName("scheduledAt")  // ✅ Cambia a camelCase
    val scheduledAt: String?,

    @SerializedName("deliveredAt")  // ✅ Cambia a camelCase
    val deliveredAt: String?,

    @SerializedName("readAt")  // ✅ Agrega este campo que viene del backend
    val readAt: String?,

    @SerializedName("createdAt")  // ✅ Cambia a camelCase
    val createdAt: String,

    @SerializedName("metadata")
    val metadata: Map<String, String>?
) {
    fun toDomain(): Notification {
        return try {
            val formatter = DateTimeFormatter.ISO_DATE_TIME
            Notification(
                id = id,
                userId = userId,
                type = enumValueOfOrNull<NotificationType>(type.uppercase()) ?: NotificationType.INFO,
                title = title,
                content = content,
                priority = enumValueOfOrNull<Priority>(priority.uppercase()) ?: Priority.NORMAL,
                status = enumValueOfOrNull<DeliveryStatus>(status.uppercase()) ?: DeliveryStatus.PENDING,
                scheduledAt = scheduledAt?.let { LocalDateTime.parse(it, formatter) },
                deliveredAt = deliveredAt?.let { LocalDateTime.parse(it, formatter) },
                readAt = readAt?.let { LocalDateTime.parse(it, formatter) },
                createdAt = LocalDateTime.parse(createdAt, formatter),
                metadata = metadata ?: emptyMap()
            )
        } catch (e: Exception) {
            println("❌ Error mapeando Notification DTO: ${e.message}")
            // Crear notificación por defecto para no romper el flujo
            Notification(
                id = id,
                userId = userId,
                type = NotificationType.INFO,
                title = title,
                content = content,
                priority = Priority.NORMAL,
                status = DeliveryStatus.PENDING,
                scheduledAt = null,
                deliveredAt = null,
                readAt = null,
                createdAt = LocalDateTime.now(),
                metadata = emptyMap()
            )
        }
    }

    // Función helper para mapeo seguro de enums
    inline fun <reified T : Enum<T>> enumValueOfOrNull(name: String): T? {
        return try {
            enumValueOf<T>(name)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}