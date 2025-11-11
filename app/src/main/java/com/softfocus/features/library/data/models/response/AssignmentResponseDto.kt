package com.softfocus.features.library.data.models.response

import com.google.gson.annotations.SerializedName
import com.softfocus.features.library.domain.models.Assignment
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * DTO para respuesta de asignación del backend
 */
data class AssignmentResponseDto(
    @SerializedName("assignmentId")
    val assignmentId: String,

    @SerializedName("psychologistId")
    val psychologistId: String?, // Nullable porque el endpoint /by-psychologist no lo envía

    @SerializedName("content")
    val content: ContentItemResponseDto,

    @SerializedName("notes")
    val notes: String? = null,

    @SerializedName("isCompleted")
    val isCompleted: Boolean = false,

    @SerializedName("completedAt")
    val completedAt: String? = null, // ISO 8601 format

    @SerializedName("assignedAt")
    val assignedAt: String // ISO 8601 format
) {
    /**
     * Mapea el DTO a la entidad de dominio
     *
     * @param currentPsychologistId ID del psicólogo actual (se usa cuando el backend no lo envía)
     */
    fun toDomain(currentPsychologistId: String? = null): Assignment {
        return Assignment(
            id = assignmentId,
            psychologistId = psychologistId ?: currentPsychologistId ?: "", // Usa el del DTO, o el del contexto, o vacío
            psychologistName = null, // No viene en la respuesta del backend
            patientId = null, // No viene en la respuesta del backend
            content = content.toDomain(),
            notes = notes,
            isCompleted = isCompleted,
            completedAt = completedAt?.let { parseDateTime(it) },
            createdAt = parseDateTime(assignedAt)
        )
    }

    companion object {
        /**
         * Parsea un string de fecha/hora ISO 8601 (con timezone UTC) a LocalDateTime
         * Convierte automáticamente de UTC a la zona horaria del dispositivo
         */
        private fun parseDateTime(dateTimeString: String): LocalDateTime {
            return try {
                // Parsear como Instant (entiende UTC y la "Z")
                val instant = Instant.parse(dateTimeString)
                // Convertir a LocalDateTime en la zona horaria del sistema
                LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            } catch (e: Exception) {
                // Fallback: intenta parsear sin timezone (formato legacy)
                try {
                    LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME)
                } catch (e: Exception) {
                    // Último fallback: usa la hora actual
                    LocalDateTime.now()
                }
            }
        }
    }
}
