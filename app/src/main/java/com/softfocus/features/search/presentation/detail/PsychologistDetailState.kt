package com.softfocus.features.search.presentation.detail

import com.softfocus.features.search.domain.models.Psychologist

data class PsychologistDetailState(
    val psychologist: Psychologist? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
