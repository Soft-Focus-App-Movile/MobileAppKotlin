package com.softfocus.features.therapy.domain.repositories

import com.softfocus.features.therapy.domain.models.PatientTask

interface PatientTaskRepository {

    /** Tareas que el psicólogo asignó a un paciente concreto (vista del psicólogo). */
    suspend fun getPatientTasks(patientId: String): Result<List<PatientTask>>

    /** Tareas asignadas al paciente autenticado (vista del paciente). */
    suspend fun getMyTasks(): Result<List<PatientTask>>

    /** Crea una tarea personalizada para un paciente. */
    suspend fun createTask(
        patientId: String,
        title: String,
        description: String
    ): Result<PatientTask>

    /** Marca una tarea como completada (la ejecuta el paciente). */
    suspend fun completeTask(taskId: String): Result<PatientTask>
}
