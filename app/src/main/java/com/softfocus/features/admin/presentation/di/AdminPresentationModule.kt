package com.softfocus.features.admin.presentation.di

import android.content.Context
import com.softfocus.core.networking.ApiConstants
import com.softfocus.features.admin.data.remote.AdminService
import com.softfocus.features.admin.data.repositories.AdminRepositoryImpl
import com.softfocus.features.admin.domain.repositories.AdminRepository
import com.softfocus.features.admin.presentation.userlist.AdminUsersViewModel
import com.softfocus.features.admin.presentation.verifypsychologist.VerifyPsychologistViewModel
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object AdminPresentationModule {

    private var authToken: String? = null

    fun setAuthToken(token: String) {
        authToken = token
    }

    fun clearAuthToken() {
        authToken = null
    }

    private fun getAdminService(): AdminService {
        return getRetrofit().create(AdminService::class.java)
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            authToken?.let {
                requestBuilder.addHeader("Authorization", "Bearer $it")
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

    private fun getAdminRepository(): AdminRepository {
        return AdminRepositoryImpl(getAdminService())
    }

    fun getAdminUsersViewModel(context: Context): AdminUsersViewModel {
        return AdminUsersViewModel(getAdminRepository())
    }

    fun getVerifyPsychologistViewModel(context: Context): VerifyPsychologistViewModel {
        return VerifyPsychologistViewModel(getAdminRepository())
    }
}
