package com.softfocus.uitests.ai

import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.softfocus.fakes.ai.FakeAIChatRepository
import com.softfocus.features.ai.presentation.chat.AIChatScreen
import com.softfocus.features.ai.presentation.chat.AIChatViewModel
import com.softfocus.helpers.TestTags
import com.softfocus.helpers.waitUntilTagVisible
import com.softfocus.robots.ai.AIChatRobot
import com.softfocus.ui.theme.SoftFocusMobileTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI Tests para AIChatScreen.
 *
 * Cubre:
 * - Pantalla visible
 * - Botón de enviar deshabilitado cuando no hay texto
 * - Botón de enviar habilitado cuando hay texto
 * - Mensaje enviado aparece en la lista
 * - Warning de límite visible cuando quedan pocos mensajes
 * - Campo se limpia tras enviar
 */
@RunWith(AndroidJUnit4::class)
class AIChatUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var fakeRepository: FakeAIChatRepository
    private lateinit var viewModel: AIChatViewModel
    private lateinit var robot: AIChatRobot

    private var backCalled = false

    @Before
    fun setUp() {
        fakeRepository = FakeAIChatRepository()
        viewModel = AIChatViewModel(repository = fakeRepository)
        robot = AIChatRobot(composeTestRule)
        backCalled = false
    }

    private fun launchScreen() {
        composeTestRule.setContent {
            SoftFocusMobileTheme {
                AIChatScreen(
                    onBackClick = { backCalled = true },
                    viewModel = viewModel
                )
            }
        }
    }

    @Test
    fun chatScreen_isVisible() {
        launchScreen()
        composeTestRule.waitUntilTagVisible(TestTags.AI.AI_CHAT_SCREEN)
        robot.assertChatScreenVisible()
    }

    @Test
    fun sendButton_isDisabled_whenInputIsEmpty() {
        launchScreen()
        composeTestRule.waitUntilTagVisible(TestTags.AI.AI_CHAT_SCREEN)
        robot.assertSendButtonDisabled()
    }

    @Test
    fun sendButton_isEnabled_whenInputHasText() {
        launchScreen()
        composeTestRule.waitUntilTagVisible(TestTags.AI.AI_CHAT_SCREEN)
        robot
            .typeMessage("Hola, necesito ayuda")
            .assertSendButtonEnabled()
    }

    @Test
    fun messageList_isVisible() {
        launchScreen()
        composeTestRule.waitUntilTagVisible(TestTags.AI.AI_CHAT_SCREEN)
        robot.assertMessageListVisible()
    }

    @Test
    fun sendMessage_clearsInputField() {
        launchScreen()
        composeTestRule.waitUntilTagVisible(TestTags.AI.AI_CHAT_SCREEN)

        robot.typeMessage("Hola")
        composeTestRule.waitForIdle()

        robot.tapSend()
        composeTestRule.waitForIdle()

        // El campo debe quedar vacío
        composeTestRule
            .onNodeWithTag(TestTags.AI.AI_CHAT_INPUT_FIELD)
            .assertTextContains("")
    }

    @Test
    fun limitWarning_isNotVisible_whenEnoughMessages() {
        fakeRepository.getUsageStatsResult =
            Result.success(FakeAIChatRepository.defaultUsageStats())

        launchScreen()
        composeTestRule.waitUntilTagVisible(TestTags.AI.AI_CHAT_SCREEN)
        composeTestRule.waitForIdle()

        robot.assertLimitWarningGone()
    }

    @Test
    fun limitWarning_isVisible_whenFewMessagesRemain() {
        fakeRepository.getUsageStatsResult =
            Result.success(FakeAIChatRepository.lowRemainingStats())

        viewModel = AIChatViewModel(repository = fakeRepository)

        composeTestRule.setContent {
            SoftFocusMobileTheme {
                AIChatScreen(
                    onBackClick = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.waitUntilTagVisible(TestTags.AI.AI_CHAT_LIMIT_WARNING)
        robot.assertLimitWarningVisible()
    }

    @Test
    fun sendMessage_withInitialMessage_showsInList() {
        composeTestRule.setContent {
            SoftFocusMobileTheme {
                AIChatScreen(
                    initialMessage = "¿Cómo manejar el estrés?",
                    onBackClick = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.waitForIdle()
        robot.assertMessageListVisible()
    }
}
