package com.softfocus.features.therapy.presentation.di

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.softfocus.core.data.local.LocalUserDataSource
import com.softfocus.core.data.local.UserSession
import com.softfocus.core.networking.ApiConstants
import com.softfocus.core.networking.Auth401Interceptor
import com.softfocus.features.therapy.data.remote.TherapyService
import com.softfocus.features.therapy.data.repositories.TherapyRepositoryImpl
import com.softfocus.features.therapy.domain.repositories.TherapyRepository
import com.softfocus.features.therapy.domain.usecases.ConnectWithPsychologistUseCase
import com.softfocus.features.therapy.domain.usecases.GetMyRelationshipUseCase
import com.softfocus.features.therapy.presentation.connect.ConnectPsychologistViewModel
import com.softfocus.features.home.presentation.HomeViewModel
import com.softfocus.features.library.assignments.data.di.AssignmentsDataModule
import com.softfocus.features.library.assignments.domain.repositories.AssignmentsRepository
import com.softfocus.features.library.data.remote.AssignmentsService
import com.softfocus.features.library.data.repositories.LibraryRepositoryImpl
import com.softfocus.features.library.domain.repositories.LibraryRepository
import com.softfocus.features.therapy.domain.usecases.GetChatHistoryUseCase
import com.softfocus.features.therapy.domain.usecases.GetLastReceivedMessageUseCase
import com.softfocus.features.therapy.domain.usecases.GetMyPatientsUseCase
import com.softfocus.features.therapy.domain.usecases.GetPatientCheckInsUseCase
import com.softfocus.features.therapy.domain.usecases.GetPatientProfileUseCase
import com.softfocus.features.therapy.domain.usecases.SendChatMessageUseCase
import com.softfocus.features.therapy.presentation.psychologist.patiendetail.PatientDetailViewModel
import com.softfocus.features.therapy.presentation.psychologist.patientlist.PatientListViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.softfocus.features.therapy.data.remote.SignalRService

object TherapyPresentationModule {

    private var applicationContext: Context? = null
    private var signalRService: SignalRService? = null


    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    private fun getTherapyService(): TherapyService {
        return getRetrofit().create(TherapyService::class.java)
    }

    fun getSignalRService(): SignalRService {
        if (signalRService == null) {
            val context = applicationContext ?: throw IllegalStateException("TherapyPresentationModule not initialized")
            // SignalRService necesita UserSession, que a su vez necesita Context
            val userSession = UserSession(context)
            signalRService = SignalRService(userSession)
        }
        return signalRService!!
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

    fun getAssignmentsRepository(): AssignmentsRepository {
        return AssignmentsDataModule.provideAssignmentsRepository(
            applicationContext ?: throw IllegalStateException("TherapyPresentationModule not initialized"),
            getRetrofit()
        )
    }

    fun getGetMyRelationshipUseCase(): GetMyRelationshipUseCase {
        return GetMyRelationshipUseCase(getTherapyRepository())
    }

    fun getGetMyPatientsUseCase(): GetMyPatientsUseCase {
        return GetMyPatientsUseCase(getTherapyRepository())
    }

    fun getGetPatientProfileUseCase(): GetPatientProfileUseCase {
        return GetPatientProfileUseCase(getTherapyRepository())
    }

    fun getGetPatientCheckInsUseCase(): GetPatientCheckInsUseCase {
        return GetPatientCheckInsUseCase(getTherapyRepository())
    }

    fun getSendChatMessageUseCase(): SendChatMessageUseCase {
        return SendChatMessageUseCase(getTherapyRepository())
    }

    fun getGetChatHistoryUseCase(): GetChatHistoryUseCase {
        return GetChatHistoryUseCase(getTherapyRepository())
    }

    fun getGetLastReceivedMessageUseCase(): GetLastReceivedMessageUseCase {
        return GetLastReceivedMessageUseCase(getTherapyRepository())
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

    fun getPatientDetailViewModel(savedStateHandle: SavedStateHandle): PatientDetailViewModel {
        return PatientDetailViewModel(
            savedStateHandle = savedStateHandle,
            getPatientProfileUseCase = getGetPatientProfileUseCase(),
            getPatientCheckInsUseCase = getGetPatientCheckInsUseCase(),
            repository = getAssignmentsRepository()
        )
    }

}
