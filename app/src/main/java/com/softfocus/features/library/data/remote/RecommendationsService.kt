package com.softfocus.features.library.data.remote

import com.softfocus.core.networking.ApiConstants
import com.softfocus.features.library.data.models.response.ContentListResponseDto
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Servicio Retrofit para recomendaciones de contenido y lugares
 */
interface RecommendationsService {

    /**
     * Obtiene recomendaciones de contenido basadas en la emoción actual del usuario
     *
     * @param token Token de autenticación Bearer
     * @param contentType Filtro opcional por tipo de contenido
     * @param limit Número máximo de resultados (1-50, default: 10)
     * @return Lista de contenido recomendado
     *
     * Funcionamiento:
     * 1. Intenta obtener emoción del contexto de Tracking
     * 2. Por defecto usa emoción "Calm"
     *
     * Ejemplo de uso:
     * ```
     * // Obtener contenido recomendado según emoción del usuario
     * val recommendations = getRecommendedContent("Bearer $token")
     *
     * // Filtrar por tipo de contenido
     * val movies = getRecommendedContent(
     *     token = "Bearer $token",
     *     contentType = "Movie",
     *     limit = 20
     * )
     * ```
     */
    @GET(ApiConstants.Library.RECOMMEND_CONTENT)
    suspend fun getRecommendedContent(
        @Header("Authorization") token: String,
        @Query("contentType") contentType: String? = null,
        @Query("limit") limit: Int = 10
    ): ContentListResponseDto

    /**
     * Obtiene recomendaciones de contenido para una emoción específica
     *
     * @param token Token de autenticación Bearer
     * @param emotion Emoción específica (Happy, Sad, Anxious, Calm, Energetic)
     * @param contentType Filtro opcional por tipo de contenido
     * @param limit Número máximo de resultados (1-100, default: 10)
     * @return Lista de contenido filtrado por emoción
     *
     * Ejemplo de uso:
     * ```
     * // Obtener contenido alegre
     * val happyContent = getRecommendedByEmotion(
     *     token = "Bearer $token",
     *     emotion = "Happy"
     * )
     *
     * // Películas calmantes
     * val calmMovies = getRecommendedByEmotion(
     *     token = "Bearer $token",
     *     emotion = "Calm",
     *     contentType = "Movie",
     *     limit = 15
     * )
     * ```
     */
    @GET(ApiConstants.Library.RECOMMEND_BY_EMOTION)
    suspend fun getRecommendedByEmotion(
        @Header("Authorization") token: String,
        @Path("emotion") emotion: String,
        @Query("contentType") contentType: String? = null,
        @Query("limit") limit: Int = 10
    ): ContentListResponseDto
}
