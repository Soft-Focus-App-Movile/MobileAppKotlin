package com.softfocus.features.library.presentation.di

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.softfocus.core.networking.ApiConstants
import com.softfocus.features.library.data.di.LibraryDataModule
import com.softfocus.features.library.domain.repositories.LibraryRepository
import com.softfocus.features.therapy.data.remote.TherapyService
import com.softfocus.features.therapy.data.repositories.TherapyRepositoryImpl
import com.softfocus.features.therapy.domain.repositories.TherapyRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Helper para inyección de dependencias de Library en Composables
 *
 * Proporciona funciones de extensión para obtener fácilmente
 * repositorios y ViewModels en las pantallas Composable
 */

/**
 * Crea el cliente OkHttp con logging
 */
private fun getOkHttpClient(): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    return OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .build()
}

fun getRetrofitInstance(): Retrofit {
    return Retrofit.Builder()
        .baseUrl(ApiConstants.BASE_URL)
        .client(getOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun provideLibraryRepository(context: Context): LibraryRepository {
    return LibraryDataModule.provideLibraryRepository(
        context = context,
        retrofit = getRetrofitInstance()
    )
}

fun provideTherapyRepository(context: Context): TherapyRepository {
    val retrofit = getRetrofitInstance()
    val therapyService = retrofit.create(TherapyService::class.java)
    return TherapyRepositoryImpl(therapyService, context)
}

@Composable
inline fun <reified T : ViewModel> libraryViewModel(
    crossinline creator: (LibraryRepository) -> T
): T {
    val context = LocalContext.current
    val factory = remember {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
                val repository = provideLibraryRepository(context)
                return creator(repository) as VM
            }
        }
    }
    return viewModel(factory = factory)
}

@Composable
inline fun <reified T : ViewModel> libraryViewModelWithTherapy(
    crossinline creator: (LibraryRepository, TherapyRepository) -> T
): T {
    val context = LocalContext.current
    val factory = remember {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
                val libraryRepo = provideLibraryRepository(context)
                val therapyRepo = provideTherapyRepository(context)
                return creator(libraryRepo, therapyRepo) as VM
            }
        }
    }
    return viewModel(factory = factory)
}

/**
 * Composable para obtener el repositorio directamente (sin ViewModel)
 *
 * Ejemplo de uso:
 * ```
 * @Composable
 * fun MyScreen() {
 *     val repository = rememberLibraryRepository()
 *     // Usar repository...
 * }
 * ```
 */
@Composable
fun rememberLibraryRepository(): LibraryRepository {
    val context = LocalContext.current
    return remember {
        provideLibraryRepository(context)
    }
}
