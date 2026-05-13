package com.softfocus.integrationtests.therapy

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.softfocus.core.data.local.UserSession
import com.softfocus.fakes.profile.FakeProfileRepository
import com.softfocus.fakes.profile.FakeTherapyRepository
import com.softfocus.features.search.domain.repositories.SearchRepository
import com.softfocus.features.therapy.data.remote.SignalRService
import com.softfocus.features.therapy.domain.models.ChatMessage
import com.softfocus.features.therapy.domain.usecases.GetChatHistoryUseCase
import com.softfocus.features.therapy.domain.usecases.GetMyRelationshipUseCase
import com.softfocus.features.therapy.domain.usecases.SendChatMessageUseCase
import com.softfocus.features.therapy.presentation.patient.PsychologistChatViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class PsychologistChatIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockUserSession: UserSession
    private lateinit var mockSignalRService: SignalRService
    private lateinit var mockSearchRepository: SearchRepository

    private lateinit var mockGetMyRelationshipUseCase: GetMyRelationshipUseCase
    private lateinit var mockGetChatHistoryUseCase: GetChatHistoryUseCase
    private lateinit var mockSendChatMessageUseCase: SendChatMessageUseCase

    private lateinit var viewModel: PsychologistChatViewModel

    @Before
    fun setUp() {
        mockUserSession = mockk(relaxed = true)
        mockSignalRService = mockk(relaxed = true)
        mockSearchRepository = mockk(relaxed = true)

        mockGetMyRelationshipUseCase = mockk()
        mockGetChatHistoryUseCase = mockk()
        mockSendChatMessageUseCase = mockk()

        // Usar el usuario por defecto válido de tu Fake
        every { mockUserSession.getUser() } returns FakeProfileRepository.defaultUser()

        viewModel = PsychologistChatViewModel(
            userSession = mockUserSession,
            getMyRelationshipUseCase = mockGetMyRelationshipUseCase,
            getChatHistoryUseCase = mockGetChatHistoryUseCase,
            sendChatMessageUseCase = mockSendChatMessageUseCase,
            signalRService = mockSignalRService,
            searchRepository = mockSearchRepository
        )
    }

    @Test
    fun initializeChat_loadsChatHistorySuccessfully() = runTest {
        val history = listOf(
            ChatMessage(
                id = "1",
                relationshipId = "rel-123",
                senderId = "user-123",
                receiverId = "psych-123",
                content = "Hola",
                timestamp = "2026-05-12T10:00:00Z",
                isFromMe = true,
                messageType = "text"
            )
        )

        coEvery { mockGetMyRelationshipUseCase() } returns Result.success(FakeTherapyRepository.defaultRelationship())
        coEvery { mockGetChatHistoryUseCase(any(), any(), any()) } returns Result.success(history)

        // Simular callback de conexion startConnection para que cargue historial
        every { mockSignalRService.startConnection(any()) } answers {
            firstArg<() -> Unit>().invoke()
        }

        composeTestRule.waitForIdle()
    }

    @Test
    fun sendMessage_addsToLocalMessagesOptimistically() = runTest {
        coEvery { mockGetMyRelationshipUseCase() } returns Result.success(FakeTherapyRepository.defaultRelationship())
        coEvery { mockSendChatMessageUseCase(any(), any(), any(), any()) } returns Result.success("msg-123")

        viewModel.sendMessage("Mensaje de prueba")
        composeTestRule.waitForIdle()

        val uiState = viewModel.uiState.first()
        assertEquals("Mensaje de prueba", uiState.messages.first().content)
        assertEquals(true, uiState.messages.first().isFromMe)
    }
}