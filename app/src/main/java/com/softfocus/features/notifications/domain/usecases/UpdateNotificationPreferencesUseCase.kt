package com.softfocus.features.notifications.domain.usecases

import com.softfocus.features.notifications.domain.models.NotificationPreference
import com.softfocus.features.notifications.domain.repositories.NotificationPreferenceRepository
import javax.inject.Inject

class UpdateNotificationPreferencesUseCase @Inject constructor(
    private val repository: NotificationPreferenceRepository
) {
    suspend operator fun invoke(preferences: List<NotificationPreference>): Result<List<NotificationPreference>> {
        return repository.updatePreferences(preferences)
    }
}