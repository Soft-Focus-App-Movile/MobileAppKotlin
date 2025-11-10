package com.softfocus.features.notifications.domain.usecases

import com.softfocus.features.notifications.domain.models.Notification
import com.softfocus.features.notifications.domain.models.NotificationType
import com.softfocus.features.notifications.domain.models.DeliveryStatus
import com.softfocus.features.notifications.domain.repositories.NotificationRepository
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(
        userId: String,
        status: DeliveryStatus? = null,
        type: NotificationType? = null,
        page: Int = 0
    ): Result<List<Notification>> {
        return repository.getNotifications(userId, status, type, page)
    }
}