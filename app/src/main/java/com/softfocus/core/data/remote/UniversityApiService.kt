package com.softfocus.core.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface UniversityApiService {
    @GET("search")
    suspend fun searchUniversities(
        @Query("country") country: String = "Peru",
        @Query("name") name: String
    ): List<UniversityDto>
}

data class UniversityDto(
    val name: String,
    val country: String,
    val alpha_two_code: String,
    val web_pages: List<String>,
    val domains: List<String>,
    val state_province: String?
)
