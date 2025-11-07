package com.softfocus.features.search.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softfocus.features.search.domain.repositories.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PsychologistDetailViewModel @Inject constructor(
    private val repository: SearchRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(PsychologistDetailState())
    val state: StateFlow<PsychologistDetailState> = _state.asStateFlow()

    private val psychologistId: String = checkNotNull(savedStateHandle["psychologistId"])

    init {
        loadPsychologist()
    }

    fun retry() {
        loadPsychologist()
    }

    private fun loadPsychologist() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = repository.getPsychologistById(psychologistId)

            result.fold(
                onSuccess = { psychologist ->
                    _state.update {
                        it.copy(
                            psychologist = psychologist,
                            isLoading = false,
                            error = null
                        )
                    }
                },
                onFailure = { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Error al cargar información del psicólogo"
                        )
                    }
                }
            )
        }
    }
}
