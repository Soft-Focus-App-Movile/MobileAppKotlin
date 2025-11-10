package com.softfocus.features.notifications.domain.repositories

import com.softfocus.features.notifications.domain.models.NotificationPreference

interface NotificationPreferenceRepository {
    suspend fun getPreferences(userId: String): Result<List<NotificationPreference>>

    suspend fun updatePreference(preference: NotificationPreference): Result<NotificationPreference>

    suspend fun updatePreferences(preferences: List<NotificationPreference>): Result<List<NotificationPreference>>

    suspend fun resetToDefaults(userId: String): Result<List<NotificationPreference>>
}