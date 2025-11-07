package com.softfocus.features.search.presentation.search

import com.softfocus.features.search.domain.models.Psychologist

data class SearchPsychologistState(
    val psychologists: List<Psychologist> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedSpecialties: List<String> = emptyList(),
    val selectedCity: String? = null,
    val minRating: Double? = null,
    val onlyAvailable: Boolean = false,
    val page: Int = 1,
    val hasMore: Boolean = true,
    val totalCount: Int = 0
)
