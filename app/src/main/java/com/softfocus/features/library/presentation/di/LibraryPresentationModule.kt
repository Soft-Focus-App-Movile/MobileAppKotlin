package com.softfocus.features.library.presentation.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.softfocus.features.library.data.di.LibraryDataModule
import com.softfocus.features.library.domain.repositories.LibraryRepository
import retrofit2.Retrofit

/**
 * Módulo de Dependency Injection para la capa de presentación de Library
 *
 * Provee ViewModels y sus dependencias
 */
object LibraryPresentationModule {

    /**
     * Factory para crear ViewModels con dependencias
     */
    class LibraryViewModelFactory(
        private val repository: LibraryRepository
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                // Aquí se agregarán los ViewModels específicos
                // modelClass.isAssignableFrom(LibraryBrowseViewModel::class.java) -> {
                //     LibraryBrowseViewModel(repository) as T
                // }
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }

    /**
     * Provee el factory de ViewModels para Library
     *
     * @param context Contexto de la aplicación
     * @param retrofit Instancia de Retrofit
     * @return Factory para crear ViewModels
     */
    fun provideLibraryViewModelFactory(
        context: Context,
        retrofit: Retrofit
    ): LibraryViewModelFactory {
        val repository = LibraryDataModule.provideLibraryRepository(context, retrofit)
        return LibraryViewModelFactory(repository)
    }
}
