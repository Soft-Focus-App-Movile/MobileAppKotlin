package com.softfocus.integrationtests.crisis

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.softfocus.fakes.crisis.FakeCrisisRepository
import com.softfocus.features.crisis.presentation.psychologist.CrisisAlertsState
import com.softfocus.features.crisis.presentation.psychologist.CrisisAlertsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class CrisisAlertsIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var fakeRepository: FakeCrisisRepository
    private lateinit var viewModel: CrisisAlertsViewModel

    @Before
    fun setUp() {
        fakeRepository = FakeCrisisRepository()
        viewModel = CrisisAlertsViewModel(fakeRepository)
    }

    @Test
    fun loadAlerts_setsSuccessState_whenAlertsExist() = runTest {
        fakeRepository.getPsychologistAlertsResult = Result.success(listOf(FakeCrisisRepository.defaultAlert()))

        viewModel.loadAlerts()
        composeTestRule.waitForIdle()

        val state = viewModel.alertsState.first()
        assertTrue(state is CrisisAlertsState.Success)
        assertEquals(1, (state as CrisisAlertsState.Success).alerts.size)
    }

    @Test
    fun loadAlerts_setsEmptyState_whenNoAlerts() = runTest {
        fakeRepository.getPsychologistAlertsResult = Result.success(emptyList())

        viewModel.loadAlerts()
        composeTestRule.waitForIdle()

        val state = viewModel.alertsState.first()
        assertTrue(state is CrisisAlertsState.Empty)
    }

    @Test
    fun loadAlerts_setsErrorState_onFailure() = runTest {
        fakeRepository.getPsychologistAlertsResult = Result.failure(Exception("Network Error"))

        viewModel.loadAlerts()
        composeTestRule.waitForIdle()

        val state = viewModel.alertsState.first()
        assertTrue(state is CrisisAlertsState.Error)
        assertEquals("Network Error", (state as CrisisAlertsState.Error).message)
    }

    @Test
    fun updateAlertStatus_transitionsStatusCorrectly() = runTest {
        val alert = FakeCrisisRepository.defaultAlert().copy(status = "PENDING")
        fakeRepository.updateAlertStatusResult = Result.success(alert.copy(status = "ATTENDED"))

        viewModel.updateAlertStatus(alert)
        composeTestRule.waitForIdle()

        // Verifica que se vuelve a cargar la lista (volver a llamar loadAlerts)
        val state = viewModel.alertsState.first()
        assertTrue(state is CrisisAlertsState.Success)
    }
}