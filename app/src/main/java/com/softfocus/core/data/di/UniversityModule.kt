package com.softfocus.core.data.di

import com.softfocus.core.data.remote.UniversityApiService
import com.softfocus.core.data.repositories.UniversityRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object UniversityModule {

    private const val UNIVERSITIES_BASE_URL = "http://universities.hipolabs.com/"

    fun getUniversityRepository(): UniversityRepository {
        return UniversityRepository(getUniversityService())
    }

    private fun getUniversityService(): UniversityApiService {
        return getRetrofit().create(UniversityApiService::class.java)
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(UNIVERSITIES_BASE_URL)
            .client(getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }
}
