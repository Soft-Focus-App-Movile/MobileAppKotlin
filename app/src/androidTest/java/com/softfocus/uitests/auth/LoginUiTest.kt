package com.softfocus.uitests.auth

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI Tests para LoginScreen.
 *
 * QUÉ prueban estos tests:
 * - Lo que el usuario ve y puede hacer en la pantalla
 * - Estados visuales: botón deshabilitado, loading, error
 * - Navegación disparada desde la pantalla
 *
 * NO prueban:
 * - Lógica de negocio (eso es Integration Test)
 * - Llamadas a la red (usamos FakeAuthRepository)
 *
 * CUÁNDO ejecutarlos:
 * - Se ven corriendo visualmente en el emulador
 * - Cada test lanza la pantalla, interactúa y valida
 */
@RunWith(AndroidJUnit4::class)
class LoginUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var fakeRepository: FakeAuthRepository
    private lateinit var viewModel: LoginViewModel
    private lateinit var robot: LoginRobot

    // Callbacks de navegación: registramos si fueron llamados
    private var loginSuccessCalled = false
    private var adminLoginSuccessCalled = false
    private var navigateToRegisterCalled = false
    private var navigateToForgotPasswordCalled = false
    private var navigateToRegisterWithOAuthCalled = false
    private var oauthEmail: String? = null
    private var oauthFullName: String? = null

    @Before
    fun setUp() {
        fakeRepository = FakeAuthRepository()
        viewModel = LoginViewModel(
            repository = fakeRepository,
            googleSignInManager = mockk(relaxed = true)
        )
        robot = LoginRobot(composeTestRule)

        loginSuccessCalled = false
        adminLoginSuccessCalled = false
        navigateToRegisterCalled = false
        navigateToForgotPasswordCalled = false
        navigateToRegisterWithOAuthCalled = false
        oauthEmail = null
        oauthFullName = null

        composeTestRule.setContent {
            SoftFocusMobileTheme {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = { loginSuccessCalled = true },
                    onAdminLoginSuccess = { adminLoginSuccessCalled = true },
                    onNavigateToRegister = { navigateToRegisterCalled = true },
                    onNavigateToRegisterWithOAuth = { email, fullName, _ ->
                        navigateToRegisterWithOAuthCalled = true
                        oauthEmail = email
                        oauthFullName = fullName
                    },
                    onNavigateToPendingVerification = {},
                    onNavigateToForgotPassword = { navigateToForgotPasswordCalled = true }
                )
            }
        }
    }

    @Test
    fun loginButton_isDisabled_whenFieldsAreEmpty() {
        // El botón debe estar deshabilitado si no hay email ni contraseña
        robot.assertLoginButtonDisabled()
    }

    @Test
    fun loginButton_isEnabled_whenBothFieldsAreFilled() {
        robot
            .enterEmail("usuario@softfocus.com")
            .enterPassword("password123")
            .assertLoginButtonEnabled()
    }

    @Test
    fun loginButton_isDisabled_whenOnlyEmailFilled() {
        robot
            .enterEmail("usuario@softfocus.com")
            .assertLoginButtonDisabled()
    }

    @Test
    fun loginButton_isDisabled_whenOnlyPasswordFilled() {
        robot
            .enterPassword("password123")
            .assertLoginButtonDisabled()
    }

    @Test
    fun login_showsError_whenCredentialsAreInvalid() {
        // Configuramos el fake para devolver error
        fakeRepository.loginResult = Result.failure(Exception("Credenciales incorrectas"))

        robot
            .fillAndSubmitLogin("wrong@email.com", "wrongpassword")

        // Esperamos a que desaparezca el loading y aparezca el error
        composeTestRule.waitUntilTagVisible(TestTags.Auth.LOGIN_ERROR_MESSAGE)
        robot.assertErrorVisible()
    }

    @Test
    fun login_callsOnLoginSuccess_whenCredentialsAreValid() {
        // El fake ya devuelve éxito por defecto
        robot.fillAndSubmitLogin("usuario@softfocus.com", "password123")

        // Esperamos a que el ViewModel procese la respuesta
        composeTestRule.waitForIdle()

        assert(loginSuccessCalled) { "onLoginSuccess debería haberse llamado" }
    }

    @Test
    fun tapRegisterLink_callsNavigationCallback() {
        robot.tapRegisterLink()
        composeTestRule.waitForIdle()

        assert(navigateToRegisterCalled) { "onNavigateToRegister debería haberse llamado" }
    }

    @Test
    fun tapForgotPassword_callsNavigationCallback() {
        robot.tapForgotPassword()
        composeTestRule.waitForIdle()

        assert(navigateToForgotPasswordCalled) { "onNavigateToForgotPassword debería haberse llamado" }
    }

    // --- Tests OAuth Google ---

    @Test
    fun googleButton_isVisible_onLoginScreen() {
        // El botón de Google siempre está visible en la pantalla de login
        robot.assertLoginScreenVisible()
        composeTestRule.waitForIdle()
        // El botón existe — verificamos que el tag esté presente
        composeTestRule.onNodeWithTag(TestTags.Auth.LOGIN_GOOGLE_BUTTON).assertExists()
    }

    @Test
    fun googleSignIn_navigatesToRegisterWithOAuth_whenUserIsNew() {
        // Simulamos que Google devuelve token y el backend dice que el usuario NO existe
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
                needsRegistration = true,   // ← usuario nuevo, debe registrarse
                existingUserType = null
            )
        )

        val vmOAuth = LoginViewModel(
            repository = fakeRepository,
            googleSignInManager = mockGoogleManager
        )

        composeTestRule.setContent {
            SoftFocusMobileTheme {
                LoginScreen(
                    viewModel = vmOAuth,
                    onLoginSuccess = { loginSuccessCalled = true },
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

        // Tap el botón Google — dispara signInWithGoogle en el ViewModel
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
        // Usuario existente: verifyOAuth devuelve needsRegistration=false con un JWT real-like token
        // Usamos un JWT válido con sub claim para que extractUserIdFromJwt no falle
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
                needsRegistration = false,   // ← usuario ya registrado
                existingUserType = "General"
            )
        )

        val vmOAuth = LoginViewModel(
            repository = fakeRepository,
            googleSignInManager = mockGoogleManager
        )

        composeTestRule.setContent {
            SoftFocusMobileTheme {
                LoginScreen(
                    viewModel = vmOAuth,
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

        val vmOAuth = LoginViewModel(
            repository = fakeRepository,
            googleSignInManager = mockGoogleManager
        )

        composeTestRule.setContent {
            SoftFocusMobileTheme {
                LoginScreen(
                    viewModel = vmOAuth,
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

    /**
     * Construye un JWT mínimo válido con el payload { "sub": userId }
     * para que extractUserIdFromJwt no lance excepción en los tests.
     */
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
}
