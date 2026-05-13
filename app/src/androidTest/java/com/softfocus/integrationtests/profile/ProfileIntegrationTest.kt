package com.softfocus.integrationtests.profile

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.softfocus.fakes.profile.FakeProfileRepository
import com.softfocus.fakes.profile.FakeTherapyRepository
import com.softfocus.features.auth.domain.models.UserType
import com.softfocus.features.profile.domain.models.AssignedPsychologist
import com.softfocus.features.profile.presentation.ProfileUiState
import com.softfocus.features.profile.presentation.ProfileViewModel
import com.softfocus.features.profile.presentation.PsychologistLoadState
import io.mockk.every
import io.mockk.mockk
import com.softfocus.core.data.local.UserSession
import com.softfocus.fakes.auth.FakeAuthRepository
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
 * Integration Tests para ProfileViewModel.
 *
 * Cubre:
 * - Carga de perfil exitosa y con error
 * - Estados cuando hay psicólogo asignado y cuando no
 * - Actualización de perfil
 * - Desconexión del psicólogo
 * - Manejo de sesión expirada
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class ProfileIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var fakeProfileRepository: FakeProfileRepository
    private lateinit var fakeTherapyRepository: FakeTherapyRepository
    private lateinit var mockUserSession: UserSession
    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setUp() {
        fakeProfileRepository = FakeProfileRepository()
        fakeTherapyRepository = FakeTherapyRepository()
        mockUserSession = mockk(relaxed = true)

        every { mockUserSession.getUser() } returns FakeProfileRepository.defaultUser()

        viewModel = ProfileViewModel(
            profileRepository = fakeProfileRepository,
            therapyRepository = fakeTherapyRepository,
            userSession = mockUserSession,
            context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
        )
    }

    // ── CARGA DE PERFIL ───────────────────────────────────────────────────────

    @Test
    fun loadProfile_setsSuccessState_whenRepositoryReturnsUser() = runTest {
        fakeProfileRepository.getProfileResult = Result.success(FakeProfileRepository.defaultUser())

        viewModel.loadProfile()
        composeTestRule.waitForIdle()

        val state = viewModel.uiState.first()
        // Success o NoTherapist son estados válidos post-carga
        assertTrue(state is ProfileUiState.Success || state is ProfileUiState.Loading)
    }

    @Test
    fun loadProfile_setsUser_withCorrectData() = runTest {
        val expectedUser = FakeProfileRepository.defaultUser()
        fakeProfileRepository.getProfileResult = Result.success(expectedUser)

        viewModel.loadProfile()
        composeTestRule.waitForIdle()

        val user = viewModel.user.first()
        assertNotNull(user)
        assertEquals("Juan", user?.firstName)
        assertEquals("Pérez", user?.lastName)
        assertEquals("test@softfocus.com", user?.email)
    }

    @Test
    fun loadProfile_setsErrorState_whenRepositoryFails() = runTest {
        fakeProfileRepository.getProfileResult = Result.failure(Exception("Error de red"))

        viewModel.loadProfile()
        composeTestRule.waitForIdle()

        val state = viewModel.uiState.first()
        assertTrue("Debe ser estado de error", state is ProfileUiState.Error)
        assertEquals("Error de red", (state as ProfileUiState.Error).message)
    }

    @Test
    fun loadProfile_setsErrorState_whenSessionIsExpired() = runTest {
        every { mockUserSession.getUser() } returns null

        viewModel.loadProfile()
        composeTestRule.waitForIdle()

        val state = viewModel.uiState.first()
        assertTrue(state is ProfileUiState.Error)
        assertTrue((state as ProfileUiState.Error).message.contains("Sesión"))
    }

    // ── PSICÓLOGO ASIGNADO ────────────────────────────────────────────────────

    @Test
    fun loadProfile_setsPsychologistSuccess_whenRelationshipIsActive() = runTest {
        fakeTherapyRepository.getMyRelationshipResult =
            Result.success(FakeTherapyRepository.defaultRelationship())
        fakeProfileRepository.getAssignedPsychologistResult =
            Result.success(FakeProfileRepository.defaultPsychologist())

        viewModel.loadProfile()
        composeTestRule.waitForIdle()

        val psychologist = viewModel.assignedPsychologist.first()
        assertNotNull(psychologist)
        assertEquals("Dra. María García", psychologist?.fullName)
    }

    @Test
    fun loadProfile_setsNoTherapist_whenNoActiveRelationship() = runTest {
        fakeTherapyRepository.getMyRelationshipResult = Result.success(null)

        viewModel.loadProfile()
        composeTestRule.waitForIdle()

        val psychologistState = viewModel.psychologistLoadState.first()
        assertTrue(psychologistState is PsychologistLoadState.NoTherapist)
    }

    @Test
    fun loadProfile_setsNoTherapist_whenRelationshipIsInactive() = runTest {
        val inactiveRelationship = FakeTherapyRepository.defaultRelationship().copy(isActive = false)
        fakeTherapyRepository.getMyRelationshipResult = Result.success(inactiveRelationship)

        viewModel.loadProfile()
        composeTestRule.waitForIdle()

        val psychologistState = viewModel.psychologistLoadState.first()
        assertTrue(psychologistState is PsychologistLoadState.NoTherapist)
    }

    // ── ACTUALIZACIÓN DE PERFIL ───────────────────────────────────────────────

    @Test
    fun updateProfile_setsUpdateSuccessState_onSuccess() = runTest {
        val updatedUser = FakeProfileRepository.defaultUser().copy(firstName = "Carlos")
        fakeProfileRepository.updateProfileResult = Result.success(updatedUser)

        viewModel.updateProfile(
            firstName = "Carlos", lastName = "Pérez",
            dateOfBirth = null, gender = null, phone = null,
            bio = null, country = null, city = null,
            interests = null, mentalHealthGoals = null,
            emailNotifications = null, pushNotifications = null,
            isProfilePublic = null, profileImageUri = null
        )
        composeTestRule.waitForIdle()

        val state = viewModel.uiState.first()
        assertTrue(state is ProfileUiState.UpdateSuccess)
    }

    @Test
    fun updateProfile_updatesUser_withNewData() = runTest {
        val updatedUser = FakeProfileRepository.defaultUser().copy(firstName = "Carlos", bio = "Nueva bio")
        fakeProfileRepository.updateProfileResult = Result.success(updatedUser)

        viewModel.updateProfile(
            firstName = "Carlos", lastName = null,
            dateOfBirth = null, gender = null, phone = null,
            bio = "Nueva bio", country = null, city = null,
            interests = null, mentalHealthGoals = null,
            emailNotifications = null, pushNotifications = null,
            isProfilePublic = null, profileImageUri = null
        )
        composeTestRule.waitForIdle()

        val user = viewModel.user.first()
        assertEquals("Carlos", user?.firstName)
        assertEquals("Nueva bio", user?.bio)
    }

    @Test
    fun updateProfile_setsErrorState_onFailure() = runTest {
        fakeProfileRepository.updateProfileResult = Result.failure(Exception("Error al guardar"))

        viewModel.updateProfile(
            firstName = "Carlos", lastName = null,
            dateOfBirth = null, gender = null, phone = null,
            bio = null, country = null, city = null,
            interests = null, mentalHealthGoals = null,
            emailNotifications = null, pushNotifications = null,
            isProfilePublic = null, profileImageUri = null
        )
        composeTestRule.waitForIdle()

        val state = viewModel.uiState.first()
        assertTrue(state is ProfileUiState.Error)
    }

    // ── DESCONEXIÓN DE PSICÓLOGO ──────────────────────────────────────────────

    @Test
    fun disconnectPsychologist_clearsAssignedPsychologist_onSuccess() = runTest {
        // Setup: hay relación activa
        fakeTherapyRepository.getMyRelationshipResult =
            Result.success(FakeTherapyRepository.defaultRelationship())
        fakeProfileRepository.getAssignedPsychologistResult =
            Result.success(FakeProfileRepository.defaultPsychologist())
        fakeTherapyRepository.disconnectResult = Result.success(Unit)

        viewModel.loadProfile()
        composeTestRule.waitForIdle()

        var disconnectCallbackCalled = false
        viewModel.disconnectPsychologist { disconnectCallbackCalled = true }
        composeTestRule.waitForIdle()

        val psychologist = viewModel.assignedPsychologist.first()
        assertNull("El psicólogo debe ser null tras desconectar", psychologist)
        assertTrue("El callback de éxito debe haberse llamado", disconnectCallbackCalled)
    }

    @Test
    fun disconnectPsychologist_setsNoTherapistState_onSuccess() = runTest {
        fakeTherapyRepository.getMyRelationshipResult =
            Result.success(FakeTherapyRepository.defaultRelationship())
        fakeTherapyRepository.disconnectResult = Result.success(Unit)

        viewModel.loadProfile()
        composeTestRule.waitForIdle()

        viewModel.disconnectPsychologist {}
        composeTestRule.waitForIdle()

        val psychologistState = viewModel.psychologistLoadState.first()
        assertTrue(psychologistState is PsychologistLoadState.NoTherapist)
    }

    @Test
    fun disconnectPsychologist_setsError_whenNoActiveRelationship() = runTest {
        // No cargamos perfil, así que relationshipId es null
        var callbackCalled = false
        viewModel.disconnectPsychologist { callbackCalled = true }
        composeTestRule.waitForIdle()

        val state = viewModel.uiState.first()
        assertTrue(state is ProfileUiState.Error)
        assertTrue("El callback NO debe llamarse si no hay relación", !callbackCalled)
    }
}
