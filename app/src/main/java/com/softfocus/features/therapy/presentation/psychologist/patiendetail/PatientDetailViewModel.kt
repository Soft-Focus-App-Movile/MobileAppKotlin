// En: soft-focus-app-movile/frontend/MobileAppKotlin-develop/app/src/main/java/com/softfocus/features/therapy/presentation/psychologist/patiendetail/PatientDetailViewModel.kt
package com.softfocus.features.therapy.presentation.psychologist.patiendetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.features.library.assignments.domain.repositories.AssignmentsRepository
import com.softfocus.features.library.assignments.presentation.AssignmentsUiState
import com.softfocus.features.library.domain.models.Assignment
import com.softfocus.features.therapy.domain.usecases.GetPatientProfileUseCase
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.time.LocalDate
import java.time.Period
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// (Aquí irían tus clases de estado, por ejemplo)
// data class PatientDetailState(val isLoading: Boolean = true, val patient: Patient? = null)

data class PatientSummaryState(
    val isLoading: Boolean = true,
    val patientName: String = "Cargando...", // Valor inicial
    val age: Int = 0,
    val formattedStartDate: String = "",
    val profilePhotoUrl: String = "",
    val error: String? = null
)

data class TasksTabState(
    val isLoading: Boolean = true,
    val assignments: List<Assignment> = emptyList(),
    val error: String? = null
)

class PatientDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val getPatientProfileUseCase: GetPatientProfileUseCase,
    private val repository: AssignmentsRepository
    // Aquí deberías inyectar tus UseCases o Repositorios
) : ViewModel() {

    // Ejemplo de cómo manejarías el estado
    // --- Summary State ---
    private val _summaryState = MutableStateFlow(PatientSummaryState())
    val summaryState: StateFlow<PatientSummaryState> = _summaryState.asStateFlow()

    // --- Tasks State ---
    private val _tasksState = MutableStateFlow<AssignmentsUiState>(AssignmentsUiState.Loading)
    val tasksState: StateFlow<AssignmentsUiState> = _tasksState.asStateFlow()

    private val patientId: String = savedStateHandle.get<String>("patientId") ?: ""
    private val relationshipId: String = savedStateHandle.get<String>("relationshipId") ?: ""
    private val encodedStartDate: String = savedStateHandle.get<String>("startDate") ?: ""

    init {
        val startDate = try { URLDecoder.decode(encodedStartDate, "UTF-8") } catch (e: Exception) { "" }
        _summaryState.update {
            it.copy(formattedStartDate = formatStartDate(startDate))
        }

        if (patientId.isNotBlank()) {
            loadPatientDetails()
            loadPsychologistAssignments()
        } else {
            val errorMsg = "Patient ID inválido"
            _summaryState.update { it.copy(isLoading = false, error = errorMsg) }
        }
    }

    private fun loadPatientDetails() {
        // Nos aseguramos de tener un patientId
        if (patientId.isBlank()) {
            _summaryState.update { it.copy(isLoading = false, error = "ID de paciente inválido") }
            return
        }

        viewModelScope.launch {
            // Ponemos el estado en "cargando"
            _summaryState.update { it.copy(isLoading = true) }

            // Llamamos al nuevo endpoint a través del Caso de Uso
            val profileResult = getPatientProfileUseCase(patientId)

            profileResult.onSuccess { profile ->
                // Éxito: Actualizamos el estado con los datos del perfil
                _summaryState.update {
                    it.copy(
                        isLoading = false,
                        patientName = profile.fullName,
                        age = calculateAge(profile.dateOfBirth),
                        profilePhotoUrl = profile.profilePhotoUrl,
                        error = null
                    )
                }
            }.onFailure { error ->
                // Error: Mostramos un mensaje
                _summaryState.update {
                    it.copy(
                        isLoading = false,
                        patientName = "Error",
                        error = error.message
                    )
                }
            }
        }
    }

    fun loadPsychologistAssignments() {
        viewModelScope.launch {
            _tasksState.value = AssignmentsUiState.Loading

            repository.getPsychologistAssignments(patientId).fold(
                onSuccess = { assignments ->
                    val pending = assignments.count { !it.isCompleted }
                    val completedCount = assignments.count { it.isCompleted }

                    _tasksState.value = AssignmentsUiState.Success(
                        assignments = assignments,
                        pendingCount = pending,
                        completedCount = completedCount
                    )
                },
                onFailure = { exception ->
                    _tasksState.value = AssignmentsUiState.Error(
                        message = exception.message ?: "Error al cargar asignaciones"
                    )
                }
            )
        }
    }

    private fun formatStartDate(isoDate: String): String {
        if (isoDate.isBlank()) return "Fecha desconocida"
        return try {
            val zonedDateTime = ZonedDateTime.parse(isoDate)
            val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "ES"))
            val formatted = zonedDateTime.format(formatter)
            "Paciente desde ${formatted.replaceFirstChar { it.uppercase() }}"
        } catch (e: Exception) {
            // Fallback si el formato falla
            "Paciente desde ${isoDate.substringBefore("T")}"
        }
    }

    /**
     * Calcula la edad a partir de una fecha de nacimiento en string (ej. "2000-11-20T...")
     */
    private fun calculateAge(isoDateOfBirth: String?): Int {
        if (isoDateOfBirth.isNullOrBlank()) return 0
        return try {
            // Extrae solo la parte de la fecha (YYYY-MM-DD)
            val date = isoDateOfBirth.substringBefore("T")
            val birthDate = LocalDate.parse(date)
            Period.between(birthDate, LocalDate.now()).years
        } catch (e: Exception) {
            0 // Retorna 0 si el formato es inválido
        }
    }
}