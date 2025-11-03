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
    @SerializedName("user_id")
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
    @SerializedName("scheduled_at")
    val scheduledAt: String?,
    @SerializedName("delivered_at")
    val deliveredAt: String?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("metadata")
    val metadata: Map<String, String>?
) {
    fun toDomain(): Notification {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        return Notification(
            id = id,
            userId = userId,
            type = NotificationType.valueOf(type.uppercase()),
            title = title,
            content = content,
            priority = Priority.valueOf(priority.uppercase()),
            status = DeliveryStatus.valueOf(status.uppercase()),
            scheduledAt = scheduledAt?.let { LocalDateTime.parse(it, formatter) },
            deliveredAt = deliveredAt?.let { LocalDateTime.parse(it, formatter) },
            createdAt = LocalDateTime.parse(createdAt, formatter),
            metadata = metadata ?: emptyMap()
        )
    }
}