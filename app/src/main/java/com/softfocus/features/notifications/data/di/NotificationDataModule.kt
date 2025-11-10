package com.softfocus.features.notifications.data.di


import com.softfocus.core.data.local.UserSession
import com.softfocus.features.notifications.data.repositories.NotificationPreferenceRepositoryImpl
import com.softfocus.features.notifications.data.repositories.NotificationRepositoryImpl
import com.softfocus.features.notifications.domain.repositories.NotificationPreferenceRepository
import com.softfocus.features.notifications.domain.repositories.NotificationRepository
import com.softfocus.features.notifications.data.remote.NotificationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationDataModule {

    @Provides
    @Singleton
    fun provideNotificationRepository(
        notificationService: NotificationService,
        userSession: UserSession
    ): NotificationRepository {
        return NotificationRepositoryImpl(notificationService, userSession)
    }

    @Provides
    @Singleton
    fun provideNotificationPreferenceRepository(
        notificationService: NotificationService
    ): NotificationPreferenceRepository {
        return NotificationPreferenceRepositoryImpl(notificationService)
    }
}