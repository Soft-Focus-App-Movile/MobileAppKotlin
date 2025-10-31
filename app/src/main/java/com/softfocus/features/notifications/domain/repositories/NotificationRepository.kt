package com.softfocus.features.notifications.domain.repositories

import com.softfocus.features.notifications.domain.models.Notification
import com.softfocus.features.notifications.domain.models.NotificationType
import com.softfocus.features.notifications.domain.models.DeliveryStatus
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun getNotifications(
        userId: String,
        status: DeliveryStatus? = null,
        type: NotificationType? = null,
        page: Int = 0,
        size: Int = 20
    ): Result<List<Notification>>

    suspend fun getNotificationById(id: String): Result<Notification>

    suspend fun sendNotification(notification: Notification): Result<Notification>

    suspend fun markAsRead(notificationId: String): Result<Unit>

    suspend fun markAllAsRead(userId: String): Result<Unit>

    suspend fun deleteNotification(notificationId: String): Result<Unit>

    fun observeNotifications(userId: String): Flow<List<Notification>>

    suspend fun getUnreadCount(userId: String): Result<Int>
}