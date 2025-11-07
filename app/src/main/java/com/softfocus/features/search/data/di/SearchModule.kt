package com.softfocus.features.search.data.di

import com.softfocus.features.search.data.remote.PsychologistSearchService
import com.softfocus.features.search.data.repositories.SearchRepositoryImpl
import com.softfocus.features.search.domain.repositories.SearchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SearchModule {

    @Provides
    @Singleton
    fun provideSearchRepository(service: PsychologistSearchService): SearchRepository {
        return SearchRepositoryImpl(service)
    }
}
