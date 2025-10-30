package com.softfocus.features.therapy.presentation.di

import android.content.Context
import com.softfocus.core.data.local.LocalUserDataSource
import com.softfocus.core.networking.ApiConstants
import com.softfocus.features.therapy.data.remote.TherapyService
import com.softfocus.features.therapy.data.repositories.TherapyRepositoryImpl
import com.softfocus.features.therapy.domain.repositories.TherapyRepository
import com.softfocus.features.therapy.domain.usecases.ConnectWithPsychologistUseCase
import com.softfocus.features.therapy.domain.usecases.GetMyRelationshipUseCase
import com.softfocus.features.therapy.presentation.connect.ConnectPsychologistViewModel
import com.softfocus.features.home.presentation.HomeViewModel
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object TherapyPresentationModule {

    private var authToken: String? = null

    fun setAuthToken(token: String) {
        authToken = token
    }

    private fun getTherapyService(): TherapyService {
        return getRetrofit().create(TherapyService::class.java)
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

    private fun getTherapyRepository(): TherapyRepository {
        return TherapyRepositoryImpl(getTherapyService())
    }

    fun getGetMyRelationshipUseCase(): GetMyRelationshipUseCase {
        return GetMyRelationshipUseCase(getTherapyRepository())
    }

    fun getConnectWithPsychologistUseCase(): ConnectWithPsychologistUseCase {
        return ConnectWithPsychologistUseCase(getTherapyRepository())
    }

    fun getLocalUserDataSource(context: Context): LocalUserDataSource {
        return LocalUserDataSource(context)
    }

    fun getConnectPsychologistViewModel(context: Context): ConnectPsychologistViewModel {
        return ConnectPsychologistViewModel(
            getConnectWithPsychologistUseCase(),
            getLocalUserDataSource(context)
        )
    }

    fun getHomeViewModel(context: Context): HomeViewModel {
        return HomeViewModel(
            getGetMyRelationshipUseCase(),
            getLocalUserDataSource(context)
        )
    }
}
