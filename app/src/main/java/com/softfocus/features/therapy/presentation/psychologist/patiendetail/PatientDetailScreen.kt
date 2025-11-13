package com.softfocus.features.therapy.presentation.psychologist.patiendetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.SourceSansRegular
import com.softfocus.core.navigation.Route
import com.softfocus.features.therapy.presentation.psychologist.patiendetail.components.PatientDetailHeader
import com.softfocus.features.therapy.presentation.psychologist.patiendetail.tabs.SummaryTab
import com.softfocus.features.therapy.presentation.psychologist.patiendetail.tabs.TasksTab

// --- Colores (puedes moverlos a un archivo Theme.kt) ---
val primaryGreen = Color(0xFF4B634B)
val lightGreen = Color(0xFFB5C9B5)
val cardBackground = Color(0xFFF7F7F3)
val lightGrayText = Color.Gray

// --- Pantalla Principal de Detalles ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientDetailScreen(
    navController: NavHostController,
    viewModel: PatientDetailViewModel,
    onBack: () -> Unit,
    patientId: String,
    relationshipId: String,
    patientName: String
) {
    // Estado para saber qué pestaña está seleccionada
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Resumen", "Tareas", "Chat")
    val summaryState by viewModel.summaryState.collectAsState()
    val checkInState by viewModel.checkInState.collectAsState()
    val tasksState by viewModel.tasksState.collectAsState()

    Scaffold(
        topBar = { PatientDetailTopBar(onBack = onBack) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Cabecera del Paciente ---
            item {
                PatientDetailHeader(summaryState = summaryState)
            }

            // --- Pestañas (Tabs) ---
            item {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    contentColor = primaryGreen,
                    modifier = Modifier.padding(horizontal = 42.dp),
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = primaryGreen,
                            height = 2.dp
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = {
                                if (index != 2) {
                                    selectedTabIndex = index
                                } else {
                                    navController.navigate(
                                        Route.PsychologistPatientChat.createRoute(
                                            patientId = patientId,
                                            relationshipId = relationshipId,
                                            patientName = patientName
                                        )
                                    )
                                }
                            },
                            text = {
                                Text(
                                    text = title,
                                    style = SourceSansRegular.copy(fontSize = 17.sp),
                                    color = if (selectedTabIndex == index) primaryGreen else Color.Gray
                                )
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(21.dp))
            }

            // --- Contenido de la Pestaña ---
            item {
                // Muestra el contenido basado en la pestaña seleccionada
                when (selectedTabIndex) {
                    0 -> SummaryTab(checkInState)
                    1 -> TasksTab(
                        tasksState = tasksState
                    )
                }
            }
        }
    }
}

// --- Componentes de PatientDetailScreen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientDetailTopBar(
    onBack: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Pacientes",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = CrimsonSemiBold.copy(fontSize = 32.sp),
                color = Green49
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.Black
                )
            }
        },
        actions = {
            Spacer(modifier = Modifier.width(48.dp)) // Espaciador para centrar título
        }
    )
}