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
            try {
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
                        val enrichedPreferences = ensureMainPreferences(preferences ?: emptyList())
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
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Error inesperado al cargar: ${e.message}"
                )
            }
        }
    }

    fun togglePreference(preference: NotificationPreference) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isSaving = true, successMessage = null, error = null)

                val updated = preference.copy(isEnabled = !preference.isEnabled)

                // Crear lista actualizada de forma segura
                val currentPreferences = _state.value.preferences
                val updatedList = currentPreferences.map {
                    if (it.notificationType == updated.notificationType) updated else it
                }

                // Validar que la lista no esté vacía
                if (updatedList.isEmpty()) {
                    _state.value = _state.value.copy(
                        isSaving = false,
                        error = "No hay preferencias para actualizar"
                    )
                    return@launch
                }

                // Log para debug
                android.util.Log.d("NotifPrefVM", "Enviando actualización: ${updatedList.map { "${it.notificationType}=${it.isEnabled}" }}")

                // Intentar guardar en el servidor
                updatePreferencesUseCase(updatedList).fold(
                    onSuccess = { serverPreferences ->
                        android.util.Log.d("NotifPrefVM", "Respuesta del servidor: ${serverPreferences?.map { "${it.notificationType}=${it.isEnabled}" }}")

                        // Si el servidor retorna preferencias, usarlas
                        // Si no, mantener las actualizadas localmente
                        val finalPreferences = if (!serverPreferences.isNullOrEmpty()) {
                            ensureMainPreferences(serverPreferences)
                        } else {
                            updatedList
                        }

                        _state.value = _state.value.copy(
                            preferences = finalPreferences,
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
                        android.util.Log.e("NotifPrefVM", "Error al actualizar: ${error.message}", error)
                        // Revertir cambios en caso de error
                        _state.value = _state.value.copy(
                            preferences = currentPreferences,
                            isSaving = false,
                            error = error.message ?: "Error al actualizar"
                        )
                    }
                )
            } catch (e: Exception) {
                android.util.Log.e("NotifPrefVM", "Excepción en togglePreference", e)
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

                // Crear lista actualizada de forma segura
                val currentPreferences = _state.value.preferences
                val updatedList = currentPreferences.map {
                    if (it.notificationType == updated.notificationType) updated else it
                }

                // Validar que la lista no esté vacía
                if (updatedList.isEmpty()) {
                    _state.value = _state.value.copy(
                        isSaving = false,
                        error = "No hay preferencias para actualizar"
                    )
                    return@launch
                }

                // Actualizar estado local inmediatamente
                _state.value = _state.value.copy(preferences = updatedList)

                // Intentar guardar en el servidor
                updatePreferencesUseCase(updatedList).fold(
                    onSuccess = { serverPreferences ->
                        val safePreferences = serverPreferences ?: updatedList
                        _state.value = _state.value.copy(
                            preferences = safePreferences,
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
                            preferences = currentPreferences,
                            isSaving = false,
                            error = error.message ?: "Error al actualizar hora"
                        )
                    }
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSaving = false,
                    error = "Error inesperado: ${e.message}\n${e.stackTraceToString()}"
                )
            }
        }
    }

    /**
     * Asegura que existen las 3 preferencias principales del mockup.
     * Si no existen, las crea con valores por defecto.
     * IMPORTANTE: Filtra y retorna SOLO las 3 preferencias principales.
     */
    private fun ensureMainPreferences(
        preferences: List<NotificationPreference>
    ): List<NotificationPreference> {
        try {
            val mainTypes = listOf(
                NotificationType.CHECKIN_REMINDER,
                NotificationType.INFO,
                NotificationType.SYSTEM_UPDATE
            )

            // Filtrar solo las preferencias que nos interesan
            val filteredPrefs = preferences.filter { it.notificationType in mainTypes }
            val mutablePrefs = mutableListOf<NotificationPreference>()

            // 1. Recordatorios de registro diario (CHECKIN_REMINDER)
            val checkInPref = filteredPrefs.find { it.notificationType == NotificationType.CHECKIN_REMINDER }
            if (checkInPref != null) {
                // Si existe pero no tiene schedule, agregarlo
                val prefWithSchedule = if (checkInPref.schedule == null) {
                    checkInPref.copy(
                        schedule = NotificationSchedule(
                            startTime = LocalTime.of(9, 0),
                            endTime = LocalTime.of(9, 0),
                            daysOfWeek = listOf(1, 2, 3, 4, 5, 6, 7)
                        )
                    )
                } else {
                    checkInPref
                }
                mutablePrefs.add(prefWithSchedule)
            } else {
                // Crear nueva
                android.util.Log.d("NotifPrefVM", "Creando preferencia por defecto: CHECKIN_REMINDER")
                mutablePrefs.add(
                    NotificationPreference(
                        id = "checkin_reminder_local",
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
            val infoPref = filteredPrefs.find { it.notificationType == NotificationType.INFO }
            if (infoPref != null) {
                mutablePrefs.add(infoPref)
            } else {
                android.util.Log.d("NotifPrefVM", "Creando preferencia por defecto: INFO")
                mutablePrefs.add(
                    NotificationPreference(
                        id = "daily_suggestions_local",
                        userId = userSession.getUser()?.id ?: "",
                        notificationType = NotificationType.INFO,
                        isEnabled = true,
                        deliveryMethod = DeliveryMethod.PUSH,
                        schedule = null
                    )
                )
            }

            // 3. Promociones y novedades (SYSTEM_UPDATE)
            val systemPref = filteredPrefs.find { it.notificationType == NotificationType.SYSTEM_UPDATE }
            if (systemPref != null) {
                mutablePrefs.add(systemPref)
            } else {
                android.util.Log.d("NotifPrefVM", "Creando preferencia por defecto: SYSTEM_UPDATE")
                mutablePrefs.add(
                    NotificationPreference(
                        id = "promotions_local",
                        userId = userSession.getUser()?.id ?: "",
                        notificationType = NotificationType.SYSTEM_UPDATE,
                        isEnabled = true,
                        deliveryMethod = DeliveryMethod.PUSH,
                        schedule = null
                    )
                )
            }

            // Ordenar para que aparezcan en el orden correcto
            val sorted = mutablePrefs.sortedBy {
                when (it.notificationType) {
                    NotificationType.CHECKIN_REMINDER -> 1
                    NotificationType.INFO -> 2
                    NotificationType.SYSTEM_UPDATE -> 3
                    else -> 4
                }
            }

            android.util.Log.d("NotifPrefVM", "Preferencias finales después de ensure: ${sorted.map { "${it.notificationType}=${it.isEnabled}, schedule=${it.schedule}" }}")
            return sorted
        } catch (e: Exception) {
            android.util.Log.e("NotifPrefVM", "Error en ensureMainPreferences", e)
            // Si falla, retornar lista vacía para evitar crash
            return emptyList()
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)

                val userId = userSession.getUser()?.id ?: return@launch

                preferenceRepository.resetToDefaults(userId).fold(
                    onSuccess = { preferences ->
                        val enrichedPreferences = ensureMainPreferences(preferences ?: emptyList())
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
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Error inesperado al restaurar: ${e.message}"
                )
            }
        }
    }
}