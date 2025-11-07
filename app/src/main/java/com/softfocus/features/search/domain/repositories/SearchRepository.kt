package com.softfocus.features.search.domain.repositories

import com.softfocus.features.search.domain.models.Psychologist

interface SearchRepository {
    suspend fun searchPsychologists(
        page: Int = 1,
        pageSize: Int = 20,
        specialties: List<String>? = null,
        city: String? = null,
        minRating: Double? = null,
        isAcceptingNewPatients: Boolean? = null,
        languages: List<String>? = null,
        searchTerm: String? = null,
        sortBy: String? = null,
        sortDescending: Boolean = false
    ): Result<Pair<List<Psychologist>, Int>>

    suspend fun getPsychologistById(id: String): Result<Psychologist>
}
