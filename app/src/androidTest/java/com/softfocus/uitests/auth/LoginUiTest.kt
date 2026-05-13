package com.softfocus.uitests.auth

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.softfocus.fakes.auth.FakeAuthRepository
import com.softfocus.features.auth.data.remote.GoogleSignInManager
import com.softfocus.features.auth.presentation.login.LoginScreen
import com.softfocus.features.auth.presentation.login.LoginViewModel
import com.softfocus.helpers.TestTags
import com.softfocus.helpers.waitUntilTagVisible
import com.softfocus.robots.auth.LoginRobot
import com.softfocus.ui.theme.SoftFocusMobileTheme
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

        composeTestRule.setContent {
            SoftFocusMobileTheme {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = { loginSuccessCalled = true },
                    onAdminLoginSuccess = { adminLoginSuccessCalled = true },
                    onNavigateToRegister = { navigateToRegisterCalled = true },
                    onNavigateToRegisterWithOAuth = { _, _, _ -> },
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

    @Test
    fun googleButton_isVisible_onLoginScreen() {
        robot.assertLoginScreenVisible()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(TestTags.Auth.LOGIN_GOOGLE_BUTTON).assertIsDisplayed()
    }
}
