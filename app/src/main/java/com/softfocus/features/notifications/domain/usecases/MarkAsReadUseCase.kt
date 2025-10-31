package com.softfocus.features.notifications.domain.usecases

import com.softfocus.features.notifications.domain.repositories.NotificationRepository
import javax.inject.Inject

class MarkAsReadUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(notificationId: String): Result<Unit> {
        return repository.markAsRead(notificationId)
    }
}
