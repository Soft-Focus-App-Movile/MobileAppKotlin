package com.softfocus.features.therapy.data.models.request

/**
 * Body para crear una tarea personalizada (POST therapy/tasks).
 */
data class CreatePatientTaskRequestDto(
    val patientId: String,
    val title: String,
    val description: String
)
