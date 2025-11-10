package com.softfocus.features.library.assignments.presentation.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.softfocus.features.library.assignments.data.di.AssignmentsDataModule
import com.softfocus.features.library.assignments.domain.repositories.AssignmentsRepository
import com.softfocus.features.library.assignments.presentation.AssignmentsViewModel
import retrofit2.Retrofit

object AssignmentsPresentationModule {

    class AssignmentsViewModelFactory(
        private val repository: AssignmentsRepository
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(AssignmentsViewModel::class.java) -> {
                    AssignmentsViewModel(repository) as T
                }
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }

    fun provideAssignmentsViewModelFactory(
        context: Context,
        retrofit: Retrofit
    ): AssignmentsViewModelFactory {
        val repository = AssignmentsDataModule.provideAssignmentsRepository(context, retrofit)
        return AssignmentsViewModelFactory(repository)
    }
}
