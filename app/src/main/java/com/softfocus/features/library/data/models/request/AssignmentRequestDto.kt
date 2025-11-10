package com.softfocus.features.library.data.models.request

import com.google.gson.annotations.SerializedName

/**
 * DTO para solicitud de asignación de contenido
 *
 * @property patientIds Lista de IDs de pacientes a los que se asignará
 * @property contentId ID externo del contenido a asignar
 * @property contentType Tipo de contenido
 * @property notes Notas/instrucciones opcionales del psicólogo
 */
data class AssignmentRequestDto(
    @SerializedName("patientIds")
    val patientIds: List<String>,

    @SerializedName("contentId")
    val contentId: String,

    @SerializedName("contentType")
    val contentType: String,

    @SerializedName("notes")
    val notes: String? = null
)
