package com.softfocus.uitests.therapy

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.softfocus.features.therapy.presentation.patient.PsychologistChatScreen
import com.softfocus.features.therapy.presentation.patient.PsychologistChatUiState // <-- Asegúrate de importar tu estado
import com.softfocus.features.therapy.presentation.patient.PsychologistChatViewModel
import com.softfocus.features.therapy.presentation.patient.PsychologistSummaryState
import com.softfocus.robots.therapy.PsychologistChatRobot
import com.softfocus.ui.theme.SoftFocusMobileTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PsychologistChatUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: PsychologistChatViewModel
    private lateinit var robot: PsychologistChatRobot

    @Before
    fun setUp() {
        viewModel = mockk(relaxed = true)

        // 1. Mockear el estado principal (el que ya tenías)
        val initialState = PsychologistChatUiState(
            isLoading = false,
            error = null,
            messages = emptyList()
        )
        every { viewModel.uiState } returns MutableStateFlow(initialState)

        // 2. NUEVO: Mockear el estado del sumario (PsychologistSummaryState)
        // Asegúrate de importar PsychologistSummaryState
        val initialSummaryState =
            PsychologistSummaryState() // Ajusta los parámetros por defecto si es necesario
        every { viewModel.summaryState } returns MutableStateFlow(initialSummaryState)

        robot = PsychologistChatRobot(composeTestRule)
    }

    @Test
    fun verifyChatScreenComponentsAreVisible() {
        composeTestRule.setContent {
            SoftFocusMobileTheme {
                val navController = rememberNavController()
                PsychologistChatScreen(
                    viewModel = viewModel,
                    navController = navController
                )
            }
        }

        composeTestRule.waitForIdle()
        robot.assertChatVisible()
    }

    @Test
    fun userCanTypeAndSendChatMessage() {
        composeTestRule.setContent {
            SoftFocusMobileTheme {
                val navController = rememberNavController()
                PsychologistChatScreen(
                    viewModel = viewModel,
                    navController = navController
                )
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onRoot().printToLog("TAG_DEBUG")
        robot.typeMessage("Hola doctor, me siento mejor")
            .clickSend()
    }
}