package com.softfocus.features.library.assignments.domain.repositories

import com.softfocus.features.library.domain.models.Assignment
import com.softfocus.features.library.domain.models.ContentType

interface AssignmentsRepository {

    suspend fun getAssignedContent(
        completed: Boolean? = null
    ): Result<List<Assignment>>

    suspend fun completeAssignment(
        assignmentId: String
    ): Result<Pair<String, String>>

    suspend fun assignContentToPatients(
        patientIds: List<String>,
        contentId: String,
        contentType: ContentType,
        notes: String? = null
    ): Result<List<String>>

    suspend fun getPsychologistAssignments(
        patientId: String? = null
    ): Result<List<Assignment>>
}
