package com.softfocus.features.therapy.data.remote

import com.softfocus.core.networking.ApiConstants
import com.softfocus.features.therapy.data.models.request.ConnectWithPsychologistRequestDto
import com.softfocus.features.therapy.data.models.response.ConnectResponseDto
import com.softfocus.features.therapy.data.models.response.MyRelationshipResponseDto
import com.softfocus.features.therapy.data.models.response.PatientDirectoryResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface TherapyService {
    @GET(ApiConstants.Therapy.MY_RELATIONSHIP)
    suspend fun getMyRelationship(
        @Header("Authorization") token: String
    ): MyRelationshipResponseDto

    @POST(ApiConstants.Therapy.CONNECT)
    suspend fun connectWithPsychologist(
        @Header("Authorization") token: String,
        @Body request: ConnectWithPsychologistRequestDto
    ): ConnectResponseDto

    @GET(ApiConstants.Therapy.PATIENTS)
    suspend fun getMyPatients(
        @Header("Authorization") token: String
    ): List<PatientDirectoryResponseDto>

}
