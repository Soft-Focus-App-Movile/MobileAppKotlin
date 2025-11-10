package com.softfocus.features.ai.presentation.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.features.ai.domain.models.ChatSession
import com.softfocus.features.ai.domain.repositories.AIChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AIWelcomeViewModel(
    private val repository: AIChatRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AIWelcomeState())
    val state: StateFlow<AIWelcomeState> = _state.asStateFlow()

    init {
        loadSessions()
    }

    fun loadSessions() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingSessions = true) }

            repository.getChatSessions(pageSize = 10)
                .onSuccess { sessions ->
                    _state.update {
                        it.copy(
                            sessions = sessions,
                            isLoadingSessions = false
                        )
                    }
                }
                .onFailure {
                    _state.update {
                        it.copy(isLoadingSessions = false)
                    }
                }
        }
    }
}

data class AIWelcomeState(
    val sessions: List<ChatSession> = emptyList(),
    val isLoadingSessions: Boolean = false
)
