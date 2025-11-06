package com.softfocus.features.profile.data.di

import android.content.Context
import com.softfocus.core.data.local.UserSession
import com.softfocus.features.profile.data.remote.ProfileService
import com.softfocus.features.profile.data.repositories.ProfileRepositoryImpl
import com.softfocus.features.profile.domain.repositories.ProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileDataModule {

    @Provides
    @Singleton
    fun provideProfileService(retrofit: Retrofit): ProfileService {
        return retrofit.create(ProfileService::class.java)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(
        profileService: ProfileService,
        userSession: UserSession,
        @ApplicationContext context: Context
    ): ProfileRepository {
        return ProfileRepositoryImpl(profileService, userSession, context)
    }
}
