package com.softfocus.features.library.data.remote

import com.softfocus.core.networking.ApiConstants
import com.softfocus.features.library.data.models.request.AssignmentRequestDto
import com.softfocus.features.library.data.models.response.AssignmentCompletedResponseDto
import com.softfocus.features.library.data.models.response.AssignmentCreatedResponseDto
import com.softfocus.features.library.data.models.response.AssignmentResponseDto
import com.softfocus.features.library.data.models.response.AssignmentsResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Servicio Retrofit para gestión de asignaciones de contenido
 */
interface AssignmentsService {

    // ============================================================
    // ENDPOINTS PARA PACIENTES (Patient)
    // ============================================================

    /**
     * Obtiene el contenido asignado al paciente autenticado
     *
     * @param token Token de autenticación Bearer
     * @param completed Filtro opcional por estado de completitud (true/false)
     * @return Objeto con lista de asignaciones y estadísticas (total, pending, completed)
     *
     * Ejemplo de uso:
     * ```
     * // Obtener todas las asignaciones
     * val response = getAssignedContent("Bearer $token")
     * val allAssignments = response.assignments
     * val total = response.total
     *
     * // Solo asignaciones pendientes
     * val pending = getAssignedContent("Bearer $token", completed = false)
     *
     * // Solo asignaciones completadas
     * val completed = getAssignedContent("Bearer $token", completed = true)
     * ```
     */
    @GET(ApiConstants.Library.ASSIGNED_CONTENT)
    suspend fun getAssignedContent(
        @Header("Authorization") token: String,
        @Query("completed") completed: Boolean? = null
    ): AssignmentsResponseDto

    /**
     * Marca una asignación como completada
     *
     * @param token Token de autenticación Bearer
     * @param assignmentId ID de la asignación a completar
     * @return Confirmación con fecha de completitud
     *
     * Nota: Solo puede completarse una vez, y solo por el paciente asignado
     *
     * Ejemplo de uso:
     * ```
     * val result = completeAssignment("Bearer $token", "assignmentId123")
     * println("Completado el: ${result.completedAt}")
     * ```
     */
    @POST(ApiConstants.Library.COMPLETE_ASSIGNMENT)
    suspend fun completeAssignment(
        @Header("Authorization") token: String,
        @Path("assignmentId") assignmentId: String
    ): AssignmentCompletedResponseDto

    // ============================================================
    // ENDPOINTS PARA PSICÓLOGOS (Psychologist)
    // ============================================================

    /**
     * Asigna contenido a uno o más pacientes
     *
     * @param token Token de autenticación Bearer
     * @param request Datos de la asignación (patientIds, contentId, contentType, notes)
     * @return IDs de las asignaciones creadas
     *
     * Nota: Solo psicólogos pueden usar este endpoint
     * Los pacientes deben pertenecer al psicólogo autenticado
     *
     * Ejemplo de uso:
     * ```
     * val request = AssignmentRequestDto(
     *     patientIds = listOf("patient1", "patient2"),
     *     contentId = "tmdb-movie-27205",
     *     contentType = "Movie",
     *     notes = "Ver esta película y reflexionar sobre los sueños"
     * )
     * val result = assignContent("Bearer $token", request)
     * println("${result.assignmentIds.size} asignaciones creadas")
     * ```
     */
    @POST(ApiConstants.Library.ASSIGNMENTS)
    suspend fun assignContent(
        @Header("Authorization") token: String,
        @Body request: AssignmentRequestDto
    ): AssignmentCreatedResponseDto

    /**
     * Obtiene todas las asignaciones creadas por el psicólogo autenticado
     *
     * @param token Token de autenticación Bearer
     * @param patientId Filtro opcional por ID de paciente específico
     * @return Objeto con lista de asignaciones y estadísticas (total, pending, completed)
     *
     * Nota: Solo psicólogos pueden usar este endpoint
     *
     * Ejemplo de uso:
     * ```
     * // Obtener todas las asignaciones del psicólogo
     * val response = getPsychologistAssignments("Bearer $token")
     * val allAssignments = response.assignments
     *
     * // Filtrar por paciente específico
     * val patientResponse = getPsychologistAssignments(
     *     "Bearer $token",
     *     patientId = "patient123"
     * )
     * ```
     */
    @GET(ApiConstants.Library.PSYCHOLOGIST_ASSIGNMENTS)
    suspend fun getPsychologistAssignments(
        @Header("Authorization") token: String,
        @Query("patientId") patientId: String? = null
    ): AssignmentsResponseDto
}
