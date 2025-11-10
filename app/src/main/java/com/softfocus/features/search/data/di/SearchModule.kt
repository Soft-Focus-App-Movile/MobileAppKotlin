package com.softfocus.features.search.data.di

import android.content.Context
import com.softfocus.features.search.data.remote.PsychologistSearchService
import com.softfocus.features.search.data.repositories.SearchRepositoryImpl
import com.softfocus.features.search.domain.repositories.SearchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SearchModule {

    @Provides
    @Singleton
    fun provideSearchRepository(
        service: PsychologistSearchService,
        @ApplicationContext context: Context
    ): SearchRepository {
        return SearchRepositoryImpl(service, context)
    }
}
