package com.softfocus.features.ai.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.features.ai.domain.models.ChatMessage
import com.softfocus.features.ai.domain.models.MessageRole
import com.softfocus.features.ai.domain.repositories.AIChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class AIChatViewModel(
    private val repository: AIChatRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AIChatState())
    val state: StateFlow<AIChatState> = _state.asStateFlow()

    init {
        loadUsageStats()
    }

    fun onMessageChange(message: String) {
        _state.update { it.copy(currentMessage = message) }
    }

    fun sendMessage(initialMessage: String? = null) {
        val messageToSend = initialMessage ?: _state.value.currentMessage

        if (messageToSend.isBlank()) return

        viewModelScope.launch {
            // Agregar mensaje del usuario
            val userMessage = ChatMessage(
                role = MessageRole.USER,
                content = messageToSend,
                timestamp = LocalDateTime.now()
            )

            _state.update {
                it.copy(
                    messages = it.messages + userMessage,
                    currentMessage = "",
                    isLoading = true,
                    error = null
                )
            }

            // Enviar al backend
            repository.sendMessage(messageToSend, _state.value.sessionId)
                .onSuccess { chatResponse ->
                    _state.update {
                        it.copy(
                            messages = it.messages + chatResponse.message,
                            sessionId = chatResponse.sessionId,
                            isLoading = false
                        )
                    }

                    // Actualizar estadísticas después de enviar
                    loadUsageStats()
                }
                .onFailure { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message
                        )
                    }
                }
        }
    }

    fun loadUsageStats() {
        viewModelScope.launch {
            repository.getUsageStats()
                .onSuccess { stats ->
                    _state.update {
                        it.copy(
                            usageStats = stats,
                            showLimitWarning = stats.remainingMessages <= 3 && stats.plan == "Free"
                        )
                    }
                }
                .onFailure {
                    // Silently fail, stats are not critical
                }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun startNewConversation() {
        _state.update {
            AIChatState()
        }
        loadUsageStats()
    }

    fun loadSession(sessionId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            repository.getSessionMessages(sessionId, limit = 50)
                .onSuccess { messages ->
                    _state.update {
                        it.copy(
                            messages = messages,
                            sessionId = sessionId,
                            isLoading = false
                        )
                    }
                }
                .onFailure { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message
                        )
                    }
                }

            loadUsageStats()
        }
    }
}
