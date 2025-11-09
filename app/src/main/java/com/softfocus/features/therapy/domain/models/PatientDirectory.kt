package com.softfocus.features.therapy.domain.models

// Representa el modelo de dominio limpio para un paciente en el directorio del psicólogo
data class PatientDirectory(
    val id: String,
    val psychologistId: String,
    val patientId: String,
    val patientName: String,
    val age: Int,
    val profilePhotoUrl: String,
    val status: String, // Simplificado a String, puedes usar un Enum si ya existe
    val startDate: String, // Simplificado a String
    val sessionCount: Int,
    val lastSessionDate: String?
)
