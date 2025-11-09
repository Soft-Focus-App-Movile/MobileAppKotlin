package com.softfocus.features.crisis.data.remote

import com.softfocus.core.networking.ApiConstants
import com.softfocus.features.crisis.data.models.request.CreateCrisisAlertRequestDto
import com.softfocus.features.crisis.data.models.request.UpdateAlertStatusRequestDto
import com.softfocus.features.crisis.data.models.response.CrisisAlertResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CrisisService {

    @POST(ApiConstants.Crisis.ALERT)
    suspend fun createCrisisAlert(@Body request: CreateCrisisAlertRequestDto): CrisisAlertResponseDto

    @GET(ApiConstants.Crisis.ALERTS_BY_PATIENT)
    suspend fun getPatientAlerts(): List<CrisisAlertResponseDto>

    @GET(ApiConstants.Crisis.ALERTS_BY_PSYCHOLOGIST)
    suspend fun getPsychologistAlerts(
        @Query("severity") severity: String? = null,
        @Query("status") status: String? = null,
        @Query("limit") limit: Int? = null
    ): List<CrisisAlertResponseDto>

    @PUT("crisis/alerts/{id}/status")
    suspend fun updateAlertStatus(
        @Path("id") alertId: String,
        @Body request: UpdateAlertStatusRequestDto
    ): CrisisAlertResponseDto
}
