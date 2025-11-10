package com.softfocus.features.library.data.remote

import com.softfocus.core.networking.ApiConstants
import com.softfocus.features.library.data.models.response.ContentListResponseDto
import com.softfocus.features.library.data.models.response.WeatherPlaceResponseDto
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Servicio Retrofit para recomendaciones de contenido y lugares
 */
interface RecommendationsService {

    /**
     * Obtiene recomendaciones de lugares basadas en clima actual
     *
     * @param token Token de autenticación Bearer
     * @param latitude Latitud de la ubicación del usuario (-90 a 90)
     * @param longitude Longitud de la ubicación del usuario (-180 a 180)
     * @param emotionFilter Filtro opcional por emoción
     * @param limit Número máximo de resultados (1-50, default: 10)
     * @return Información del clima + lista de lugares recomendados
     *
     * Funcionamiento:
     * 1. Obtiene clima actual de OpenWeather
     * 2. Determina si es apto para actividades outdoor/indoor
     * 3. Busca lugares en Foursquare según clima y emoción
     *
     * Ejemplo de uso:
     * ```
     * // Obtener lugares recomendados según clima
     * val result = getRecommendedPlaces(
     *     token = "Bearer $token",
     *     latitude = -12.0464,
     *     longitude = -77.0428
     * )
     * println("Clima: ${result.weather.description}")
     * println("${result.recommendedPlaces.size} lugares recomendados")
     *
     * // Con filtro de emoción
     * val calmPlaces = getRecommendedPlaces(
     *     token = "Bearer $token",
     *     latitude = -12.0464,
     *     longitude = -77.0428,
     *     emotionFilter = "Calm"
     * )
     * ```
     */
    @GET(ApiConstants.Library.RECOMMEND_PLACES)
    suspend fun getRecommendedPlaces(
        @Header("Authorization") token: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("emotionFilter") emotionFilter: String? = null,
        @Query("limit") limit: Int = 10
    ): WeatherPlaceResponseDto

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
     * 2. Si no hay, intenta inferir emoción del clima (si hay ubicación)
     * 3. Por defecto usa emoción "Calm"
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
