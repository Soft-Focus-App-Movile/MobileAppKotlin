package com.softfocus.uitests.crisis

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.softfocus.fakes.crisis.FakeCrisisRepository
import com.softfocus.features.crisis.presentation.psychologist.CrisisAlertsScreen
import com.softfocus.features.crisis.presentation.psychologist.CrisisAlertsViewModel
import com.softfocus.robots.crisis.CrisisAlertsRobot
import com.softfocus.ui.theme.SoftFocusMobileTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CrisisAlertsUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var fakeRepository: FakeCrisisRepository
    private lateinit var viewModel: CrisisAlertsViewModel
    private lateinit var robot: CrisisAlertsRobot

    @Before
    fun setUp() {
        fakeRepository = FakeCrisisRepository()
        viewModel = CrisisAlertsViewModel(fakeRepository)
        robot = CrisisAlertsRobot(composeTestRule)
    }

    @Test
    fun displaysAlertList_whenDataIsLoadedSuccessfully() {
        fakeRepository.getPsychologistAlertsResult = Result.success(listOf(FakeCrisisRepository.defaultAlert()))

        composeTestRule.setContent {
            SoftFocusMobileTheme {
                CrisisAlertsScreen(
                    viewModel = viewModel,
                    onNavigateBack = {},
                    onSendMessage = {},
                    onViewPatientProfile = {}
                )
            }
        }

        composeTestRule.waitForIdle()
        robot.assertAlertsListVisible()
    }

    @Test
    fun displaysEmptyState_whenNoAlertsExist() {
        fakeRepository.getPsychologistAlertsResult = Result.success(emptyList())

        composeTestRule.setContent {
            SoftFocusMobileTheme {
                CrisisAlertsScreen(
                    viewModel = viewModel,
                    onNavigateBack = {},
                    onSendMessage = {},
                    onViewPatientProfile = {}
                )
            }
        }

        composeTestRule.waitForIdle()
        robot.assertEmptyStateVisible()
    }
}