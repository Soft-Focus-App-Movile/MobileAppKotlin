package com.softfocus.uitests.notifications

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.softfocus.features.notifications.domain.models.*
import com.softfocus.features.notifications.presentation.list.NotificationsScreen
import com.softfocus.features.notifications.presentation.list.NotificationsState
import com.softfocus.features.notifications.presentation.list.NotificationsViewModel
import com.softfocus.ui.theme.SoftFocusMobileTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class NotificationsUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun validacion_US37_notificaciones_push_test() {
        // 1. Configuración del Mock
        val viewModelMock = mockk<NotificationsViewModel>(relaxed = true)

        // 2. Creamos exactamente los datos que exige la US37
        val fakeNotifications = listOf(
            // Dato para Escenario 2 de US37: Alerta de crisis (Prioridad Alta/Crítica)
            Notification(
                id = "1",
                userId = "paciente_123",
                title = "Alerta de Crisis",
                content = "El paciente ha activado el botón de emergencia.",
                status = DeliveryStatus.DELIVERED,
                type = NotificationType.CRISIS_ALERT,
                priority = Priority.CRITICAL, // Esto activa el badge de "!" en tu código
                createdAt = LocalDateTime.now().minusMinutes(2),
                scheduledAt = LocalDateTime.now().minusMinutes(2),
                deliveredAt = LocalDateTime.now().minusMinutes(2),
                readAt = null
            ),
            // Dato para Escenario 1 de US37: Recordatorio de Check-in
            Notification(
                id = "2",
                userId = "user_456",
                title = "Recordatorio de Check-in",
                content = "Es hora de tu registro diario. ¿Cómo te sientes hoy?",
                status = DeliveryStatus.DELIVERED,
                type = NotificationType.CHECKIN_REMINDER,
                priority = Priority.NORMAL,
                createdAt = LocalDateTime.now().minusHours(1),
                scheduledAt = LocalDateTime.now().minusHours(1),
                deliveredAt = LocalDateTime.now().minusHours(1),
                readAt = null
            )
        )

        val fakeState = NotificationsState(
            notifications = fakeNotifications,
            isLoading = false,
            notificationsEnabled = true
        )

        every { viewModelMock.state } returns MutableStateFlow(fakeState)

        // 3. Lanzar la pantalla
        composeTestRule.setContent {
            SoftFocusMobileTheme {
                NotificationsScreen(
                    onNavigateBack = { },
                    viewModel = viewModelMock
                )
            }
        }


        Thread.sleep(1500)

        // Verificamos que cargó la pantalla
        composeTestRule.onNodeWithText("Notificaciones").assertIsDisplayed()

        // ---------------------------------------------------------
        // VALIDACIÓN ESCENARIO 2: Alertas de crisis con prioridad alta
        // ---------------------------------------------------------
        // Comprueba que la alerta de crisis llegó
        composeTestRule.onNodeWithText("Alerta de Crisis").assertIsDisplayed()

        // Comprueba que el sistema muestra visualmente que es prioridad CRÍTICA
        composeTestRule.onNodeWithText("!").assertIsDisplayed()

        Thread.sleep(2000) // Pausa para que veas la alerta crítica en pantalla

        // ---------------------------------------------------------
        // VALIDACIÓN ESCENARIO 1: Recordatorios de check-in
        // ---------------------------------------------------------
        // Comprueba que el recordatorio de check-in llegó a la bandeja
        composeTestRule.onNodeWithText("Recordatorio de Check-in").assertIsDisplayed()

        // Expandimos el check-in para ver el contenido completo
        composeTestRule.onNodeWithText("Recordatorio de Check-in").performClick()

        // Verificamos el texto exacto del recordatorio
        composeTestRule.onNodeWithText("Es hora de tu registro diario. ¿Cómo te sientes hoy?").assertIsDisplayed()

        // Pausa final  (4 segundos) para demostrar que ambos escenarios de la US37 están en la pantalla.
        Thread.sleep(4000)
    }
}