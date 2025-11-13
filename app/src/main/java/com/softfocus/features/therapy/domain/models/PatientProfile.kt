package com.softfocus.features.therapy.domain.models

/**
 * Modelo de dominio limpio para el perfil de un paciente.
 * Contiene solo los datos que la UI necesita.
 */
data class PatientProfile(
    val id: String,
    val fullName: String,
    val profilePhotoUrl: String,
    val dateOfBirth: String?
)