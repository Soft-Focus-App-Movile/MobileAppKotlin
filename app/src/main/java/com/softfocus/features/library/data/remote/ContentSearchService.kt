package com.softfocus.features.library.data.remote

import com.softfocus.core.networking.ApiConstants
import com.softfocus.features.library.data.models.request.ContentSearchRequestDto
import com.softfocus.features.library.data.models.response.ContentItemResponseDto
import com.softfocus.features.library.data.models.response.ContentSearchResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Servicio Retrofit para búsqueda de contenido multimedia
 */
interface ContentSearchService {

    @GET(ApiConstants.Library.CONTENT_BY_ID)
    suspend fun getContentById(
        @Header("Authorization") token: String,
        @Path("contentId") contentId: String
    ): ContentItemResponseDto

    @POST(ApiConstants.Library.SEARCH)
    suspend fun searchContent(
        @Header("Authorization") token: String,
        @Body request: ContentSearchRequestDto
    ): ContentSearchResponseDto
}
