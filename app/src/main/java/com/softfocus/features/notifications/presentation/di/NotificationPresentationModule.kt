package com.softfocus.features.notifications.presentation.di

import android.content.Context
import com.softfocus.core.data.local.UserSession
import com.softfocus.features.notifications.data.remote.NotificationService
import com.softfocus.features.notifications.data.repositories.NotificationPreferenceRepositoryImpl
import com.softfocus.features.notifications.data.repositories.NotificationRepositoryImpl
import com.softfocus.features.notifications.domain.repositories.NotificationPreferenceRepository
import com.softfocus.features.notifications.domain.repositories.NotificationRepository
import com.softfocus.features.notifications.domain.usecases.*
import com.softfocus.features.notifications.presentation.list.NotificationsViewModel
import com.softfocus.features.notifications.presentation.preferences.NotificationPreferencesViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.softfocus.core.networking.ApiConstants
import java.util.concurrent.TimeUnit

object NotificationPresentationModule {

    private var authToken: String = ""

    fun setAuthToken(token: String) {
        authToken = token
    }

    fun clearAuthToken() {
        authToken = ""
    }

    private fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestBuilder = originalRequest.newBuilder()

                if (authToken.isNotEmpty()) {
                    requestBuilder.header("Authorization", "Bearer $authToken")
                }

                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun provideNotificationService(): NotificationService {
        return provideRetrofit().create(NotificationService::class.java)
    }

    private fun provideNotificationRepository(context: Context): NotificationRepository {
        return NotificationRepositoryImpl(
            notificationService = provideNotificationService(),
            userSession = UserSession(context)
        )
    }

    private fun provideNotificationPreferenceRepository(): NotificationPreferenceRepository {
        return NotificationPreferenceRepositoryImpl(
            notificationService = provideNotificationService()
        )
    }

    private fun provideGetNotificationsUseCase(context: Context): GetNotificationsUseCase {
        return GetNotificationsUseCase(provideNotificationRepository(context))
    }

    private fun provideMarkAsReadUseCase(context: Context): MarkAsReadUseCase {
        return MarkAsReadUseCase(provideNotificationRepository(context))
    }

    private fun provideGetNotificationPreferencesUseCase(): GetNotificationPreferencesUseCase {
        return GetNotificationPreferencesUseCase(provideNotificationPreferenceRepository())
    }

    private fun provideUpdateNotificationPreferencesUseCase(): UpdateNotificationPreferencesUseCase {
        return UpdateNotificationPreferencesUseCase(provideNotificationPreferenceRepository())
    }

    fun getNotificationsViewModel(context: Context): NotificationsViewModel {
        return NotificationsViewModel(
            getNotificationsUseCase = provideGetNotificationsUseCase(context),
            markAsReadUseCase = provideMarkAsReadUseCase(context),
            notificationRepository = provideNotificationRepository(context),
            userSession = UserSession(context)
        )
    }

    fun getNotificationPreferencesViewModel(context: Context): NotificationPreferencesViewModel {
        return NotificationPreferencesViewModel(
            getPreferencesUseCase = provideGetNotificationPreferencesUseCase(),
            updatePreferencesUseCase = provideUpdateNotificationPreferencesUseCase(),
            preferenceRepository = provideNotificationPreferenceRepository(),
            userSession = UserSession(context)
        )
    }
}