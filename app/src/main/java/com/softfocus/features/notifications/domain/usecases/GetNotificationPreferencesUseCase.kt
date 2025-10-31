package com.softfocus.features.notifications.domain.usecases

import com.softfocus.features.notifications.domain.models.NotificationPreference
import com.softfocus.features.notifications.domain.repositories.NotificationPreferenceRepository
import javax.inject.Inject

class GetNotificationPreferencesUseCase @Inject constructor(
    private val repository: NotificationPreferenceRepository
) {
    suspend operator fun invoke(userId: String): Result<List<NotificationPreference>> {
        return repository.getPreferences(userId)
    }
}