package com.softfocus.integrationtests.ai

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.softfocus.fakes.ai.FakeAIChatRepository
import com.softfocus.features.ai.domain.models.MessageRole
import com.softfocus.features.ai.presentation.chat.AIChatViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration Tests para AIChatViewModel.
 *
 * Cubre:
 * - Envío de mensajes y recepción de respuesta
 * - Manejo de estados: loading, error, mensajes
 * - Estadísticas de uso y warning de límite
 * - Nueva conversación
 * - Carga de sesión previa
 * - Mensaje vacío no se envía
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class AIChatIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var fakeRepository: FakeAIChatRepository
    private lateinit var viewModel: AIChatViewModel

    @Before
    fun setUp() {
        fakeRepository = FakeAIChatRepository()
        viewModel = AIChatViewModel(repository = fakeRepository)
    }

    // ── ENVÍO DE MENSAJES ─────────────────────────────────────────────────────

    @Test
    fun sendMessage_addsUserMessage_toMessageList() = runTest {
        viewModel.onMessageChange("Hola, necesito ayuda")
        viewModel.sendMessage()
        composeTestRule.waitForIdle()

        val messages = viewModel.state.first().messages
        val userMessage = messages.firstOrNull { it.role == MessageRole.USER }
        assertNotNull("Debe haber un mensaje del usuario", userMessage)
        assertEquals("Hola, necesito ayuda", userMessage?.content)
    }

    @Test
    fun sendMessage_addsAssistantResponse_toMessageList() = runTest {
        viewModel.onMessageChange("Hola")
        viewModel.sendMessage()
        composeTestRule.waitForIdle()

        val messages = viewModel.state.first().messages
        val assistantMessage = messages.firstOrNull { it.role == MessageRole.ASSISTANT }
        assertNotNull("Debe haber respuesta del asistente", assistantMessage)
        assertEquals(
            "Hola, soy Focus AI. ¿En qué puedo ayudarte?",
            assistantMessage?.content
        )
    }

    @Test
    fun sendMessage_clearsCurrentMessage_afterSending() = runTest {
        viewModel.onMessageChange("Hola")
        viewModel.sendMessage()
        composeTestRule.waitForIdle()

        val currentMessage = viewModel.state.first().currentMessage
        assertEquals("El campo debe quedar vacío tras enviar", "", currentMessage)
    }

    @Test
    fun sendMessage_setsSessionId_fromResponse() = runTest {
        viewModel.onMessageChange("Hola")
        viewModel.sendMessage()
        composeTestRule.waitForIdle()

        val sessionId = viewModel.state.first().sessionId
        assertEquals("session-123", sessionId)
    }

    @Test
    fun sendMessage_isLoadingFalse_afterCompletion() = runTest {
        viewModel.onMessageChange("Hola")
        viewModel.sendMessage()
        composeTestRule.waitForIdle()

        val isLoading = viewModel.state.first().isLoading
        assertFalse("isLoading debe ser false tras completar", isLoading)
    }

    @Test
    fun sendMessage_callsRepository_exactlyOnce() = runTest {
        viewModel.onMessageChange("Hola")
        viewModel.sendMessage()
        composeTestRule.waitForIdle()

        assertEquals("sendMessage debe llamarse una sola vez", 1, fakeRepository.sendMessageCallCount)
    }

    @Test
    fun sendMessage_doesNothing_whenMessageIsBlank() = runTest {
        viewModel.onMessageChange("   ")
        viewModel.sendMessage()
        composeTestRule.waitForIdle()

        assertEquals("No debe llamar al repo con mensaje vacío", 0, fakeRepository.sendMessageCallCount)
        assertTrue("Lista de mensajes debe seguir vacía", viewModel.state.first().messages.isEmpty())
    }

    @Test
    fun sendMessage_withInitialMessage_sendsThatMessage() = runTest {
        viewModel.sendMessage(initialMessage = "¿Cómo manejar el estrés?")
        composeTestRule.waitForIdle()

        assertEquals("¿Cómo manejar el estrés?", fakeRepository.lastSentMessage)
    }

    // ── MANEJO DE ERRORES ─────────────────────────────────────────────────────

    @Test
    fun sendMessage_setsError_whenRepositoryFails() = runTest {
        fakeRepository.sendMessageResult = Result.failure(Exception("Sin conexión"))

        viewModel.onMessageChange("Hola")
        viewModel.sendMessage()
        composeTestRule.waitForIdle()

        val error = viewModel.state.first().error
        assertNotNull("Debe haber un error", error)
        assertEquals("Sin conexión", error)
    }

    @Test
    fun sendMessage_isLoadingFalse_afterError() = runTest {
        fakeRepository.sendMessageResult = Result.failure(Exception("Sin conexión"))

        viewModel.onMessageChange("Hola")
        viewModel.sendMessage()
        composeTestRule.waitForIdle()

        assertFalse(viewModel.state.first().isLoading)
    }

    @Test
    fun clearError_removesErrorFromState() = runTest {
        fakeRepository.sendMessageResult = Result.failure(Exception("Error"))

        viewModel.onMessageChange("Hola")
        viewModel.sendMessage()
        composeTestRule.waitForIdle()

        viewModel.clearError()
        composeTestRule.waitForIdle()

        assertNull("El error debe estar limpio", viewModel.state.first().error)
    }

    // ── ESTADÍSTICAS DE USO ───────────────────────────────────────────────────

    @Test
    fun loadUsageStats_setsStats_onSuccess() = runTest {
        fakeRepository.getUsageStatsResult = Result.success(FakeAIChatRepository.defaultUsageStats())

        viewModel.loadUsageStats()
        composeTestRule.waitForIdle()

        val stats = viewModel.state.first().usageStats
        assertNotNull(stats)
        assertEquals(8, stats?.remainingMessages)
        assertEquals("Free", stats?.plan)
    }

    @Test
    fun loadUsageStats_showsLimitWarning_whenRemainingIsLow() = runTest {
        fakeRepository.getUsageStatsResult = Result.success(FakeAIChatRepository.lowRemainingStats())

        viewModel.loadUsageStats()
        composeTestRule.waitForIdle()

        val showWarning = viewModel.state.first().showLimitWarning
        assertTrue("Debe mostrar warning cuando quedan 2 mensajes en plan Free", showWarning)
    }

    @Test
    fun loadUsageStats_doesNotShowWarning_whenEnoughMessages() = runTest {
        fakeRepository.getUsageStatsResult = Result.success(FakeAIChatRepository.defaultUsageStats())

        viewModel.loadUsageStats()
        composeTestRule.waitForIdle()

        val showWarning = viewModel.state.first().showLimitWarning
        assertFalse("No debe mostrar warning con 8 mensajes restantes", showWarning)
    }

    // ── NUEVA CONVERSACIÓN ────────────────────────────────────────────────────

    @Test
    fun startNewConversation_clearsMessages() = runTest {
        viewModel.onMessageChange("Hola")
        viewModel.sendMessage()
        composeTestRule.waitForIdle()

        viewModel.startNewConversation()
        composeTestRule.waitForIdle()

        val messages = viewModel.state.first().messages
        assertTrue("Los mensajes deben estar vacíos", messages.isEmpty())
    }

    @Test
    fun startNewConversation_clearsSessionId() = runTest {
        viewModel.onMessageChange("Hola")
        viewModel.sendMessage()
        composeTestRule.waitForIdle()

        viewModel.startNewConversation()
        composeTestRule.waitForIdle()

        val sessionId = viewModel.state.first().sessionId
        assertNull("El sessionId debe ser null", sessionId)
    }

    // ── CARGA DE SESIÓN ───────────────────────────────────────────────────────

    @Test
    fun loadSession_setsMessages_fromRepository() = runTest {
        val existingMessages = listOf(
            com.softfocus.features.ai.domain.models.ChatMessage(
                role = MessageRole.USER,
                content = "Mensaje previo",
                timestamp = java.time.LocalDateTime.now()
            )
        )
        fakeRepository.getSessionMessagesResult = Result.success(existingMessages)

        viewModel.loadSession("session-abc")
        composeTestRule.waitForIdle()

        val messages = viewModel.state.first().messages
        assertEquals(1, messages.size)
        assertEquals("Mensaje previo", messages.first().content)
    }

    @Test
    fun loadSession_setsSessionId_fromParameter() = runTest {
        fakeRepository.getSessionMessagesResult = Result.success(emptyList())

        viewModel.loadSession("session-abc")
        composeTestRule.waitForIdle()

        val sessionId = viewModel.state.first().sessionId
        assertEquals("session-abc", sessionId)
    }

    // ── CAMBIO DE MENSAJE ─────────────────────────────────────────────────────

    @Test
    fun onMessageChange_updatesCurrentMessage() = runTest {
        viewModel.onMessageChange("Escribiendo...")
        val current = viewModel.state.first().currentMessage
        assertEquals("Escribiendo...", current)
    }
}
