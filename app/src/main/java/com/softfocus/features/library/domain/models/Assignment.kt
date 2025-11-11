package com.softfocus.features.library.domain.models

import java.time.LocalDateTime

/**
 * Entidad de dominio que representa contenido asignado por un psicólogo a un paciente
 *
 * @property id ID único de la asignación en MongoDB
 * @property psychologistId ID del psicólogo que asignó el contenido
 * @property psychologistName Nombre completo del psicólogo (opcional, no siempre viene del backend)
 * @property patientId ID del paciente al que se asignó (opcional, no siempre viene del backend)
 * @property content Contenido completo embebido
 * @property notes Notas/instrucciones del psicólogo para el paciente
 * @property isCompleted Indica si el paciente completó la asignación
 * @property completedAt Fecha y hora de completitud (si aplica)
 * @property createdAt Fecha y hora de creación de la asignación
 */
data class Assignment(
    val id: String,
    val psychologistId: String,
    val psychologistName: String? = null,
    val patientId: String? = null,
    val content: ContentItem,
    val notes: String? = null,
    val isCompleted: Boolean = false,
    val completedAt: LocalDateTime? = null,
    val createdAt: LocalDateTime
) {
    /**
     * Verifica si esta asignación pertenece al paciente especificado
     */
    fun belongsToPatient(patientId: String): Boolean = this.patientId == patientId

    /**
     * Verifica si esta asignación fue creada por el psicólogo especificado
     */
    fun createdByPsychologist(psychologistId: String): Boolean = this.psychologistId == psychologistId

    /**
     * Verifica si la asignación está pendiente de completar
     */
    fun isPending(): Boolean = !isCompleted

    /**
     * Obtiene el tiempo transcurrido desde la asignación en días
     */
    fun getDaysSinceAssignment(): Long {
        return java.time.Duration.between(createdAt, LocalDateTime.now()).toDays()
    }

    /**
     * Obtiene un resumen del estado de la asignación
     */
    fun getStatusDescription(): String = when {
        isCompleted -> "Completado el ${completedAt?.toLocalDate()}"
        else -> "Pendiente (hace ${getDaysSinceAssignment()} días)"
    }
}
