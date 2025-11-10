package com.softfocus.features.crisis.presentation.di

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.softfocus.core.networking.ApiConstants
import com.softfocus.features.crisis.data.remote.CrisisService
import com.softfocus.features.crisis.data.repositories.CrisisRepositoryImpl
import com.softfocus.features.crisis.domain.repositories.CrisisRepository
import com.softfocus.features.crisis.presentation.CrisisViewModel
import com.softfocus.features.crisis.presentation.psychologist.CrisisAlertsViewModel
import com.softfocus.core.data.local.UserSession
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private fun getOkHttpClient(context: Context): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val authInterceptor = Interceptor { chain ->
        val userSession = UserSession(context)
        val token = userSession.getUser()?.token

        val requestBuilder = chain.request().newBuilder()
        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        chain.proceed(requestBuilder.build())
    }

    return OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()
}

private fun getRetrofitInstance(context: Context): Retrofit {
    return Retrofit.Builder()
        .baseUrl(ApiConstants.BASE_URL)
        .client(getOkHttpClient(context))
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun provideCrisisRepository(context: Context): CrisisRepository {
    val crisisService = getRetrofitInstance(context).create(CrisisService::class.java)
    return CrisisRepositoryImpl(crisisService)
}

@Composable
fun crisisViewModel(): CrisisViewModel {
    val context = LocalContext.current
    val factory = remember {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
                val crisisRepository = provideCrisisRepository(context)
                return CrisisViewModel(crisisRepository) as VM
            }
        }
    }
    return viewModel(factory = factory)
}

object CrisisInjection {
    fun getCrisisAlertsViewModel(context: Context): CrisisAlertsViewModel {
        val crisisRepository = provideCrisisRepository(context)
        return CrisisAlertsViewModel(crisisRepository)
    }
}
