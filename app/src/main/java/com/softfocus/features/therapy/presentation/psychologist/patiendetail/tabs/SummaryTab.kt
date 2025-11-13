package com.softfocus.features.therapy.presentation.psychologist.patiendetail.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.features.therapy.presentation.psychologist.patiendetail.PatientCheckInState
import com.softfocus.features.therapy.presentation.psychologist.patiendetail.components.EvolutionChart
import com.softfocus.features.therapy.presentation.psychologist.patiendetail.components.LastCheckInCard
import com.softfocus.features.therapy.presentation.psychologist.patiendetail.primaryGreen
import com.softfocus.ui.theme.CrimsonSemiBold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryTab(state: PatientCheckInState) {

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = primaryGreen
            )
        } else if (state.error != null) {
            Text(
                text = "Error: ${state.error}",
                color = Color.Red,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Último registro",
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    style = CrimsonSemiBold.copy(fontSize = 21.sp),
                    color = primaryGreen
                )
                Spacer(modifier = Modifier.height(21.dp))

                LastCheckInCard(state = state)

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Evolución",
                    style = CrimsonSemiBold.copy(fontSize = 21.sp),
                    color = primaryGreen,
                    modifier = Modifier
                        .padding(16.dp),
                )
                Spacer(modifier = Modifier.height(21.dp))
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    EvolutionChart(
                        lineData = state.weeklyChartLineData,
                        columnData = state.weeklyChartColumnData,
                        isLoading = state.isChartLoading
                    )
                }
            }
        }
    }
}