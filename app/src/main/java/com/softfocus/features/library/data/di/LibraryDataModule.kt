package com.softfocus.features.library.data.di

import android.content.Context
import com.softfocus.features.library.data.remote.AssignmentsService
import com.softfocus.features.library.data.remote.ContentSearchService
import com.softfocus.features.library.data.remote.FavoritesService
import com.softfocus.features.library.data.remote.RecommendationsService
import com.softfocus.features.library.data.repositories.LibraryRepositoryImpl
import com.softfocus.features.library.domain.repositories.LibraryRepository
import retrofit2.Retrofit

/**
 * Módulo de Dependency Injection para la capa de datos de Library
 *
 * Nota: Este módulo usa patrón de objeto singleton manual
 * porque el proyecto tiene una configuración mixta de DI (Hilt + manual)
 */
object LibraryDataModule {

    private var repositoryInstance: LibraryRepository? = null

    /**
     * Provee el servicio Retrofit para búsqueda de contenido
     */
    fun provideContentSearchService(retrofit: Retrofit): ContentSearchService {
        return retrofit.create(ContentSearchService::class.java)
    }

    /**
     * Provee el servicio Retrofit para favoritos
     */
    fun provideFavoritesService(retrofit: Retrofit): FavoritesService {
        return retrofit.create(FavoritesService::class.java)
    }

    /**
     * Provee el servicio Retrofit para asignaciones
     */
    fun provideAssignmentsService(retrofit: Retrofit): AssignmentsService {
        return retrofit.create(AssignmentsService::class.java)
    }

    /**
     * Provee el servicio Retrofit para recomendaciones
     */
    fun provideRecommendationsService(retrofit: Retrofit): RecommendationsService {
        return retrofit.create(RecommendationsService::class.java)
    }

    /**
     * Provee el repositorio de Library (Singleton)
     *
     * @param context Contexto de la aplicación
     * @param retrofit Instancia de Retrofit
     * @return Implementación del repositorio
     */
    fun provideLibraryRepository(
        context: Context,
        retrofit: Retrofit
    ): LibraryRepository {
        return repositoryInstance ?: synchronized(this) {
            repositoryInstance ?: LibraryRepositoryImpl(
                contentSearchService = provideContentSearchService(retrofit),
                favoritesService = provideFavoritesService(retrofit),
                assignmentsService = provideAssignmentsService(retrofit),
                recommendationsService = provideRecommendationsService(retrofit),
                context = context
            ).also { repositoryInstance = it }
        }
    }

    /**
     * Limpia la instancia del repositorio (útil para testing)
     */
    fun clearRepository() {
        repositoryInstance = null
    }
}
