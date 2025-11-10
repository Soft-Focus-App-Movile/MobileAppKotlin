package com.softfocus.features.library.domain.repositories

import com.softfocus.features.library.domain.models.Assignment
import com.softfocus.features.library.domain.models.ContentItem
import com.softfocus.features.library.domain.models.ContentType
import com.softfocus.features.library.domain.models.EmotionalTag
import com.softfocus.features.library.domain.models.Favorite
import com.softfocus.features.library.domain.models.WeatherCondition

/**
 * Interfaz del repositorio de Library (Domain Layer)
 * Define los contratos para acceder a datos de contenido multimedia
 */
interface LibraryRepository {

    // ============================================================
    // CONTENT SEARCH
    // ============================================================

    suspend fun getContentById(
        contentId: String
    ): Result<ContentItem>

    suspend fun searchContent(
        query: String,
        contentType: ContentType,
        emotionFilter: EmotionalTag? = null,
        limit: Int = 20
    ): Result<List<ContentItem>>

    // ============================================================
    // FAVORITES (General y Patient only)
    // ============================================================

    /**
     * Obtiene todos los favoritos del usuario autenticado
     *
     * @param contentType Filtro opcional por tipo de contenido
     * @param emotionFilter Filtro opcional por emoción
     * @return Result con lista de favoritos
     */
    suspend fun getFavorites(
        contentType: ContentType? = null,
        emotionFilter: EmotionalTag? = null
    ): Result<List<Favorite>>

    /**
     * Agrega un contenido a favoritos
     *
     * @param contentId ID externo del contenido
     * @param contentType Tipo de contenido
     * @return Result con el favorito creado
     */
    suspend fun addFavorite(
        contentId: String,
        contentType: ContentType
    ): Result<Favorite>

    /**
     * Elimina un contenido de favoritos
     *
     * @param favoriteId ID del favorito a eliminar
     * @return Result indicando éxito o error
     */
    suspend fun deleteFavorite(
        favoriteId: String
    ): Result<Unit>

    // ============================================================
    // ASSIGNMENTS - PATIENT SIDE
    // ============================================================

    /**
     * Obtiene el contenido asignado al paciente autenticado
     *
     * @param completed Filtro opcional por estado de completitud
     * @return Result con lista de asignaciones
     */
    suspend fun getAssignedContent(
        completed: Boolean? = null
    ): Result<List<Assignment>>

    /**
     * Marca una asignación como completada
     *
     * @param assignmentId ID de la asignación
     * @return Result con el ID y fecha de completitud
     */
    suspend fun completeAssignment(
        assignmentId: String
    ): Result<Pair<String, String>> // (assignmentId, completedAt)

    // ============================================================
    // ASSIGNMENTS - PSYCHOLOGIST SIDE
    // ============================================================

    /**
     * Asigna contenido a uno o más pacientes
     *
     * @param patientIds Lista de IDs de pacientes
     * @param contentId ID externo del contenido
     * @param contentType Tipo de contenido
     * @param notes Notas opcionales del psicólogo
     * @return Result con lista de IDs de asignaciones creadas
     */
    suspend fun assignContent(
        patientIds: List<String>,
        contentId: String,
        contentType: ContentType,
        notes: String? = null
    ): Result<List<String>>

    /**
     * Obtiene todas las asignaciones creadas por el psicólogo autenticado
     *
     * @param patientId Filtro opcional por ID de paciente específico
     * @return Result con lista de asignaciones
     */
    suspend fun getPsychologistAssignments(
        patientId: String? = null
    ): Result<List<Assignment>>

    // ============================================================
    // RECOMMENDATIONS
    // ============================================================

    /**
     * Obtiene recomendaciones de lugares basadas en clima actual
     *
     * @param latitude Latitud de la ubicación
     * @param longitude Longitud de la ubicación
     * @param emotionFilter Filtro opcional por emoción
     * @param limit Número máximo de resultados (1-50)
     * @return Result con condición climática y lista de lugares
     */
    suspend fun getWeather(
        latitude: Double,
        longitude: Double
    ): Result<WeatherCondition>

    /**
     * Obtiene recomendaciones de contenido basadas en la emoción del usuario
     *
     * @param contentType Filtro opcional por tipo de contenido
     * @param limit Número máximo de resultados (1-50)
     * @return Result con lista de contenido recomendado
     */
    suspend fun getRecommendedContent(
        contentType: ContentType? = null,
        limit: Int = 10
    ): Result<List<ContentItem>>

    /**
     * Obtiene recomendaciones de contenido para una emoción específica
     *
     * @param emotion Emoción específica
     * @param contentType Filtro opcional por tipo de contenido
     * @param limit Número máximo de resultados (1-100)
     * @return Result con lista de contenido filtrado por emoción
     */
    suspend fun getRecommendedByEmotion(
        emotion: EmotionalTag,
        contentType: ContentType? = null,
        limit: Int = 10
    ): Result<List<ContentItem>>
}
