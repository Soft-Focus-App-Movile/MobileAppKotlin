package com.softfocus.features.therapy.presentation.psychologist.patiendetail.tabs

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.core.data.local.UserSession
import com.softfocus.features.therapy.data.remote.SignalRService
import com.softfocus.features.therapy.domain.models.ChatMessage
import com.softfocus.features.therapy.domain.usecases.GetChatHistoryUseCase
import com.softfocus.features.therapy.domain.usecases.GetPatientProfileUseCase
import com.softfocus.features.therapy.domain.usecases.GetRelationshipWithPatientUseCase
import com.softfocus.features.therapy.domain.usecases.SendChatMessageUseCase
import com.softfocus.features.therapy.presentation.psychologist.patiendetail.PatientSummaryState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// 1. ESTADO DE LA UI
data class PatientChatUiState(
    val isLoading: Boolean = true,
    val patientName: String = "",
    val patientProfileUrl: String? = null,
    val messages: List<ChatMessage> = emptyList(),
    val error: String? = null
)

// 2. VIEWMODEL
class PatientChatViewModel(
    savedStateHandle: SavedStateHandle,
    private val getPatientProfileUseCase: GetPatientProfileUseCase,
    private val getRelationshipWithPatientUseCase: GetRelationshipWithPatientUseCase,
    private val getChatHistoryUseCase: GetChatHistoryUseCase,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val signalRService: SignalRService,
    private val userSession: UserSession
) : ViewModel() {

    private val _uiState = MutableStateFlow(PatientChatUiState())
    val uiState: StateFlow<PatientChatUiState> = _uiState.asStateFlow()

    private val _summaryState = MutableStateFlow(PatientSummaryState())
    val summaryState: StateFlow<PatientSummaryState> = _summaryState.asStateFlow()

    // Argumentos de navegación
    private val patientId: String = checkNotNull(savedStateHandle["patientId"])
    private val patientName: String = checkNotNull(savedStateHandle["patientName"])
    private val patientProfileUrl: String? = savedStateHandle["profilePhotoUrl"]

    // IDs
    private var psychologistId: String? = null
    private var relationshipId: String? = null

    // Formateador de hora
    private val timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale("es", "ES"))

    init {
        _uiState.update {
            it.copy(
                patientName = this.patientName,
                patientProfileUrl = this.patientProfileUrl
            )
        }
        psychologistId = userSession.getUser()?.id

        if (psychologistId == null) {
            _uiState.update { it.copy(isLoading = false, error = "Error: No se pudo obtener el ID del psicólogo.") }
        } else {
            loadPatientDetails()
            initializeChat()
        }
    }

    private fun initializeChat() {
        viewModelScope.launch(Dispatchers.IO) { // Usar IO para operaciones de red
            try {
                // 1. Obtener el RelationshipId
                val relationshipResult = getRelationshipWithPatientUseCase(patientId)
                if (relationshipResult.isFailure) {
                    throw Exception(relationshipResult.exceptionOrNull()?.message ?: "Error al obtener ID de relación")
                }
                relationshipId = relationshipResult.getOrThrow()

                // 2. Configurar SignalR
                signalRService.initConnection()

                // 3. Registrar el listener ANTES de conectar
                signalRService.registerMessageHandler { newMessage ->
                    _uiState.update { state ->
                        // Añade el nuevo mensaje al inicio de la lista
                        state.copy(messages = listOf(newMessage) + state.messages)
                    }
                }

                // 4. Conectar y cargar historial
                signalRService.startConnection {
                    loadChatHistory() // Cargar historial una vez conectado
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
            }
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

    private fun loadChatHistory() {

        viewModelScope.launch {

            val relationshipResult = getRelationshipWithPatientUseCase(patientId)
            if (relationshipResult.isFailure) {
                throw Exception(relationshipResult.exceptionOrNull()?.message ?: "Error al obtener ID de relación")
            }
            relationshipId = relationshipResult.getOrThrow()

            _uiState.update { it.copy(isLoading = true) }

            getChatHistoryUseCase("$relationshipId", page = 1, size = 50) // Carga los 50 más recientes
                .onSuccess { history ->
                    _uiState.update { it.copy(isLoading = false, messages = history) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun sendMessage(content: String) {

        viewModelScope.launch(Dispatchers.IO) {
            val relationshipResult = getRelationshipWithPatientUseCase(patientId)
            if (relationshipResult.isFailure) {
                throw Exception(relationshipResult.exceptionOrNull()?.message ?: "Error al obtener ID de relación")
            }
            relationshipId = relationshipResult.getOrThrow()
            val psyId = userSession.getUser()?.id

            // 1. Crear mensaje local y añadirlo a la UI (Actualización optimista)
            val localMessage = ChatMessage(
                id = System.currentTimeMillis().toString(), // ID temporal
                relationshipId = "$relationshipId",
                senderId = "$psyId",
                receiverId = patientId,
                content = content,
                timestamp = ZonedDateTime.now().toString(),
                isFromMe = true,
                messageType = "text"
            )
            _uiState.update { it.copy(messages = listOf(localMessage) + it.messages) }

            sendChatMessageUseCase("$relationshipId", patientId, content, "text")
                .onFailure { error ->
                    // Opcional: Marcar el mensaje local como "fallido" en la UI
                    withContext(Dispatchers.Main) {
                        _uiState.update { it.copy(error = "Error al enviar: ${error.message}") }
                    }
                }
            // No necesitamos .onSuccess porque el psicólogo no recibe su propio
            // mensaje por SignalR. La actualización optimista es suficiente.
        }
    }

    // 3. Formatear la hora
    fun formatTimestamp(isoTimestamp: String): String {
        return try {
            ZonedDateTime.parse(isoTimestamp)
                .withZoneSameInstant(java.time.ZoneId.systemDefault())
                .format(timeFormatter)
        } catch (e: Exception) {
            "..." // Fallback
        }
    }

    // 4. Limpiar conexión al salir
    override fun onCleared() {
        viewModelScope.launch(Dispatchers.IO) {
            signalRService.stopConnection()
        }
        super.onCleared()
    }
}