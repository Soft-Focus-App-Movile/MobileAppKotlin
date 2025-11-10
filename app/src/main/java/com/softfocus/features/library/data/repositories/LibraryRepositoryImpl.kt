package com.softfocus.features.library.data.repositories

import android.content.Context
import com.softfocus.core.data.local.UserSession
import com.softfocus.features.library.data.models.request.AssignmentRequestDto
import com.softfocus.features.library.data.models.request.ContentSearchRequestDto
import com.softfocus.features.library.data.models.request.FavoriteRequestDto
import com.softfocus.features.library.data.remote.AssignmentsService
import com.softfocus.features.library.data.remote.ContentSearchService
import com.softfocus.features.library.data.remote.FavoritesService
import com.softfocus.features.library.data.remote.RecommendationsService
import com.softfocus.features.library.domain.models.Assignment
import com.softfocus.features.library.domain.models.ContentItem
import com.softfocus.features.library.domain.models.ContentType
import com.softfocus.features.library.domain.models.EmotionalTag
import com.softfocus.features.library.domain.models.Favorite
import com.softfocus.features.library.domain.models.WeatherCondition
import com.softfocus.features.library.domain.repositories.LibraryRepository

/**
 * Implementación del repositorio de Library (Data Layer)
 * Maneja la comunicación con los servicios Retrofit y transforma DTOs a modelos de dominio
 */
class LibraryRepositoryImpl(
    private val contentSearchService: ContentSearchService,
    private val favoritesService: FavoritesService,
    private val assignmentsService: AssignmentsService,
    private val recommendationsService: RecommendationsService,
    private val context: Context
) : LibraryRepository {

    private val userSession = UserSession(context)

    /**
     * Obtiene el token de autenticación del usuario actual
     */
    private fun getAuthToken(): String {
        val token = userSession.getUser()?.token
        return "Bearer $token"
    }

    // ============================================================
    // CONTENT SEARCH
    // ============================================================

    override suspend fun getContentById(contentId: String): Result<ContentItem> {
        return try {
            val response = contentSearchService.getContentById(
                token = getAuthToken(),
                contentId = contentId
            )

            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(Exception("Error al obtener contenido: ${e.message}", e))
        }
    }

    override suspend fun searchContent(
        query: String,
        contentType: ContentType,
        emotionFilter: EmotionalTag?,
        limit: Int
    ): Result<List<ContentItem>> {
        return try {
            val request = ContentSearchRequestDto(
                query = query,
                contentType = contentType.name,
                emotionFilter = emotionFilter?.name,
                limit = limit
            )

            android.util.Log.d("LibraryRepository", "Buscando: query='$query', type=${contentType.name}")

            val response = contentSearchService.searchContent(
                token = getAuthToken(),
                request = request
            )

            android.util.Log.d("LibraryRepository", "Búsqueda exitosa: ${response.results.size} resultados de ${response.totalResults} totales")

            val contentItems = response.results.map { it.toDomain() }
            Result.success(contentItems)
        } catch (e: retrofit2.HttpException) {
            val errorMsg = when (e.code()) {
                404 -> "Endpoint de búsqueda no encontrado (404). Verifica que el backend esté actualizado."
                500 -> "Error del servidor (500). El backend no pudo procesar la búsqueda."
                401 -> "No autorizado (401). Tu sesión puede haber expirado."
                else -> "Error HTTP ${e.code()}: ${e.message()}"
            }
            android.util.Log.e("LibraryRepository", "Error HTTP en búsqueda: $errorMsg", e)
            Result.failure(Exception(errorMsg, e))
        } catch (e: java.net.UnknownHostException) {
            val errorMsg = "Sin conexión a internet. Verifica tu red."
            android.util.Log.e("LibraryRepository", errorMsg, e)
            Result.failure(Exception(errorMsg, e))
        } catch (e: java.net.SocketTimeoutException) {
            val errorMsg = "Tiempo de espera agotado. El servidor no responde."
            android.util.Log.e("LibraryRepository", errorMsg, e)
            Result.failure(Exception(errorMsg, e))
        } catch (e: Exception) {
            val errorMsg = "Error inesperado: ${e.message}"
            android.util.Log.e("LibraryRepository", errorMsg, e)
            Result.failure(Exception(errorMsg, e))
        }
    }

    // ============================================================
    // FAVORITES (General y Patient only)
    // ============================================================

    override suspend fun getFavorites(
        contentType: ContentType?,
        emotionFilter: EmotionalTag?
    ): Result<List<Favorite>> {
        return try {
            val response = favoritesService.getFavorites(
                token = getAuthToken(),
                contentType = contentType?.name,
                emotionFilter = emotionFilter?.name
            )

            val favorites = response.map { it.toDomain() }
            Result.success(favorites)
        } catch (e: Exception) {
            Result.failure(Exception("Error al obtener favoritos: ${e.message}", e))
        }
    }

    override suspend fun addFavorite(
        contentId: String,
        contentType: ContentType
    ): Result<Favorite> {
        return try {
            val request = FavoriteRequestDto(
                contentId = contentId,
                contentType = contentType.name
            )

            val response = favoritesService.addFavorite(
                token = getAuthToken(),
                request = request
            )

            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(Exception("Error al agregar favorito: ${e.message}", e))
        }
    }

    override suspend fun deleteFavorite(favoriteId: String): Result<Unit> {
        return try {
            favoritesService.deleteFavorite(
                token = getAuthToken(),
                favoriteId = favoriteId
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al eliminar favorito: ${e.message}", e))
        }
    }

    // ============================================================
    // ASSIGNMENTS - PATIENT SIDE
    // ============================================================

    override suspend fun getAssignedContent(
        completed: Boolean?
    ): Result<List<Assignment>> {
        return try {
            val response = assignmentsService.getAssignedContent(
                token = getAuthToken(),
                completed = completed
            )

            val assignments = response.map { it.toDomain() }
            Result.success(assignments)
        } catch (e: Exception) {
            Result.failure(Exception("Error al obtener asignaciones: ${e.message}", e))
        }
    }

    override suspend fun completeAssignment(
        assignmentId: String
    ): Result<Pair<String, String>> {
        return try {
            val response = assignmentsService.completeAssignment(
                token = getAuthToken(),
                assignmentId = assignmentId
            )

            Result.success(Pair(response.assignmentId, response.completedAt))
        } catch (e: Exception) {
            Result.failure(Exception("Error al completar asignación: ${e.message}", e))
        }
    }

    // ============================================================
    // ASSIGNMENTS - PSYCHOLOGIST SIDE
    // ============================================================

    override suspend fun assignContent(
        patientIds: List<String>,
        contentId: String,
        contentType: ContentType,
        notes: String?
    ): Result<List<String>> {
        return try {
            val request = AssignmentRequestDto(
                patientIds = patientIds,
                contentId = contentId,
                contentType = contentType.name,
                notes = notes
            )

            val response = assignmentsService.assignContent(
                token = getAuthToken(),
                request = request
            )

            Result.success(response.assignmentIds)
        } catch (e: Exception) {
            Result.failure(Exception("Error al asignar contenido: ${e.message}", e))
        }
    }

    override suspend fun getPsychologistAssignments(
        patientId: String?
    ): Result<List<Assignment>> {
        return try {
            val response = assignmentsService.getPsychologistAssignments(
                token = getAuthToken(),
                patientId = patientId
            )

            val assignments = response.map { it.toDomain() }
            Result.success(assignments)
        } catch (e: Exception) {
            Result.failure(Exception("Error al obtener asignaciones del psicólogo: ${e.message}", e))
        }
    }

    // ============================================================
    // RECOMMENDATIONS
    // ============================================================

    override suspend fun getWeather(
        latitude: Double,
        longitude: Double
    ): Result<WeatherCondition> {
        return try {
            val response = recommendationsService.getRecommendedPlaces(
                token = getAuthToken(),
                latitude = latitude,
                longitude = longitude,
                emotionFilter = null,
                limit = 5
            )

            val weather = response.weather.toDomain()
            Result.success(weather)
        } catch (e: Exception) {
            Result.failure(Exception("Error al obtener clima: ${e.message}", e))
        }
    }

    override suspend fun getRecommendedContent(
        contentType: ContentType?,
        limit: Int
    ): Result<List<ContentItem>> {
        return try {
            val response = recommendationsService.getRecommendedContent(
                token = getAuthToken(),
                contentType = contentType?.name,
                limit = limit
            )

            val contentItems = response.content.map { it.toDomain() }
            Result.success(contentItems)
        } catch (e: Exception) {
            Result.failure(Exception("Error al obtener contenido recomendado: ${e.message}", e))
        }
    }

    override suspend fun getRecommendedByEmotion(
        emotion: EmotionalTag,
        contentType: ContentType?,
        limit: Int
    ): Result<List<ContentItem>> {
        return try {
            val response = recommendationsService.getRecommendedByEmotion(
                token = getAuthToken(),
                emotion = emotion.name,
                contentType = contentType?.name,
                limit = limit
            )

            val contentItems = response.content.map { it.toDomain() }
            Result.success(contentItems)
        } catch (e: Exception) {
            Result.failure(Exception("Error al obtener contenido por emoción: ${e.message}", e))
        }
    }
}
