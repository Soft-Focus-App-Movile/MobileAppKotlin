package com.softfocus.features.therapy.data.repositories

import android.content.Context
import com.softfocus.core.data.local.UserSession
import com.softfocus.features.therapy.data.models.request.CreatePatientTaskRequestDto
import com.softfocus.features.therapy.data.models.response.toDomain
import com.softfocus.features.therapy.data.remote.PatientTaskService
import com.softfocus.features.therapy.domain.models.PatientTask
import com.softfocus.features.therapy.domain.repositories.PatientTaskRepository

class PatientTaskRepositoryImpl(
    private val service: PatientTaskService,
    private val context: Context
) : PatientTaskRepository {

    private val userSession = UserSession(context)

    private fun getAuthToken(): String {
        val token = userSession.getUser()?.token
        if (token.isNullOrEmpty()) {
            throw IllegalStateException("Token no disponible. Usuario debe iniciar sesión nuevamente.")
        }
        return "Bearer $token"
    }

    override suspend fun getPatientTasks(patientId: String): Result<List<PatientTask>> {
        return try {
            val response = service.getPatientTasks(
                token = getAuthToken(),
                patientId = patientId
            )
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyTasks(): Result<List<PatientTask>> {
        return try {
            val response = service.getMyTasks(token = getAuthToken())
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun completeTask(taskId: String): Result<PatientTask> {
        return try {
            val response = service.completeTask(token = getAuthToken(), taskId = taskId)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createTask(
        patientId: String,
        title: String,
        description: String
    ): Result<PatientTask> {
        return try {
            val response = service.createTask(
                token = getAuthToken(),
                request = CreatePatientTaskRequestDto(
                    patientId = patientId,
                    title = title,
                    description = description
                )
            )
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
