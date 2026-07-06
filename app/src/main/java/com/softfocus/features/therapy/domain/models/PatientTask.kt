package com.softfocus.features.therapy.domain.models

/**
 * Entidad de dominio que representa una tarea/propósito de texto libre que el
 * psicólogo escribe y asigna a un paciente.
 *
 * Es independiente de las asignaciones de Biblioteca (contenido): aquí solo hay
 * un título y una descripción redactados por el psicólogo.
 */
data class PatientTask(
    val id: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val assignedAt: String?
)
