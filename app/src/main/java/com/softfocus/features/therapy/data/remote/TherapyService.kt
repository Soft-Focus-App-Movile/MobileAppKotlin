package com.softfocus.features.therapy.data.remote

import com.softfocus.core.networking.ApiConstants
import com.softfocus.features.therapy.data.models.request.ConnectWithPsychologistRequestDto
import com.softfocus.features.therapy.data.models.request.SendChatMessageRequestDto
import com.softfocus.features.therapy.data.models.response.ChatHistoryResponseDto
import com.softfocus.features.therapy.data.models.response.ConnectResponseDto
import com.softfocus.features.therapy.data.models.response.MyRelationshipResponseDto
import com.softfocus.features.therapy.data.models.response.PatientDirectoryResponseDto
import com.softfocus.features.therapy.data.models.response.SendChatMessageResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

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

    @GET(ApiConstants.Chat.HISTORY)
    suspend fun getChatHistory(
        @Header("Authorization") token: String,
        @Query("relationshipId") relationshipId: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): ChatHistoryResponseDto

    @POST(ApiConstants.Chat.SEND)
    suspend fun sendChatMessage(
        @Header("Authorization") token: String,
        @Body request: SendChatMessageRequestDto
    ): SendChatMessageResponseDto

}
