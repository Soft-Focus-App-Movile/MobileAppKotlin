package com.softfocus.features.auth.data.di

import android.content.Context
import com.softfocus.core.networking.ApiConstants
import com.softfocus.features.auth.data.remote.AuthService
import com.softfocus.features.auth.data.remote.GoogleSignInManager
import com.softfocus.features.auth.data.repositories.AuthRepositoryImpl
import com.softfocus.features.auth.domain.repositories.AuthRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object DataModule {

    fun getAuthRepository(context: Context): AuthRepository {
        return AuthRepositoryImpl(
            getAuthService(),
            context
        )
    }

    fun getGoogleSignInManager(context: Context): GoogleSignInManager {
        return GoogleSignInManager(context)
    }

    private fun getAuthService(): AuthService {
        return getRetrofit().create(AuthService::class.java)
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
