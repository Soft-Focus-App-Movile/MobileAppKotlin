package com.softfocus.features.auth.presentation.di

import android.content.Context
import com.softfocus.core.data.di.UniversityModule
import com.softfocus.features.auth.data.di.DataModule.getAuthRepository
import com.softfocus.features.auth.data.di.DataModule.getGoogleSignInManager
import com.softfocus.features.auth.domain.usecases.ForgotPasswordUseCase
import com.softfocus.features.auth.domain.usecases.ResetPasswordUseCase
import com.softfocus.features.auth.presentation.forgotpassword.ForgotPasswordViewModel
import com.softfocus.features.auth.presentation.login.LoginViewModel
import com.softfocus.features.auth.presentation.register.RegisterViewModel

object PresentationModule {

    fun getLoginViewModel(context: Context): LoginViewModel {
        return LoginViewModel(
            repository = getAuthRepository(context),
            googleSignInManager = getGoogleSignInManager(context)
        )
    }

    fun getRegisterViewModel(context: Context): RegisterViewModel {
        return RegisterViewModel(
            repository = getAuthRepository(context),
            universityRepository = UniversityModule.getUniversityRepository()
        )
    }

    fun getForgotPasswordViewModel(context: Context): ForgotPasswordViewModel {
        val repository = getAuthRepository(context)
        return ForgotPasswordViewModel(
            forgotPasswordUseCase = ForgotPasswordUseCase(repository),
            resetPasswordUseCase = ResetPasswordUseCase(repository)
        )
    }
}
