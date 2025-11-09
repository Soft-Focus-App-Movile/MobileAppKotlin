package com.softfocus.features.crisis.data.remote

import com.softfocus.core.networking.ApiConstants
import com.softfocus.features.crisis.data.models.request.CreateCrisisAlertRequestDto
import com.softfocus.features.crisis.data.models.response.CrisisAlertResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CrisisService {

    @POST(ApiConstants.Crisis.ALERT)
    suspend fun createCrisisAlert(@Body request: CreateCrisisAlertRequestDto): CrisisAlertResponseDto

    @GET(ApiConstants.Crisis.ALERTS_BY_PATIENT)
    suspend fun getPatientAlerts(): List<CrisisAlertResponseDto>

    @GET(ApiConstants.Crisis.ALERTS_BY_PSYCHOLOGIST)
    suspend fun getPsychologistAlerts(): List<CrisisAlertResponseDto>
}
