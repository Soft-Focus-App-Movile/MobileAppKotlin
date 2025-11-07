package com.softfocus.features.search.data.remote

import com.softfocus.core.networking.ApiConstants
import com.softfocus.features.search.data.models.response.PsychologistDirectoryResponseDto
import com.softfocus.features.search.data.models.response.PsychologistResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Servicio Retrofit para búsqueda de psicólogos
 */
interface PsychologistSearchService {

    @GET(ApiConstants.Users.PSYCHOLOGISTS_DIRECTORY)
    suspend fun searchPsychologists(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("specialties") specialties: List<String>? = null,
        @Query("city") city: String? = null,
        @Query("minRating") minRating: Double? = null,
        @Query("isAcceptingNewPatients") isAcceptingNewPatients: Boolean? = null,
        @Query("languages") languages: List<String>? = null,
        @Query("searchTerm") searchTerm: String? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("sortDescending") sortDescending: Boolean = false
    ): PsychologistDirectoryResponseDto

    @GET(ApiConstants.Users.PSYCHOLOGIST_DETAIL)
    suspend fun getPsychologistById(
        @Path("id") id: String
    ): PsychologistResponseDto
}
