// En: soft-focus-app-movile/frontend/MobileAppKotlin-develop/app/src/main/java/com/softfocus/features/therapy/presentation/psychologist/patiendetail/PatientDetailViewModel.kt
package com.softfocus.features.therapy.presentation.psychologist.patiendetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// (Aquí irían tus clases de estado, por ejemplo)
// data class PatientDetailState(val isLoading: Boolean = true, val patient: Patient? = null)

data class PatientSummaryState(
    val patientName: String = "",
    val age: Int = 0,
    val formattedStartDate: String = "",
    val profilePhotoUrl: String = ""
)

class PatientDetailViewModel(
    savedStateHandle: SavedStateHandle
    // Aquí deberías inyectar tus UseCases o Repositorios
) : ViewModel() {

    // Ejemplo de cómo manejarías el estado
    // private val _uiState = MutableStateFlow(PatientDetailState())
    // val uiState = _uiState.asStateFlow()
    private val _summaryState = MutableStateFlow(PatientSummaryState())
    val summaryState: StateFlow<PatientSummaryState> = _summaryState.asStateFlow()

    private val patientId: String = savedStateHandle["patientId"] ?: ""
    private val relationshipId: String = savedStateHandle["relationshipId"] ?: ""

    init {
        val patientName: String = savedStateHandle["patientName"] ?: "Paciente"
        val age: Int = savedStateHandle.get<String>("age")?.toIntOrNull() ?: 0
        val encodedStartDate: String = savedStateHandle["startDate"] ?: ""

        // Decodificar y formatear la fecha
        val startDate = try { URLDecoder.decode(encodedStartDate, "UTF-8") } catch (e: Exception) { "" }

        val profilePhotoUrl: String = savedStateHandle["photoUrl"] ?: ""

        _summaryState.value = PatientSummaryState(
            patientName = patientName,
            age = age,
            formattedStartDate = formatStartDate(startDate),
            profilePhotoUrl = profilePhotoUrl
        )
        loadPatientDetails()
    }

    private fun loadPatientDetails() {
        viewModelScope.launch {
            // Lógica para cargar datos...
            // Ejemplo:
            // _uiState.value = PatientDetailState(isLoading = true)
            // val result = getPatientDetailsUseCase(patientId, relationshipId)
            // ... manejar el resultado ...
            println("Cargando detalles para patientId: $patientId, relationshipId: $relationshipId")
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
}