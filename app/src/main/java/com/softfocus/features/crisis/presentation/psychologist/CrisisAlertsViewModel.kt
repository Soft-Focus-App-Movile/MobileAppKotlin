package com.softfocus.features.crisis.presentation.psychologist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.features.crisis.domain.models.CrisisAlert
import com.softfocus.features.crisis.domain.repositories.CrisisRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CrisisAlertsViewModel(
    private val crisisRepository: CrisisRepository
) : ViewModel() {

    private val _alertsState = MutableStateFlow<CrisisAlertsState>(CrisisAlertsState.Loading)
    val alertsState: StateFlow<CrisisAlertsState> = _alertsState.asStateFlow()

    private val _selectedSeverity = MutableStateFlow<String?>(null)
    val selectedSeverity: StateFlow<String?> = _selectedSeverity.asStateFlow()

    init {
        loadAlerts()
    }

    fun loadAlerts(severity: String? = null) {
        viewModelScope.launch {
            _alertsState.value = CrisisAlertsState.Loading
            _selectedSeverity.value = severity

            val result = crisisRepository.getPsychologistAlerts(
                severity = severity,
                status = null,
                limit = null
            )

            result.onSuccess { alerts ->
                _alertsState.value = if (alerts.isEmpty()) {
                    CrisisAlertsState.Empty
                } else {
                    CrisisAlertsState.Success(alerts)
                }
            }.onFailure { error ->
                _alertsState.value = CrisisAlertsState.Error(error.message ?: "Error al cargar alertas")
            }
        }
    }

    fun updateAlertStatus(alert: CrisisAlert) {
        viewModelScope.launch {
            val nextStatus = getNextStatus(alert.status)

            val result = crisisRepository.updateAlertStatus(alert.id, nextStatus)

            result.onSuccess {
                loadAlerts(_selectedSeverity.value)
            }.onFailure { error ->
                _alertsState.value = CrisisAlertsState.Error(error.message ?: "Error al actualizar estado")
            }
        }
    }

    private fun getNextStatus(currentStatus: String): String {
        return when (currentStatus.uppercase()) {
            "PENDING" -> "Attended"
            "ATTENDED" -> "Resolved"
            "RESOLVED" -> "Pending"
            else -> "Pending"
        }
    }

    fun retry() {
        loadAlerts(_selectedSeverity.value)
    }
}

sealed class CrisisAlertsState {
    object Loading : CrisisAlertsState()
    object Empty : CrisisAlertsState()
    data class Success(val alerts: List<CrisisAlert>) : CrisisAlertsState()
    data class Error(val message: String) : CrisisAlertsState()
}
