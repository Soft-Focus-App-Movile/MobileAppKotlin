package com.softfocus.features.library.data.models.response

import com.google.gson.annotations.SerializedName

/**
 * DTO para respuesta simple de lista de contenido (usado en recomendaciones)
 */
data class ContentListResponseDto(
    @SerializedName("content")
    val content: List<ContentItemResponseDto>
)

/**
 * DTO para respuesta de asignación exitosa (devuelve IDs)
 */
data class AssignmentCreatedResponseDto(
    @SerializedName("assignmentIds")
    val assignmentIds: List<String>,

    @SerializedName("message")
    val message: String? = null
)

/**
 * DTO para respuesta de completar asignación
 */
data class AssignmentCompletedResponseDto(
    @SerializedName("assignmentId")
    val assignmentId: String,

    @SerializedName("completedAt")
    val completedAt: String,

    @SerializedName("message")
    val message: String? = null
)
