package com.softfocus.features.therapy.data.remote

import com.softfocus.core.networking.ApiConstants
import com.softfocus.features.therapy.data.models.request.CreatePatientTaskRequestDto
import com.softfocus.features.therapy.data.models.response.PatientTaskResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PatientTaskService {

    @POST(ApiConstants.Therapy.Tasks.BASE)
    suspend fun createTask(
        @Header("Authorization") token: String,
        @Body request: CreatePatientTaskRequestDto
    ): PatientTaskResponseDto

    @GET(ApiConstants.Therapy.Tasks.BASE)
    suspend fun getPatientTasks(
        @Header("Authorization") token: String,
        @Query("patientId") patientId: String
    ): List<PatientTaskResponseDto>

    @GET(ApiConstants.Therapy.Tasks.ASSIGNED)
    suspend fun getMyTasks(
        @Header("Authorization") token: String
    ): List<PatientTaskResponseDto>

    @POST(ApiConstants.Therapy.Tasks.COMPLETE)
    suspend fun completeTask(
        @Header("Authorization") token: String,
        @Path("taskId") taskId: String
    ): PatientTaskResponseDto
}
