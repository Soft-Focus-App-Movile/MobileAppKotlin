package com.softfocus.features.profile.data.di

import android.content.Context
import com.softfocus.core.data.local.UserSession
import com.softfocus.core.networking.ApiConstants
import com.softfocus.features.profile.data.remote.ProfileService
import com.softfocus.features.profile.data.repositories.ProfileRepositoryImpl
import com.softfocus.features.profile.domain.repositories.ProfileRepository
import com.softfocus.features.therapy.data.remote.TherapyService
import com.softfocus.features.therapy.data.repositories.TherapyRepositoryImpl
import com.softfocus.features.therapy.domain.repositories.TherapyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ProfileRetrofit

@Module
@InstallIn(SingletonComponent::class)
object ProfileDataModule {

    /**
     * Provides a separate Retrofit instance WITHOUT auth interceptor.
     * This is needed ONLY for TherapyService because its methods manually add Authorization header.
     * ProfileService uses the regular Retrofit (with auth interceptor) from NetworkModule.
     * This matches the pattern used in PatientHomeInjection.kt
     */
    @Provides
    @Singleton
    @ProfileRetrofit
    fun provideRetrofitWithoutAuthInterceptor(): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideProfileService(retrofit: Retrofit): ProfileService {
        return retrofit.create(ProfileService::class.java)
    }

    @Provides
    @Singleton
    @ProfileRetrofit
    fun provideTherapyServiceForProfile(@ProfileRetrofit retrofit: Retrofit): TherapyService {
        return retrofit.create(TherapyService::class.java)
    }

    @Provides
    @Singleton
    fun provideTherapyRepository(
        @ProfileRetrofit therapyService: TherapyService,
        @ApplicationContext context: Context
    ): TherapyRepository {
        return TherapyRepositoryImpl(therapyService, context)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(
        profileService: ProfileService,
        therapyRepository: TherapyRepository,
        userSession: UserSession,
        @ApplicationContext context: Context
    ): ProfileRepository {
        return ProfileRepositoryImpl(profileService, therapyRepository, userSession, context)
    }
}
