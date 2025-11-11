package com.softfocus.features.ai.presentation.di

import android.content.Context
import com.softfocus.core.data.local.UserSession
import com.softfocus.core.networking.ApiConstants
import com.softfocus.features.ai.data.di.AIDataModule
import com.softfocus.features.ai.presentation.chat.AIChatViewModel
import com.softfocus.features.ai.presentation.emotion.EmotionDetectionViewModel
import com.softfocus.features.ai.presentation.welcome.AIWelcomeViewModel
import com.softfocus.features.tracking.data.TrackingRepositoryImpl
import com.softfocus.features.tracking.data.remote.TrackingApi
import com.softfocus.features.tracking.domain.usecase.GetTodayCheckInUseCase
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object AIPresentationModule {

    fun getAIChatViewModel(context: Context): AIChatViewModel {
        return AIChatViewModel(
            repository = AIDataModule.getAIChatRepository(context)
        )
    }

    fun getAIWelcomeViewModel(context: Context): AIWelcomeViewModel {
        return AIWelcomeViewModel(
            repository = AIDataModule.getAIChatRepository(context)
        )
    }

    fun getEmotionDetectionViewModel(context: Context): EmotionDetectionViewModel {
        val trackingApi = getRetrofit(context).create(TrackingApi::class.java)
        val trackingRepository = TrackingRepositoryImpl(trackingApi)
        val getTodayCheckInUseCase = GetTodayCheckInUseCase(trackingRepository)

        return EmotionDetectionViewModel(
            emotionRepository = AIDataModule.getAIEmotionRepository(context),
            getTodayCheckInUseCase = getTodayCheckInUseCase
        )
    }

    private fun getRetrofit(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(getOkHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getOkHttpClient(context: Context): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = Interceptor { chain ->
            val userSession = UserSession(context)
            val token = userSession.getUser()?.token

            val requestBuilder = chain.request().newBuilder()
            if (token != null) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }

            chain.proceed(requestBuilder.build())
        }

        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }
}
