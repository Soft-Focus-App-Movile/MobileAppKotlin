package com.softfocus.features.ai.data.remote

import com.softfocus.core.networking.ApiConstants
import com.softfocus.features.ai.data.models.request.ChatMessageRequestDto
import com.softfocus.features.ai.data.models.response.AIUsageStatsResponseDto
import com.softfocus.features.ai.data.models.response.ChatMessageResponseDto
import com.softfocus.features.ai.data.models.response.ChatHistoryResponseDto
import com.softfocus.features.ai.data.models.response.SessionMessagesResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AIChatService {
    @POST(ApiConstants.AI.CHAT_MESSAGE)
    suspend fun sendMessage(
        @Body request: ChatMessageRequestDto
    ): Response<ChatMessageResponseDto>

    @GET(ApiConstants.AI.CHAT_USAGE)
    suspend fun getUsageStats(): Response<AIUsageStatsResponseDto>

    @GET(ApiConstants.AI.CHAT_SESSIONS)
    suspend fun getChatSessions(
        @Query("pageSize") pageSize: Int = 20
    ): Response<ChatHistoryResponseDto>

    @GET(ApiConstants.AI.CHAT_SESSION_MESSAGES)
    suspend fun getSessionMessages(
        @Path("sessionId") sessionId: String,
        @Query("limit") limit: Int = 50
    ): Response<SessionMessagesResponseDto>
}
