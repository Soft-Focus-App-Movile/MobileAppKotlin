package com.softfocus.features.notifications.presentation.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.core.data.local.UserSession
import com.softfocus.features.notifications.domain.models.*
import com.softfocus.features.notifications.domain.usecases.GetNotificationPreferencesUseCase
import com.softfocus.features.notifications.domain.usecases.UpdateNotificationPreferencesUseCase
import com.softfocus.features.notifications.domain.repositories.NotificationPreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

data class NotificationPreferencesState(
    val preferences: List<NotificationPreference> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class NotificationPreferencesViewModel @Inject constructor(
    private val getPreferencesUseCase: GetNotificationPreferencesUseCase,
    private val updatePreferencesUseCase: UpdateNotificationPreferencesUseCase,
    private val preferenceRepository: NotificationPreferenceRepository,
    private val userSession: UserSession
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationPreferencesState())
    val state: StateFlow<NotificationPreferencesState> = _state.asStateFlow()

    init {
        loadPreferences()
    }

    fun loadPreferences() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            val userId = userSession.getUser()?.id
            if (userId == null) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Usuario no autenticado"
                )
                return@launch
            }

            getPreferencesUseCase(userId).fold(
                onSuccess = { preferences ->
                    // Asegurar que tenemos las 3 preferencias principales
                    val enrichedPreferences = ensureMainPreferences(preferences)
                    _state.value = _state.value.copy(
                        preferences = enrichedPreferences,
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = error.message ?: "Error al cargar preferencias"
                    )
                }
            )
        }
    }

    fun togglePreference(preference: NotificationPreference) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isSaving = true, successMessage = null, error = null)

                val updated = preference.copy(isEnabled = !preference.isEnabled)
                val updatedList = _state.value.preferences.map {
                    if (it.notificationType == updated.notificationType) updated else it
                }

                // Actualizar estado local inmediatamente
                _state.value = _state.value.copy(preferences = updatedList)

                // Intentar guardar en el servidor
                updatePreferencesUseCase(updatedList).fold(
                    onSuccess = { serverPreferences ->
                        _state.value = _state.value.copy(
                            preferences = serverPreferences,
                            isSaving = false,
                            successMessage = "Preferencias actualizadas"
                        )

                        // Limpiar mensaje después de 3 segundos
                        kotlinx.coroutines.delay(3000)
                        if (_state.value.successMessage == "Preferencias actualizadas") {
                            _state.value = _state.value.copy(successMessage = null)
                        }
                    },
                    onFailure = { error ->
                        // Revertir cambios en caso de error
                        _state.value = _state.value.copy(
                            preferences = _state.value.preferences.map {
                                if (it.notificationType == preference.notificationType) preference else it
                            },
                            isSaving = false,
                            error = error.message ?: "Error al actualizar"
                        )
                    }
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSaving = false,
                    error = "Error inesperado: ${e.message}"
                )
            }
        }
    }

    fun updateCheckInTime(preference: NotificationPreference, time: LocalTime) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isSaving = true, successMessage = null, error = null)

                val schedule = preference.schedule?.copy(startTime = time, endTime = time)
                    ?: NotificationSchedule(
                        startTime = time,
                        endTime = time,
                        daysOfWeek = listOf(1, 2, 3, 4, 5, 6, 7)
                    )

                val updated = preference.copy(schedule = schedule)
                val updatedList = _state.value.preferences.map {
                    if (it.notificationType == updated.notificationType) updated else it
                }

                // Actualizar estado local inmediatamente
                _state.value = _state.value.copy(preferences = updatedList)

                // Intentar guardar en el servidor
                updatePreferencesUseCase(updatedList).fold(
                    onSuccess = { serverPreferences ->
                        _state.value = _state.value.copy(
                            preferences = serverPreferences,
                            isSaving = false,
                            successMessage = "Hora actualizada correctamente"
                        )

                        // Limpiar mensaje después de 3 segundos
                        kotlinx.coroutines.delay(3000)
                        if (_state.value.successMessage == "Hora actualizada correctamente") {
                            _state.value = _state.value.copy(successMessage = null)
                        }
                    },
                    onFailure = { error ->
                        // Revertir cambios en caso de error
                        _state.value = _state.value.copy(
                            preferences = _state.value.preferences.map {
                                if (it.notificationType == preference.notificationType) preference else it
                            },
                            isSaving = false,
                            error = error.message ?: "Error al actualizar hora"
                        )
                    }
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSaving = false,
                    error = "Error inesperado: ${e.message}"
                )
            }
        }
    }

    /**
     * Asegura que existen las 3 preferencias principales del mockup.
     * Si no existen, las crea con valores por defecto.
     */
    private fun ensureMainPreferences(
        preferences: List<NotificationPreference>
    ): List<NotificationPreference> {
        val mutablePrefs = preferences.toMutableList()

        // 1. Recordatorios de registro diario (CHECKIN_REMINDER)
        if (preferences.none { it.notificationType == NotificationType.CHECKIN_REMINDER }) {
            mutablePrefs.add(
                NotificationPreference(
                    id = "checkin_reminder",
                    userId = userSession.getUser()?.id ?: "",
                    notificationType = NotificationType.CHECKIN_REMINDER,
                    isEnabled = true,
                    deliveryMethod = DeliveryMethod.PUSH,
                    schedule = NotificationSchedule(
                        startTime = LocalTime.of(9, 0),
                        endTime = LocalTime.of(9, 0),
                        daysOfWeek = listOf(1, 2, 3, 4, 5, 6, 7)
                    )
                )
            )
        }

        // 2. Sugerencias diarias (INFO)
        if (preferences.none { it.notificationType == NotificationType.INFO }) {
            mutablePrefs.add(
                NotificationPreference(
                    id = "daily_suggestions",
                    userId = userSession.getUser()?.id ?: "",
                    notificationType = NotificationType.INFO,
                    isEnabled = true,
                    deliveryMethod = DeliveryMethod.PUSH,
                    schedule = null
                )
            )
        }

        // 3. Promociones y novedades (SYSTEM_UPDATE)
        if (preferences.none { it.notificationType == NotificationType.SYSTEM_UPDATE }) {
            mutablePrefs.add(
                NotificationPreference(
                    id = "promotions",
                    userId = userSession.getUser()?.id ?: "",
                    notificationType = NotificationType.SYSTEM_UPDATE,
                    isEnabled = true,
                    deliveryMethod = DeliveryMethod.PUSH,
                    schedule = null
                )
            )
        }

        // Ordenar para que aparezcan en el orden correcto
        return mutablePrefs.sortedBy {
            when (it.notificationType) {
                NotificationType.CHECKIN_REMINDER -> 1
                NotificationType.INFO -> 2
                NotificationType.SYSTEM_UPDATE -> 3
                else -> 4
            }
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val userId = userSession.getUser()?.id ?: return@launch

            preferenceRepository.resetToDefaults(userId).fold(
                onSuccess = { preferences ->
                    val enrichedPreferences = ensureMainPreferences(preferences)
                    _state.value = _state.value.copy(
                        preferences = enrichedPreferences,
                        isLoading = false,
                        successMessage = "Configuración restaurada"
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = error.message ?: "Error al restaurar"
                    )
                }
            )
        }
    }
}