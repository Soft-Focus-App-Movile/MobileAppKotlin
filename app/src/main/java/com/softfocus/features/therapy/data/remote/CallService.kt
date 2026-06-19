package com.softfocus.features.therapy.data.remote

import com.softfocus.core.networking.ApiConstants
import com.softfocus.features.therapy.data.models.request.InitiateCallRequestDto
import com.softfocus.features.therapy.data.models.response.CallAccessResponseDto
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Retrofit endpoints for the Agora-backed calling feature (Therapy bounded context).
 */
interface CallService {

    @POST(ApiConstants.Calls.INITIATE)
    suspend fun initiateCall(
        @Header("Authorization") token: String,
        @Body request: InitiateCallRequestDto
    ): CallAccessResponseDto

    @POST(ApiConstants.Calls.ANSWER)
    suspend fun answerCall(
        @Header("Authorization") token: String,
        @Path("callId") callId: String
    ): CallAccessResponseDto

    @POST(ApiConstants.Calls.REJECT)
    suspend fun rejectCall(
        @Header("Authorization") token: String,
        @Path("callId") callId: String
    )

    @POST(ApiConstants.Calls.END)
    suspend fun endCall(
        @Header("Authorization") token: String,
        @Path("callId") callId: String
    )
}
