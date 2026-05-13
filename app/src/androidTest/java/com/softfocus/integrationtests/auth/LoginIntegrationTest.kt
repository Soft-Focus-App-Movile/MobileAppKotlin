package com.softfocus.integrationtests.auth

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.softfocus.fakes.auth.FakeAuthRepository
import com.softfocus.fakes.auth.FakeAuthRepository.Companion.defaultUser
import com.softfocus.features.auth.data.remote.GoogleSignInManager
import com.softfocus.features.auth.domain.models.UserType
import com.softfocus.features.auth.presentation.login.LoginViewModel
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration Tests para el flujo de Login.
 *
 * QUÉ prueban estos tests:
 * - Que LoginViewModel + FakeAuthRepository funcionan juntos correctamente
 * - Que los estados (isLoading, errorMessage, user) cambian como se espera
 * - Que los flujos de datos (StateFlow) emiten los valores correctos
 *
 * NO prueban:
 * - La UI visual (eso es UI Test)
 * - La red real (usamos FakeAuthRepository)
 *
 * CUÁNDO usar Integration Test vs UI Test:
 * - Integration: cuando quieres validar lógica de estado y flujos
 * - UI Test: cuando quieres validar lo que el usuario ve en pantalla
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class LoginIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var fakeRepository: FakeAuthRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        fakeRepository = FakeAuthRepository()
        viewModel = LoginViewModel(
            repository = fakeRepository,
            googleSignInManager = mockk(relaxed = true)
        )
    }

    @Test
    fun login_setsUser_whenCredentialsAreValid() = runTest {
        fakeRepository.loginResult = Result.success(defaultUser())

        viewModel.updateEmail("test@softfocus.com")
        viewModel.updatePassword("password123")
        viewModel.login()

        composeTestRule.waitForIdle()

        val user = viewModel.user.first()
        assertNotNull("El usuario debería estar seteado tras login exitoso", user)
        assertEquals("test@softfocus.com", user?.email)
        assertEquals(UserType.GENERAL, user?.userType)
    }

    @Test
    fun login_setsErrorMessage_whenCredentialsAreInvalid() = runTest {
        fakeRepository.loginResult = Result.failure(Exception("Credenciales incorrectas"))

        viewModel.updateEmail("wrong@email.com")
        viewModel.updatePassword("wrongpass")
        viewModel.login()

        composeTestRule.waitForIdle()

        val error = viewModel.errorMessage.first()
        assertNotNull("Debería haber un mensaje de error", error)
        assertEquals("Credenciales incorrectas", error)
    }

    @Test
    fun login_clearsErrorMessage_beforeNewAttempt() = runTest {
        // Primer intento falla
        fakeRepository.loginResult = Result.failure(Exception("Error"))
        viewModel.login()
        composeTestRule.waitForIdle()

        // Segundo intento exitoso — el error anterior debe limpiarse
        fakeRepository.loginResult = Result.success(defaultUser())
        viewModel.login()
        composeTestRule.waitForIdle()

        val error = viewModel.errorMessage.first()
        assertNull("El error debería haberse limpiado en el segundo intento", error)
    }

    @Test
    fun login_isLoadingFalse_afterCompletion() = runTest {
        fakeRepository.loginResult = Result.success(defaultUser())

        viewModel.login()
        composeTestRule.waitForIdle()

        val isLoading = viewModel.isLoading.first()
        assertTrue("isLoading debería ser false después de completar", !isLoading)
    }

    @Test
    fun login_isLoadingFalse_afterError() = runTest {
        fakeRepository.loginResult = Result.failure(Exception("Error de red"))

        viewModel.login()
        composeTestRule.waitForIdle()

        val isLoading = viewModel.isLoading.first()
        assertTrue("isLoading debería ser false incluso después de un error", !isLoading)
    }

    @Test
    fun updateEmail_updatesEmailState() = runTest {
        viewModel.updateEmail("nuevo@email.com")

        val email = viewModel.email.first()
        assertEquals("nuevo@email.com", email)
    }

    @Test
    fun updatePassword_updatesPasswordState() = runTest {
        viewModel.updatePassword("nuevaPassword")

        val password = viewModel.password.first()
        assertEquals("nuevaPassword", password)
    }

    @Test
    fun login_callsRepository_exactlyOnce() = runTest {
        viewModel.login()
        composeTestRule.waitForIdle()

        assertEquals("login() debería llamarse exactamente una vez", 1, fakeRepository.loginCallCount)
    }

    @Test
    fun psychologistPendingVerification_isTrue_whenBackendReturnsVerificationError() = runTest {
        fakeRepository.loginResult = Result.failure(Exception("pending verification"))

        viewModel.login()
        composeTestRule.waitForIdle()

        val isPending = viewModel.psychologistPendingVerification.first()
        assertTrue("psychologistPendingVerification debería ser true", isPending)
    }
}
