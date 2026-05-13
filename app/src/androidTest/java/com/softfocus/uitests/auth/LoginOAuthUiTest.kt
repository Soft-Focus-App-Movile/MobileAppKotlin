package com.softfocus.uitests.auth

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.softfocus.fakes.auth.FakeAuthRepository
import com.softfocus.features.auth.data.remote.GoogleSignInManager
import com.softfocus.features.auth.data.remote.GoogleSignInResult
import com.softfocus.features.auth.domain.repositories.OAuthVerificationData
import com.softfocus.features.auth.presentation.login.LoginScreen
import com.softfocus.features.auth.presentation.login.LoginViewModel
import com.softfocus.helpers.TestTags
import com.softfocus.helpers.waitUntilTagVisible
import com.softfocus.robots.auth.LoginRobot
import com.softfocus.ui.theme.SoftFocusMobileTheme
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI Tests para el flujo OAuth (Google Sign-In) en LoginScreen.
 *
 * Clase separada de LoginUiTest porque cada test necesita su propio
 * setContent con un ViewModel distinto — no se puede llamar setContent
 * dos veces en la misma regla de Compose.
 */
@RunWith(AndroidJUnit4::class)
class LoginOAuthUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun buildFakeJwt(userId: String): String {
        val header = android.util.Base64.encodeToString(
            """{"alg":"HS256","typ":"JWT"}""".toByteArray(),
            android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP
        )
        val payload = android.util.Base64.encodeToString(
            """{"sub":"$userId","email":"existente@gmail.com"}""".toByteArray(),
            android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP
        )
        return "$header.$payload.fake-signature"
    }

    @Test
    fun googleSignIn_navigatesToRegisterWithOAuth_whenUserIsNew() {
        val fakeRepository = FakeAuthRepository()
        var navigateToRegisterWithOAuthCalled = false
        var oauthEmail: String? = null
        var oauthFullName: String? = null

        val mockGoogleManager = mockk<GoogleSignInManager>(relaxed = true)
        coEvery { mockGoogleManager.signIn(any()) } returns Result.success(
            GoogleSignInResult(
                idToken = "google-id-token-nuevo",
                email = "nuevo@gmail.com",
                displayName = "Usuario Nuevo",
                profilePictureUri = null
            )
        )

        fakeRepository.verifyOAuthResult = Result.success(
            OAuthVerificationData(
                email = "nuevo@gmail.com",
                fullName = "Usuario Nuevo",
                provider = "Google",
                tempToken = "temp-oauth-token",
                needsRegistration = true,
                existingUserType = null
            )
        )

        val viewModel = LoginViewModel(
            repository = fakeRepository,
            googleSignInManager = mockGoogleManager
        )
        val robot = LoginRobot(composeTestRule)

        composeTestRule.setContent {
            SoftFocusMobileTheme {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {},
                    onAdminLoginSuccess = {},
                    onNavigateToRegister = {},
                    onNavigateToRegisterWithOAuth = { email, fullName, _ ->
                        navigateToRegisterWithOAuthCalled = true
                        oauthEmail = email
                        oauthFullName = fullName
                    },
                    onNavigateToPendingVerification = {},
                    onNavigateToForgotPassword = {}
                )
            }
        }

        robot.tapGoogleButton()
        composeTestRule.waitForIdle()

        assert(navigateToRegisterWithOAuthCalled) {
            "onNavigateToRegisterWithOAuth debería haberse llamado para usuario nuevo"
        }
        assert(oauthEmail == "nuevo@gmail.com") {
            "El email OAuth debería ser nuevo@gmail.com, fue: $oauthEmail"
        }
        assert(oauthFullName == "Usuario Nuevo") {
            "El nombre OAuth debería ser Usuario Nuevo, fue: $oauthFullName"
        }
    }

    @Test
    fun googleSignIn_callsOnLoginSuccess_whenUserAlreadyExists() {
        val fakeRepository = FakeAuthRepository()
        var loginSuccessCalled = false
        val fakeJwt = buildFakeJwt(userId = "user-oauth-123")

        val mockGoogleManager = mockk<GoogleSignInManager>(relaxed = true)
        coEvery { mockGoogleManager.signIn(any()) } returns Result.success(
            GoogleSignInResult(
                idToken = "google-id-token-existente",
                email = "existente@gmail.com",
                displayName = "Usuario Existente",
                profilePictureUri = null
            )
        )

        fakeRepository.verifyOAuthResult = Result.success(
            OAuthVerificationData(
                email = "existente@gmail.com",
                fullName = "Usuario Existente",
                provider = "Google",
                tempToken = fakeJwt,
                needsRegistration = false,
                existingUserType = "General"
            )
        )

        val viewModel = LoginViewModel(
            repository = fakeRepository,
            googleSignInManager = mockGoogleManager
        )
        val robot = LoginRobot(composeTestRule)

        composeTestRule.setContent {
            SoftFocusMobileTheme {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = { loginSuccessCalled = true },
                    onAdminLoginSuccess = {},
                    onNavigateToRegister = {},
                    onNavigateToRegisterWithOAuth = { _, _, _ -> },
                    onNavigateToPendingVerification = {},
                    onNavigateToForgotPassword = {}
                )
            }
        }

        robot.tapGoogleButton()
        composeTestRule.waitForIdle()

        assert(loginSuccessCalled) {
            "onLoginSuccess debería haberse llamado cuando el usuario ya existe en el sistema"
        }
    }

    @Test
    fun googleSignIn_showsError_whenVerifyOAuthFails() {
        val fakeRepository = FakeAuthRepository()

        val mockGoogleManager = mockk<GoogleSignInManager>(relaxed = true)
        coEvery { mockGoogleManager.signIn(any()) } returns Result.success(
            GoogleSignInResult(
                idToken = "google-id-token-error",
                email = "error@gmail.com",
                displayName = "Error User",
                profilePictureUri = null
            )
        )

        fakeRepository.verifyOAuthResult = Result.failure(
            Exception("Error al verificar cuenta de Google")
        )

        val viewModel = LoginViewModel(
            repository = fakeRepository,
            googleSignInManager = mockGoogleManager
        )
        val robot = LoginRobot(composeTestRule)

        composeTestRule.setContent {
            SoftFocusMobileTheme {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {},
                    onAdminLoginSuccess = {},
                    onNavigateToRegister = {},
                    onNavigateToRegisterWithOAuth = { _, _, _ -> },
                    onNavigateToPendingVerification = {},
                    onNavigateToForgotPassword = {}
                )
            }
        }

        robot.tapGoogleButton()

        composeTestRule.waitUntilTagVisible(TestTags.Auth.LOGIN_ERROR_MESSAGE)
        robot.assertErrorVisible()
    }
}
