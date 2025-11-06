package com.softfocus.features.therapy.data.di

import com.softfocus.features.therapy.data.remote.TherapyService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TherapyDataModule {

    @Provides
    @Singleton
    fun provideTherapyService(retrofit: Retrofit): TherapyService {
        return retrofit.create(TherapyService::class.java)
    }
}
