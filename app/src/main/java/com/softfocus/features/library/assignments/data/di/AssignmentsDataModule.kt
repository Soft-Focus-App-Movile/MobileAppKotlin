package com.softfocus.features.library.assignments.data.di

import android.content.Context
import com.softfocus.features.library.assignments.data.repositories.AssignmentsRepositoryImpl
import com.softfocus.features.library.assignments.domain.repositories.AssignmentsRepository
import com.softfocus.features.library.data.remote.AssignmentsService
import retrofit2.Retrofit

object AssignmentsDataModule {

    private var repositoryInstance: AssignmentsRepository? = null

    fun provideAssignmentsService(retrofit: Retrofit): AssignmentsService {
        return retrofit.create(AssignmentsService::class.java)
    }

    fun provideAssignmentsRepository(
        context: Context,
        retrofit: Retrofit
    ): AssignmentsRepository {
        return repositoryInstance ?: synchronized(this) {
            repositoryInstance ?: AssignmentsRepositoryImpl(
                assignmentsService = provideAssignmentsService(retrofit),
                context = context
            ).also { repositoryInstance = it }
        }
    }

    fun clearRepository() {
        repositoryInstance = null
    }
}
