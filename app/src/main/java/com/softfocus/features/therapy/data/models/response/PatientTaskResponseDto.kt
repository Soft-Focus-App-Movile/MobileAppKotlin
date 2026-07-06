package com.softfocus.features.therapy.data.models.response

import com.softfocus.features.therapy.domain.models.PatientTask

/**
 * DTO que refleja la respuesta del backend para una tarea personalizada.
 */
data class PatientTaskResponseDto(
    val id: String,
    val psychologistId: String? = null,
    val patientId: String? = null,
    val title: String = "",
    val description: String? = null,
    val isCompleted: Boolean = false,
    val completedAt: String? = null,
    val assignedAt: String? = null,
    val createdAt: String? = null
)

fun PatientTaskResponseDto.toDomain(): PatientTask = PatientTask(
    id = id,
    title = title,
    description = description ?: "",
    isCompleted = isCompleted,
    assignedAt = assignedAt
)
