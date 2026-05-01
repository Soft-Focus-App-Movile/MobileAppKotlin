package com.softfocus.features.therapy.presentation.patient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.core.data.local.UserSession
import com.softfocus.features.profile.domain.models.AssignedPsychologist
import com.softfocus.features.profile.presentation.PsychologistLoadState
import com.softfocus.features.search.domain.repositories.SearchRepository
import com.softfocus.features.therapy.data.remote.SignalRService
import com.softfocus.features.therapy.domain.models.ChatMessage
import com.softfocus.features.therapy.domain.usecases.GetChatHistoryUseCase
import com.softfocus.features.therapy.domain.usecases.GetMyRelationshipUseCase
import com.softfocus.features.therapy.domain.usecases.SendChatMessageUseCase
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

data class PsychologistChatUiState(
    val isLoading: Boolean = true,
    val patientName: String = "",
    val patientProfileUrl: String? = null,
    val messages: List<ChatMessage> = emptyList(),
    val error: String? = null
)

data class PsychologistSummaryState(
    val isLoading: Boolean = true,
    val psychologistName: String = "Cargando...",
    val profilePhotoUrl: String = ""
)

class PsychologistChatViewModel(
    private val userSession: UserSession,
    private val getMyRelationshipUseCase: GetMyRelationshipUseCase,
    private val getChatHistoryUseCase: GetChatHistoryUseCase,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val signalRService: SignalRService,
    private val searchRepository: SearchRepository
): ViewModel() {

    private val _assignedPsychologist = MutableStateFlow<AssignedPsychologist?>(null)
    val assignedPsychologist: StateFlow<AssignedPsychologist?> = _assignedPsychologist.asStateFlow()

    private val _psychologistLoadState = MutableStateFlow<PsychologistLoadState>(PsychologistLoadState.Loading)
    val psychologistLoadState: StateFlow<PsychologistLoadState> = _psychologistLoadState.asStateFlow()

    private val _uiState = MutableStateFlow(PsychologistChatUiState())
    val uiState: StateFlow<PsychologistChatUiState> = _uiState.asStateFlow()

    private val _summaryState = MutableStateFlow(PsychologistSummaryState())
    val summaryState: StateFlow<PsychologistSummaryState> = _summaryState.asStateFlow()

    // IDs

    private var patientId: String? = null
    private var psychologistId: String? = null
    private var relationshipId: String? = null

    // Formateador de hora
    private val timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale("es", "ES"))

    init {

        patientId = userSession.getUser()?.id

        if (patientId == null) {
            _uiState.update { it.copy(isLoading = false, error = "Error: No se pudo obtener el ID del paciente.") }
        } else {
            initializeChat()
        }
    }

    private fun initializeChat() {
        viewModelScope.launch(Dispatchers.IO) { // Usar IO para operaciones de red
            try {
                // 1. Obtener el RelationshipId
                val relationshipResult = getMyRelationshipUseCase()
                val relationship = relationshipResult.getOrThrow()
                relationshipId = relationship?.id
                psychologistId = relationship?.psychologistId

                loadPsychologistDetails("$psychologistId")

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

    private fun loadPsychologistDetails(psychologistId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // Llama al repositorio para obtener los detalles
            val result = searchRepository.getPsychologistById(psychologistId)

            result.onSuccess { psychologist ->
                // Actualiza el state con la información completa
                withContext(Dispatchers.Main) {
                    _summaryState.update {
                        it.copy(
                            psychologistName = psychologist.fullName,
                            profilePhotoUrl = "${psychologist.profileImageUrl}"
                        )
                    }
                }
            }.onFailure {
                // Opcional: manejar el error.
                // No es crítico porque ya tenemos el nombre básico de la relación.
            }
        }
    }

    private fun loadChatHistory() {

        viewModelScope.launch {

            val relationshipResult = getMyRelationshipUseCase()
            val relationship = relationshipResult.getOrThrow()
            relationshipId = relationship?.id

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
            val relationshipResult = getMyRelationshipUseCase()
            val relationship = relationshipResult.getOrThrow()
            relationshipId = relationship?.id
            patientId = userSession.getUser()?.id
            psychologistId = relationship?.psychologistId

            // 1. Crear mensaje local y añadirlo a la UI (Actualización optimista)
            val localMessage = ChatMessage(
                id = System.currentTimeMillis().toString(), // ID temporal
                relationshipId = "$relationshipId",
                senderId = "$patientId",
                receiverId = "$psychologistId",
                content = content,
                timestamp = ZonedDateTime.now().toString(),
                isFromMe = true,
                messageType = "text"
            )
            _uiState.update { it.copy(messages = listOf(localMessage) + it.messages) }

            sendChatMessageUseCase("$relationshipId", "$psychologistId", content, "text")
                .onFailure { error ->
                    // Opcional: Marcar el mensaje local como "fallido" en la UI
                    withContext(Dispatchers.Main) {
                        _uiState.update { it.copy(error = "Error al enviar: ${error.message}") }
                    }
                }
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