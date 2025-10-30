package com.softfocus.features.psychologist.presentation.di

import android.content.Context
import com.softfocus.core.networking.ApiConstants
import com.softfocus.features.home.presentation.psychologist.PsychologistHomeViewModel
import com.softfocus.features.psychologist.data.remote.PsychologistService
import com.softfocus.features.psychologist.data.repositories.PsychologistRepositoryImpl
import com.softfocus.features.psychologist.domain.repositories.PsychologistRepository
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object PsychologistPresentationModule {

    private var authToken: String? = null

    fun setAuthToken(token: String) {
        authToken = token
    }

    private fun getPsychologistService(): PsychologistService {
        return getRetrofit().create(PsychologistService::class.java)
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getOkHttpClient(): OkHttpClient {
        val authInterceptor = Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            authToken?.let {
                requestBuilder.addHeader("Authorization", "Bearer $it")
            }
            chain.proceed(requestBuilder.build())
        }

        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .build()
    }

    private fun getPsychologistRepository(): PsychologistRepository {
        return PsychologistRepositoryImpl(getPsychologistService())
    }

    fun getPsychologistHomeViewModel(context: Context): PsychologistHomeViewModel {
        return PsychologistHomeViewModel(
            getPsychologistRepository(),
            context
        )
    }
}
