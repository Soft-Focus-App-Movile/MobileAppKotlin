package com.softfocus.features.therapy.presentation.psychologist.patientlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.features.therapy.domain.models.PatientDirectory
import com.softfocus.features.therapy.domain.usecases.GetMyPatientsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la UI
data class PatientListUiState(
    val isLoading: Boolean = false,
    val patients: List<PatientDirectory> = emptyList(),
    val error: String? = null
)

class PatientListViewModel(
    private val getMyPatientsUseCase: GetMyPatientsUseCase
) : ViewModel() {

    // Flujo de estado mutable y privado
    private val _uiState = MutableStateFlow(PatientListUiState())
    // Flujo de estado público e inmutable
    val uiState: StateFlow<PatientListUiState> = _uiState.asStateFlow()

    // Función para cargar los pacientes, llamada desde la UI
    fun loadPatients() {
        if (_uiState.value.isLoading) return // Evitar cargas múltiples

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getMyPatientsUseCase().onSuccess { patientList ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        patients = patientList
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = error.message ?: "Ocurrió un error desconocido"
                    )
                }
            }
        }
    }
}