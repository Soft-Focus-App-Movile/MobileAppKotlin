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
                    _state.value = _state.value.copy(
                        preferences = preferences,
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
        updatePreference(preference.copy(isEnabled = !preference.isEnabled))
    }

    fun updateDeliveryMethod(preference: NotificationPreference, method: DeliveryMethod) {
        updatePreference(preference.copy(deliveryMethod = method))
    }

    fun updateSchedule(preference: NotificationPreference, schedule: NotificationSchedule?) {
        updatePreference(preference.copy(schedule = schedule))
    }

    private fun updatePreference(updated: NotificationPreference) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, successMessage = null)

            val updatedList = _state.value.preferences.map {
                if (it.id == updated.id) updated else it
            }

            updatePreferencesUseCase(updatedList).fold(
                onSuccess = { preferences ->
                    _state.value = _state.value.copy(
                        preferences = preferences,
                        isSaving = false,
                        successMessage = "Preferencias actualizadas"
                    )

                    // Clear success message after 3 seconds
                    kotlinx.coroutines.delay(3000)
                    _state.value = _state.value.copy(successMessage = null)
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isSaving = false,
                        error = error.message ?: "Error al actualizar"
                    )
                }
            )
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val userId = userSession.getUser()?.id ?: return@launch

            preferenceRepository.resetToDefaults(userId).fold(
                onSuccess = { preferences ->
                    _state.value = _state.value.copy(
                        preferences = preferences,
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