package com.softfocus.integrationtests.auth

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.softfocus.fakes.auth.FakeAuthRepository
import com.softfocus.fakes.auth.FakeUniversityRepository
import com.softfocus.features.auth.domain.models.UserType
import com.softfocus.features.auth.presentation.register.RegisterViewModel
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
 * Integration Tests para el flujo de Registro.
 *
 * Cubre:
 * - Flujo General: validaciones de email/password, registro exitoso, error
 * - Flujo Psicólogo: registro con datos profesionales, pending verification
 * - Validaciones inline del ViewModel (email, password, confirmPassword)
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class RegisterIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var fakeRepository: FakeAuthRepository
    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setUp() {
        fakeRepository = FakeAuthRepository()
        viewModel = RegisterViewModel(
            repository = fakeRepository,
            universityRepository = FakeUniversityRepository.create()
        )
    }

    // ── VALIDACIONES ─────────────────────────────────────────────────────────

    @Test
    fun emailValidation_setsError_whenMissingAtSign() = runTest {
        viewModel.updateEmail("correo-sin-arroba")
        val error = viewModel.emailError.first()
        assertNotNull("Debe haber error de email", error)
        assertTrue(error!!.contains("@"))
    }

    @Test
    fun emailValidation_noError_whenValidEmail() = runTest {
        viewModel.updateEmail("valido@softfocus.com")
        val error = viewModel.emailError.first()
        assertNull("No debe haber error con email válido", error)
    }

    @Test
    fun passwordValidation_setsError_whenTooShort() = runTest {
        viewModel.updatePassword("abc")
        val error = viewModel.passwordError.first()
        assertNotNull("Debe haber error de contraseña corta", error)
        assertTrue(error!!.contains("6"))
    }

    @Test
    fun passwordValidation_setsError_whenNoUppercase() = runTest {
        viewModel.updatePassword("password1@")
        val error = viewModel.passwordError.first()
        assertNotNull(error)
        assertTrue(error!!.contains("mayúscula"))
    }

    @Test
    fun passwordValidation_setsError_whenNoSpecialChar() = runTest {
        viewModel.updatePassword("Password1")
        val error = viewModel.passwordError.first()
        assertNotNull(error)
        assertTrue(error!!.contains("especial"))
    }

    @Test
    fun passwordValidation_noError_whenValid() = runTest {
        viewModel.updatePassword("Password1@")
        val error = viewModel.passwordError.first()
        assertNull("No debe haber error con contraseña válida", error)
    }

    @Test
    fun confirmPassword_setsError_whenDoesNotMatch() = runTest {
        viewModel.updatePassword("Password1@")
        viewModel.updateConfirmPassword("OtraPassword1@")
        val error = viewModel.confirmPasswordError.first()
        assertNotNull(error)
        assertTrue(error!!.contains("coinciden"))
    }

    @Test
    fun confirmPassword_noError_whenMatches() = runTest {
        viewModel.updatePassword("Password1@")
        viewModel.updateConfirmPassword("Password1@")
        val error = viewModel.confirmPasswordError.first()
        assertNull("No debe haber error cuando coinciden", error)
    }

    // ── FLUJO GENERAL ────────────────────────────────────────────────────────

    @Test
    fun registerGeneralUser_setsResult_onSuccess() = runTest {
        fakeRepository.registerResult = Result.success(Pair("user-123", "juan@softfocus.com"))
        viewModel.updateEmail("juan@softfocus.com")
        viewModel.updatePassword("Password1@")
        viewModel.updateConfirmPassword("Password1@")

        viewModel.registerGeneralUser("Juan", "Pérez", acceptsPrivacyPolicy = true)
        composeTestRule.waitForIdle()

        val result = viewModel.registrationResultRegular.first()
        assertNotNull("El resultado de registro debe estar seteado", result)
        assertEquals("user-123", result?.first)
    }

    @Test
    fun registerGeneralUser_setsError_whenEmailAlreadyExists() = runTest {
        fakeRepository.registerResult = Result.failure(Exception("El email ya está registrado"))
        viewModel.updateEmail("existente@softfocus.com")
        viewModel.updatePassword("Password1@")
        viewModel.updateConfirmPassword("Password1@")

        viewModel.registerGeneralUser("Juan", "Pérez", acceptsPrivacyPolicy = true)
        composeTestRule.waitForIdle()

        val error = viewModel.errorMessage.first()
        assertNotNull(error)
        assertTrue(error!!.contains("email"))
    }

    @Test
    fun registerGeneralUser_setsError_whenPrivacyPolicyNotAccepted() = runTest {
        viewModel.updateEmail("juan@softfocus.com")
        viewModel.updatePassword("Password1@")
        viewModel.updateConfirmPassword("Password1@")

        viewModel.registerGeneralUser("Juan", "Pérez", acceptsPrivacyPolicy = false)
        composeTestRule.waitForIdle()

        val error = viewModel.errorMessage.first()
        assertNotNull(error)
        assertTrue(error!!.contains("privacidad"))
    }

    @Test
    fun registerGeneralUser_setsError_whenPasswordsDoNotMatch() = runTest {
        viewModel.updateEmail("juan@softfocus.com")
        viewModel.updatePassword("Password1@")
        viewModel.updateConfirmPassword("OtraPassword1@")

        viewModel.registerGeneralUser("Juan", "Pérez", acceptsPrivacyPolicy = true)
        composeTestRule.waitForIdle()

        val error = viewModel.errorMessage.first()
        assertNotNull(error)
        assertTrue(error!!.contains("coinciden"))
    }

    @Test
    fun registerGeneralUser_isLoadingFalse_afterCompletion() = runTest {
        fakeRepository.registerResult = Result.success(Pair("user-123", "juan@softfocus.com"))
        viewModel.updateEmail("juan@softfocus.com")
        viewModel.updatePassword("Password1@")
        viewModel.updateConfirmPassword("Password1@")

        viewModel.registerGeneralUser("Juan", "Pérez", acceptsPrivacyPolicy = true)
        composeTestRule.waitForIdle()

        val isLoading = viewModel.isLoading.first()
        assertTrue("isLoading debe ser false tras completar", !isLoading)
    }

    // ── FLUJO PSICÓLOGO ──────────────────────────────────────────────────────

    @Test
    fun updateUserType_toPsychologist_changesState() = runTest {
        viewModel.updateUserType(UserType.PSYCHOLOGIST)
        val userType = viewModel.userType.first()
        assertEquals(UserType.PSYCHOLOGIST, userType)
    }

    @Test
    fun updateUserType_toGeneral_changesState() = runTest {
        viewModel.updateUserType(UserType.PSYCHOLOGIST)
        viewModel.updateUserType(UserType.GENERAL)
        val userType = viewModel.userType.first()
        assertEquals(UserType.GENERAL, userType)
    }

    @Test
    fun registerPsychologist_setsResult_onSuccess() = runTest {
        fakeRepository.registerResult = Result.success(Pair("psych-456", "psych@softfocus.com"))
        viewModel.updateEmail("psych@softfocus.com")
        viewModel.updatePassword("Password1@")
        viewModel.updateConfirmPassword("Password1@")
        viewModel.updateUserType(UserType.PSYCHOLOGIST)

        viewModel.registerPsychologist(
            firstName = "María",
            lastName = "García",
            professionalLicense = "PSY-001",
            yearsOfExperience = 5,
            collegiateRegion = "Lima",
            university = "UNMSM",
            graduationYear = 2015,
            acceptsPrivacyPolicy = true,
            licenseDocumentUri = "content://doc/license.pdf",
            diplomaDocumentUri = "content://doc/diploma.pdf",
            dniDocumentUri = "content://doc/dni.pdf"
        )
        composeTestRule.waitForIdle()

        val result = viewModel.registrationResultRegular.first()
        assertNotNull("El resultado del psicólogo debe estar seteado", result)
        assertEquals("psych-456", result?.first)
    }

    @Test
    fun registerPsychologist_setsError_whenLicenseIsBlank() = runTest {
        viewModel.updateEmail("psych@softfocus.com")
        viewModel.updatePassword("Password1@")
        viewModel.updateConfirmPassword("Password1@")

        viewModel.registerPsychologist(
            firstName = "María",
            lastName = "García",
            professionalLicense = "",
            yearsOfExperience = 5,
            collegiateRegion = "Lima",
            university = "UNMSM",
            graduationYear = 2015,
            acceptsPrivacyPolicy = true,
            licenseDocumentUri = "content://doc/license.pdf",
            diplomaDocumentUri = "content://doc/diploma.pdf",
            dniDocumentUri = "content://doc/dni.pdf"
        )
        composeTestRule.waitForIdle()

        val error = viewModel.errorMessage.first()
        assertNotNull(error)
        assertTrue(error!!.contains("licencia"))
    }

    // ── UNIVERSIDAD SEARCH ───────────────────────────────────────────────────

    @Test
    fun searchUniversities_returnsResults_whenQueryLongEnough() = runTest {
        // El FakeUniversityRepository ya devuelve defaultUniversities() por defecto
        viewModel.searchUniversities("San Marcos")

        // Damos tiempo al debounce de 300ms
        kotlinx.coroutines.delay(400)
        composeTestRule.waitForIdle()

        val suggestions = viewModel.universitySuggestions.first()
        assertTrue("Debe haber sugerencias de universidades", suggestions.isNotEmpty())
    }

    @Test
    fun searchUniversities_returnsEmpty_whenQueryTooShort() = runTest {
        viewModel.searchUniversities("S")
        composeTestRule.waitForIdle()

        val suggestions = viewModel.universitySuggestions.first()
        assertTrue("No debe haber sugerencias para query menor a 2 chars", suggestions.isEmpty())
    }
}
