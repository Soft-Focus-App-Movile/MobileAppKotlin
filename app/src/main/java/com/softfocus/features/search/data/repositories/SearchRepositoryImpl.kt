package com.softfocus.features.search.data.repositories

import com.softfocus.features.search.data.remote.PsychologistSearchService
import com.softfocus.features.search.domain.models.Psychologist
import com.softfocus.features.search.domain.repositories.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val service: PsychologistSearchService
) : SearchRepository {

    override suspend fun searchPsychologists(
        page: Int,
        pageSize: Int,
        specialties: List<String>?,
        city: String?,
        minRating: Double?,
        isAcceptingNewPatients: Boolean?,
        languages: List<String>?,
        searchTerm: String?,
        sortBy: String?,
        sortDescending: Boolean
    ): Result<Pair<List<Psychologist>, Int>> {
        return try {
            val response = service.searchPsychologists(
                page = page,
                pageSize = pageSize,
                specialties = specialties,
                city = city,
                minRating = minRating,
                isAcceptingNewPatients = isAcceptingNewPatients,
                languages = languages,
                searchTerm = searchTerm,
                sortBy = sortBy,
                sortDescending = sortDescending
            )

            val psychologists = response.psychologists.map { it.toDomain() }
            val totalCount = response.pagination.totalCount

            Result.success(Pair(psychologists, totalCount))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPsychologistById(id: String): Result<Psychologist> {
        return try {
            val response = service.getPsychologistById(id)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
