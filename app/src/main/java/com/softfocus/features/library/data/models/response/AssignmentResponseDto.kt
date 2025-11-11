package com.softfocus.features.library.data.models.response

import com.google.gson.annotations.SerializedName
import com.softfocus.features.library.domain.models.Assignment
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * DTO para respuesta de asignación del backend
 */
data class AssignmentResponseDto(
    @SerializedName("assignmentId")
    val assignmentId: String,

    @SerializedName("psychologistId")
    val psychologistId: String,

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
     */
    fun toDomain(): Assignment {
        return Assignment(
            id = assignmentId,
            psychologistId = psychologistId,
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
         * Parsea un string de fecha/hora a LocalDateTime
         */
        private fun parseDateTime(dateTimeString: String): LocalDateTime {
            return try {
                LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME)
            } catch (e: Exception) {
                // Fallback: intenta otros formatos comunes
                try {
                    LocalDateTime.parse(dateTimeString)
                } catch (e: Exception) {
                    LocalDateTime.now() // Último fallback
                }
            }
        }
    }
}
