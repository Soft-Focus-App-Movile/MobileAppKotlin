package com.softfocus.features.tracking.di

import com.softfocus.features.tracking.data.TrackingRepositoryImpl
import com.softfocus.features.tracking.data.remote.TrackingApi
import com.softfocus.features.tracking.domain.repository.TrackingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TrackingModule {

    @Provides
    @Singleton
    fun provideTrackingApi(retrofit: Retrofit): TrackingApi {
        return retrofit.create(TrackingApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTrackingRepository(api: TrackingApi): TrackingRepository {
        return TrackingRepositoryImpl(api)
    }
}
