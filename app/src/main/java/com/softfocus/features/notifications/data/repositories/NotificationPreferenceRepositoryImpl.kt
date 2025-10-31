package com.softfocus.features.notifications.data.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import com.softfocus.features.notifications.data.models.request.NotificationPreferenceDto
import com.softfocus.features.notifications.data.models.request.NotificationScheduleDto
import com.softfocus.features.notifications.data.models.request.UpdatePreferencesRequestDto
import com.softfocus.features.notifications.data.remote.NotificationService
import com.softfocus.features.notifications.domain.models.NotificationPreference
import com.softfocus.features.notifications.domain.repositories.NotificationPreferenceRepository
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class NotificationPreferenceRepositoryImpl @Inject constructor(
    private val notificationService: NotificationService
) : NotificationPreferenceRepository {

    override suspend fun getPreferences(userId: String): Result<List<NotificationPreference>> {
        return try {
            val response = notificationService.getPreferences()

            if (response.isSuccessful && response.body() != null) {
                val preferences = response.body()!!.preferences.map { it.toDomain() }
                Result.success(preferences)
            } else {
                Result.failure(Exception("Error al obtener preferencias"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePreference(preference: NotificationPreference): Result<NotificationPreference> {
        return updatePreferences(listOf(preference)).map { it.first() }
    }

    override suspend fun updatePreferences(preferences: List<NotificationPreference>): Result<List<NotificationPreference>> {
        return try {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val preferenceDtos = preferences.map { pref ->
                NotificationPreferenceDto(
                    notificationType = pref.notificationType.name.lowercase().replace("_", "-"),
                    isEnabled = pref.isEnabled,
                    schedule = pref.schedule?.let {
                        NotificationScheduleDto(
                            startTime = it.startTime.format(formatter),
                            endTime = it.endTime.format(formatter),
                            daysOfWeek = it.daysOfWeek
                        )
                    },
                    deliveryMethod = pref.deliveryMethod.name.lowercase()
                )
            }

            val response = notificationService.updatePreferences(
                UpdatePreferencesRequestDto(preferenceDtos)
            )

            if (response.isSuccessful && response.body() != null) {
                val updatedPreferences = response.body()!!.preferences.map { it.toDomain() }
                Result.success(updatedPreferences)
            } else {
                Result.failure(Exception("Error al actualizar preferencias"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resetToDefaults(userId: String): Result<List<NotificationPreference>> {
        return try {
            val response = notificationService.resetPreferences()

            if (response.isSuccessful && response.body() != null) {
                val preferences = response.body()!!.preferences.map { it.toDomain() }
                Result.success(preferences)
            } else {
                Result.failure(Exception("Error al resetear preferencias"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}