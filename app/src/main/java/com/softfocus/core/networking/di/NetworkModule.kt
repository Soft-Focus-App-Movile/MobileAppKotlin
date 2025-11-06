package com.softfocus.core.networking.di

import android.content.Context
import com.softfocus.core.data.local.UserSession
import com.softfocus.core.networking.ApiConstants
import com.softfocus.features.library.data.remote.AssignmentsService
import com.softfocus.features.library.data.remote.ContentSearchService
import com.softfocus.features.library.data.remote.FavoritesService
import com.softfocus.features.library.data.remote.RecommendationsService
import com.softfocus.features.notifications.data.remote.NotificationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = Interceptor { chain ->
            val userSession = UserSession(context)
            val token = userSession.getUser()?.token

            val requestBuilder = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json") // Añadido desde tu HEAD

            if (token != null && token.isNotEmpty()) { // Lógica mejorada
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

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideNotificationService(retrofit: Retrofit): NotificationService {
        return retrofit.create(NotificationService::class.java)
    }

    // ============================================================
    // LIBRARY SERVICES (Mantenidos de tu rama HEAD)
    // ============================================================

    @Provides
    @Singleton
    fun provideContentSearchService(retrofit: Retrofit): ContentSearchService {
        return retrofit.create(ContentSearchService::class.java)
    }

    @Provides
    @Singleton
    fun provideFavoritesService(retrofit: Retrofit): FavoritesService {
        return retrofit.create(FavoritesService::class.java)
    }

    @Provides
    @Singleton
    fun provideAssignmentsService(retrofit: Retrofit): AssignmentsService {
        return retrofit.create(AssignmentsService::class.java)
    }

    @Provides
    @Singleton
    fun provideRecommendationsService(retrofit: Retrofit): RecommendationsService {
        return retrofit.create(RecommendationsService::class.java)
    }
}