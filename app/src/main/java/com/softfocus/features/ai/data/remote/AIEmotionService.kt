package com.softfocus.features.ai.data.remote

import com.softfocus.core.networking.ApiConstants
import com.softfocus.features.ai.data.models.response.AIUsageStatsResponseDto
import com.softfocus.features.ai.data.models.response.EmotionAnalysisResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AIEmotionService {
    @Multipart
    @POST(ApiConstants.AI.EMOTION_ANALYZE)
    suspend fun analyzeEmotion(
        @Part image: MultipartBody.Part,
        @Part("autoCheckIn") autoCheckIn: RequestBody
    ): Response<EmotionAnalysisResponseDto>

    @GET(ApiConstants.AI.EMOTION_USAGE)
    suspend fun getEmotionUsageStats(): Response<AIUsageStatsResponseDto>
}
