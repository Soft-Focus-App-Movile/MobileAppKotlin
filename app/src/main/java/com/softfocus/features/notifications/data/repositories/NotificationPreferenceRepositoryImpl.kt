package com.softfocus.features.notifications.data.repositories

import com.softfocus.features.notifications.data.models.request.NotificationPreferenceDto
import com.softfocus.features.notifications.data.models.request.NotificationScheduleDto
import com.softfocus.features.notifications.data.models.request.UpdatePreferencesRequestDto
import com.softfocus.features.notifications.data.remote.NotificationService
import com.softfocus.features.notifications.domain.models.NotificationPreference
import com.softfocus.features.notifications.domain.repositories.NotificationPreferenceRepository
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class NotificationPreferenceRepositoryImpl @Inject constructor(
    private val notificationService: NotificationService
) : NotificationPreferenceRepository {

    override suspend fun getPreferences(userId: String): Result<List<NotificationPreference>> {
        return try {
            val response = notificationService.getPreferences()

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val preferences = body.preferences?.map { it.toDomain() } ?: emptyList()
                Result.success(preferences)
            } else {
                Result.failure(Exception("Error al obtener preferencias: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red al obtener preferencias: ${e.message}", e))
        }
    }

    override suspend fun updatePreference(preference: NotificationPreference): Result<NotificationPreference> {
        return updatePreferences(listOf(preference)).map {
            it.firstOrNull() ?: preference
        }
    }

    override suspend fun updatePreferences(preferences: List<NotificationPreference>): Result<List<NotificationPreference>> {
        return try {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")

            // Mapear las preferencias al formato del backend
            val preferenceDtos = preferences.map { pref ->
                val scheduleDto = pref.schedule?.let {
                    NotificationScheduleDto(
                        startTime = it.startTime.format(formatter),
                        endTime = it.endTime.format(formatter),
                        daysOfWeek = it.daysOfWeek
                    )
                }

                android.util.Log.d("NotifRepo", "Mapeando ${pref.notificationType}: schedule=${scheduleDto}")

                NotificationPreferenceDto(
                    notificationType = pref.notificationType.name
                        .lowercase()
                        .replace("_", "-"),
                    isEnabled = pref.isEnabled,
                    schedule = scheduleDto,
                    deliveryMethod = pref.deliveryMethod.name.lowercase()
                )
            }

            android.util.Log.d("NotifRepo", "Enviando al backend: ${preferenceDtos.map { "${it.notificationType}=${it.isEnabled}, schedule=${it.schedule}" }}")

            val response = notificationService.updatePreferences(
                UpdatePreferencesRequestDto(preferenceDtos)
            )

            android.util.Log.d("NotifRepo", "Response code: ${response.code()}")

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val updatedPreferences = body.preferences?.map { it.toDomain() } ?: emptyList()
                android.util.Log.d("NotifRepo", "Preferencias recibidas: ${updatedPreferences.map { "${it.notificationType}=${it.isEnabled}, schedule=${it.schedule}" }}")
                Result.success(updatedPreferences)
            } else {
                Result.failure(Exception("Error al actualizar preferencias: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            android.util.Log.e("NotifRepo", "Excepción al actualizar preferencias", e)
            Result.failure(Exception("Error de red al actualizar preferencias: ${e.message}", e))
        }
    }

    override suspend fun resetToDefaults(userId: String): Result<List<NotificationPreference>> {
        return try {
            val response = notificationService.resetPreferences()

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val preferences = body.preferences?.map { it.toDomain() } ?: emptyList()
                Result.success(preferences)
            } else {
                Result.failure(Exception("Error al resetear preferencias: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red al resetear preferencias: ${e.message}", e))
        }
    }
}