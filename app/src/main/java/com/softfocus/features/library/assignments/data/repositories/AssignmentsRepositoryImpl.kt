package com.softfocus.features.library.assignments.data.repositories

import android.content.Context
import com.softfocus.core.data.local.UserSession
import com.softfocus.features.library.assignments.domain.repositories.AssignmentsRepository
import com.softfocus.features.library.data.models.request.AssignmentRequestDto
import com.softfocus.features.library.data.remote.AssignmentsService
import com.softfocus.features.library.domain.models.Assignment
import com.softfocus.features.library.domain.models.ContentType

class AssignmentsRepositoryImpl(
    private val assignmentsService: AssignmentsService,
    private val context: Context
) : AssignmentsRepository {

    private val userSession = UserSession(context)

    private fun getAuthToken(): String {
        val token = userSession.getUser()?.token
        return "Bearer $token"
    }

    override suspend fun getAssignedContent(
        completed: Boolean?
    ): Result<List<Assignment>> {
        return try {
            val response = assignmentsService.getAssignedContent(
                token = getAuthToken(),
                completed = completed
            )

            val assignments = response.assignments.map { it.toDomain() }
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

    override suspend fun assignContentToPatients(
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

            val assignments = response.assignments.map { it.toDomain() }
            Result.success(assignments)
        } catch (e: Exception) {
            Result.failure(Exception("Error al obtener asignaciones del psicólogo: ${e.message}", e))
        }
    }
}
