package com.softfocus.features.library.data.models.response

import com.google.gson.annotations.SerializedName

/**
 * DTO para respuesta que contiene lista de asignaciones del backend
 *
 * Esta clase envuelve la lista de asignaciones junto con estadísticas
 * sobre el total, pendientes y completadas.
 */
data class AssignmentsResponseDto(
    @SerializedName("assignments")
    val assignments: List<AssignmentResponseDto>,

    @SerializedName("total")
    val total: Int,

    @SerializedName("pending")
    val pending: Int,

    @SerializedName("completed")
    val completed: Int
)
