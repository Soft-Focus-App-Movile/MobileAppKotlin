package com.softfocus.features.search.presentation.search

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
class SearchPsychologistViewModel @Inject constructor(
    private val repository: SearchRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SearchPsychologistState())
    val state: StateFlow<SearchPsychologistState> = _state.asStateFlow()

    init {
        // Cargar psicólogos inicialmente
        searchPsychologists()
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query, page = 1) }
        searchPsychologists()
    }

    fun onSpecialtyToggle(specialty: String) {
        _state.update {
            val newSpecialties = if (specialty in it.selectedSpecialties) {
                it.selectedSpecialties - specialty
            } else {
                it.selectedSpecialties + specialty
            }
            it.copy(selectedSpecialties = newSpecialties, page = 1)
        }
        searchPsychologists()
    }

    fun onCityChange(city: String?) {
        _state.update { it.copy(selectedCity = city, page = 1) }
        searchPsychologists()
    }

    fun onMinRatingChange(rating: Double?) {
        _state.update { it.copy(minRating = rating, page = 1) }
        searchPsychologists()
    }

    fun onAvailabilityToggle() {
        _state.update { it.copy(onlyAvailable = !it.onlyAvailable, page = 1) }
        searchPsychologists()
    }

    fun clearFilters() {
        _state.update {
            SearchPsychologistState(
                searchQuery = it.searchQuery,
                page = 1
            )
        }
        searchPsychologists()
    }

    fun loadNextPage() {
        if (!_state.value.isLoading && _state.value.hasMore) {
            _state.update { it.copy(page = it.page + 1) }
            searchPsychologists(append = true)
        }
    }

    fun retry() {
        searchPsychologists()
    }

    private fun searchPsychologists(append: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = repository.searchPsychologists(
                page = _state.value.page,
                pageSize = 20,
                specialties = _state.value.selectedSpecialties.ifEmpty { null },
                city = _state.value.selectedCity,
                minRating = _state.value.minRating,
                isAcceptingNewPatients = if (_state.value.onlyAvailable) true else null,
                searchTerm = _state.value.searchQuery.ifBlank { null },
                sortBy = "rating",
                sortDescending = true
            )

            result.fold(
                onSuccess = { (psychologists, totalCount) ->
                    _state.update {
                        val newPsychologists = if (append) {
                            it.psychologists + psychologists
                        } else {
                            psychologists
                        }
                        it.copy(
                            psychologists = newPsychologists,
                            totalCount = totalCount,
                            hasMore = newPsychologists.size < totalCount,
                            isLoading = false,
                            error = null
                        )
                    }
                },
                onFailure = { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Error al cargar psicólogos"
                        )
                    }
                }
            )
        }
    }
}
