package com.softfocus.features.therapy.data.remote

import com.softfocus.features.therapy.data.models.request.ConnectWithPsychologistRequestDto
import com.softfocus.features.therapy.data.models.response.ConnectResponseDto
import com.softfocus.features.therapy.data.models.response.MyRelationshipResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TherapyService {
    @GET("../therapy/my-relationship")
    suspend fun getMyRelationship(): MyRelationshipResponseDto

    @POST("../therapy/connect")
    suspend fun connectWithPsychologist(@Body request: ConnectWithPsychologistRequestDto): ConnectResponseDto
}
