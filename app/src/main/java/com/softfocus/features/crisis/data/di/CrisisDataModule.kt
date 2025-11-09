package com.softfocus.features.crisis.data.di

import com.softfocus.core.networking.ApiConstants
import com.softfocus.features.crisis.data.remote.CrisisService
import com.softfocus.features.crisis.data.repositories.CrisisRepositoryImpl
import com.softfocus.features.crisis.domain.repositories.CrisisRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object CrisisDataModule {

    fun getCrisisRepository(): CrisisRepository {
        return CrisisRepositoryImpl(getCrisisService())
    }

    private fun getCrisisService(): CrisisService {
        return getRetrofit().create(CrisisService::class.java)
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }
}
