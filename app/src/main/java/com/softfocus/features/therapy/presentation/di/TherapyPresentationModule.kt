package com.softfocus.features.therapy.presentation.di

import android.content.Context
import com.softfocus.core.data.local.LocalUserDataSource
import com.softfocus.core.networking.ApiConstants
import com.softfocus.core.networking.Auth401Interceptor
import com.softfocus.features.therapy.data.remote.TherapyService
import com.softfocus.features.therapy.data.repositories.TherapyRepositoryImpl
import com.softfocus.features.therapy.domain.repositories.TherapyRepository
import com.softfocus.features.therapy.domain.usecases.ConnectWithPsychologistUseCase
import com.softfocus.features.therapy.domain.usecases.GetMyRelationshipUseCase
import com.softfocus.features.therapy.presentation.connect.ConnectPsychologistViewModel
import com.softfocus.features.home.presentation.HomeViewModel
import com.softfocus.features.therapy.domain.usecases.GetMyPatientsUseCase
import com.softfocus.features.therapy.presentation.psychologist.patientlist.PatientListViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object TherapyPresentationModule {

    private var applicationContext: Context? = null

    fun init(context: Context) {
        applicationContext = context.applicationContext
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
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val builder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)

        applicationContext?.let {
            builder.addInterceptor(Auth401Interceptor(it))
        }

        return builder.build()
    }

    private fun getTherapyRepository(): TherapyRepository {
        return TherapyRepositoryImpl(
            getTherapyService(),
            applicationContext ?: throw IllegalStateException("TherapyPresentationModule not initialized")
        )
    }

    fun getGetMyRelationshipUseCase(): GetMyRelationshipUseCase {
        return GetMyRelationshipUseCase(getTherapyRepository())
    }

    fun getGetMyPatientsUseCase(): GetMyPatientsUseCase {
        return GetMyPatientsUseCase(getTherapyRepository())
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

    fun getPatientListViewModel(): PatientListViewModel {
        return PatientListViewModel(getGetMyPatientsUseCase())
    }

}
