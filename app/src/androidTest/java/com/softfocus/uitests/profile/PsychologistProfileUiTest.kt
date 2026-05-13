package com.softfocus.uitests.profile

import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.softfocus.fakes.profile.FakePsychologistProfileRepository
import com.softfocus.features.profile.presentation.psychologist.PsychologistProfileScreen
import com.softfocus.features.profile.presentation.psychologist.PsychologistProfileViewModel
import com.softfocus.helpers.TestTags
import com.softfocus.helpers.waitUntilTagVisible
import com.softfocus.robots.profile.ProfileRobot
import com.softfocus.ui.theme.SoftFocusMobileTheme
import io.mockk.mockk
import com.softfocus.core.data.local.UserSession
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI Tests para PsychologistProfileScreen.
 *
 * Cubre:
 * - Pantalla visible con datos del psicólogo
 * - Nombre y email se muestran correctamente
 * - Especialidades visibles
 * - Botón logout llama al callback
 * - Estado de error cuando falla la carga
 */
@RunWith(AndroidJUnit4::class)
class PsychologistProfileUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var fakePsychRepository: FakePsychologistProfileRepository
    private lateinit var mockUserSession: UserSession
    private lateinit var viewModel: PsychologistProfileViewModel
    private lateinit var robot: ProfileRobot

    private var logoutCalled = false
    private var navigateToEditCalled = false
    private var navigateToStatsCalled = false

    @Before
    fun setUp() {
        fakePsychRepository = FakePsychologistProfileRepository()
        mockUserSession = mockk(relaxed = true)

        viewModel = PsychologistProfileViewModel(
            profileRepository = fakePsychRepository,
            userSession = mockUserSession
        )

        robot = ProfileRobot(composeTestRule)
        logoutCalled = false
        navigateToEditCalled = false
        navigateToStatsCalled = false
    }

    private fun launchScreen() {
        composeTestRule.setContent {
            SoftFocusMobileTheme {
                PsychologistProfileScreen(
                    onNavigateToEditProfile = { navigateToEditCalled = true },
                    onNavigateToInvitationCode = {},
                    onNavigateToNotifications = {},
                    onNavigateToPlan = {},
                    onNavigateToStats = { navigateToStatsCalled = true },
                    onNavigateToProfessionalData = {},
                    onNavigateBack = {},
                    onLogout = { logoutCalled = true },
                    viewModel = viewModel
                )
            }
        }
    }

    @Test
    fun psychologistProfileScreen_isVisible_afterLoad() {
        launchScreen()
        composeTestRule.waitUntilTagVisible(TestTags.Profile.PSYCHOLOGIST_PROFILE_SCREEN)
        composeTestRule.onNodeWithTag(TestTags.Profile.PSYCHOLOGIST_PROFILE_SCREEN).assertExists()
    }

    @Test
    fun psychologistProfileScreen_displaysFullName() {
        launchScreen()
        composeTestRule.waitUntilTagVisible(TestTags.Profile.PSYCHOLOGIST_PROFILE_NAME_TEXT)
        composeTestRule
            .onNodeWithTag(TestTags.Profile.PSYCHOLOGIST_PROFILE_NAME_TEXT)
            .assertTextContains("Dra. Ana Torres")
    }

    @Test
    fun psychologistProfileScreen_displaysEmail() {
        launchScreen()
        composeTestRule.waitUntilTagVisible(TestTags.Profile.PSYCHOLOGIST_PROFILE_EMAIL_TEXT)
        composeTestRule
            .onNodeWithTag(TestTags.Profile.PSYCHOLOGIST_PROFILE_EMAIL_TEXT)
            .assertTextContains("psych@softfocus.com")
    }

    @Test
    fun psychologistProfileScreen_displaysSpecialties() {
        launchScreen()
        composeTestRule.waitUntilTagVisible(TestTags.Profile.PSYCHOLOGIST_PROFILE_SPECIALTIES)
        composeTestRule
            .onNodeWithTag(TestTags.Profile.PSYCHOLOGIST_PROFILE_SPECIALTIES)
            .assertExists()
    }

    @Test
    fun tapLogout_callsLogoutCallback() {
        launchScreen()
        composeTestRule.waitUntilTagVisible(TestTags.Profile.PSYCHOLOGIST_PROFILE_LOGOUT_BUTTON)

        robot.tapPsychologistLogout()
        composeTestRule.waitForIdle()

        assert(logoutCalled) { "onLogout debería haberse llamado al tocar Cerrar Sesión" }
    }

    @Test
    fun psychologistProfileScreen_showsError_whenLoadFails() {
        fakePsychRepository.getPsychologistCompleteProfileResult =
            Result.failure(Exception("Error al cargar perfil del psicólogo"))

        val failingViewModel = PsychologistProfileViewModel(
            profileRepository = fakePsychRepository,
            userSession = mockUserSession
        )

        composeTestRule.setContent {
            SoftFocusMobileTheme {
                PsychologistProfileScreen(
                    onNavigateToEditProfile = {},
                    onNavigateToInvitationCode = {},
                    onNavigateToNotifications = {},
                    onNavigateToPlan = {},
                    onNavigateToStats = {},
                    onNavigateToProfessionalData = {},
                    onNavigateBack = {},
                    onLogout = {},
                    viewModel = failingViewModel
                )
            }
        }

        composeTestRule.waitUntilTagVisible(TestTags.Profile.PSYCHOLOGIST_PROFILE_ERROR_TEXT)
        composeTestRule
            .onNodeWithTag(TestTags.Profile.PSYCHOLOGIST_PROFILE_ERROR_TEXT)
            .assertTextContains("Error al cargar perfil del psicólogo")
    }
}
