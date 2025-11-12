package com.softfocus.features.home.presentation.patient.di

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.softfocus.core.data.local.UserSession
import com.softfocus.core.networking.ApiConstants
import com.softfocus.features.home.presentation.patient.PatientHomeViewModel
import com.softfocus.features.library.assignments.data.di.AssignmentsDataModule
import com.softfocus.features.library.assignments.domain.repositories.AssignmentsRepository
import com.softfocus.features.library.data.di.LibraryDataModule
import com.softfocus.features.library.domain.repositories.LibraryRepository
import com.softfocus.features.search.data.remote.PsychologistSearchService
import com.softfocus.features.search.data.repositories.SearchRepositoryImpl
import com.softfocus.features.search.domain.repositories.SearchRepository
import com.softfocus.features.therapy.data.remote.TherapyService
import com.softfocus.features.therapy.data.repositories.TherapyRepositoryImpl
import com.softfocus.features.therapy.domain.repositories.TherapyRepository
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

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

private fun getRetrofitInstance(): Retrofit {
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
    val therapyService = getRetrofitInstance().create(TherapyService::class.java)
    return TherapyRepositoryImpl(therapyService, context)
}

fun provideSearchRepository(context: Context): SearchRepository {
    val searchService = getRetrofitInstance().create(PsychologistSearchService::class.java)
    return SearchRepositoryImpl(searchService, context)
}

fun provideAssignmentsRepository(context: Context): AssignmentsRepository {
    return AssignmentsDataModule.provideAssignmentsRepository(
        context = context,
        retrofit = getRetrofitInstance()
    )
}

@Composable
fun patientHomeViewModel(): PatientHomeViewModel {
    val context = LocalContext.current
    val factory = remember {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
                val libraryRepository = provideLibraryRepository(context)
                val therapyRepository = provideTherapyRepository(context)
                val searchRepository = provideSearchRepository(context)
                val assignmentsRepository = provideAssignmentsRepository(context)
                return PatientHomeViewModel(
                    libraryRepository,
                    therapyRepository,
                    searchRepository,
                    assignmentsRepository
                ) as VM
            }
        }
    }
    return viewModel(factory = factory)
}
