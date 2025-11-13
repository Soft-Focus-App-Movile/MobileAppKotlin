package com.softfocus.features.therapy.presentation.psychologist.patiendetail

import SummaryTab
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.patrykandpatryk.vico.compose.axis.axisLabelComponent
import com.patrykandpatryk.vico.compose.axis.horizontal.bottomAxis
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.SourceSansRegular
import com.patrykandpatryk.vico.compose.axis.vertical.startAxis
import com.patrykandpatryk.vico.compose.chart.Chart
import com.patrykandpatryk.vico.compose.chart.column.columnChart
import com.patrykandpatryk.vico.compose.chart.line.lineChart
import com.patrykandpatryk.vico.compose.chart.line.lineSpec
import com.patrykandpatryk.vico.compose.component.shape.shader.verticalGradient
import com.patrykandpatryk.vico.core.axis.AxisPosition
import com.patrykandpatryk.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatryk.vico.core.chart.composed.plus
import com.patrykandpatryk.vico.core.component.shape.LineComponent
import com.patrykandpatryk.vico.core.entry.composed.plus
import com.patrykandpatryk.vico.core.entry.entryModelOf
import com.softfocus.core.navigation.Route
import com.softfocus.features.therapy.presentation.psychologist.patiendetail.components.PatientDetailHeader
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvolucionChart() {

    // 1. Datos para el gráfico de línea (7 días, con un pico en Jueves)
    val lineEntryModel = entryModelOf(2f, 3f, 2.5f, 5f, 3.5f, 4f, 3f)

    // 2. Datos para la barra (solo en la 4ª posición, "Th")
    val columnEntryModel = entryModelOf(0f, 0f, 0f, 5f, 0f, 0f, 0f)

    // 3. Define el gráfico de Columna (la barra de "Th")
    val columnChart = columnChart(
        columns = listOf(
            // La barra sólida de color verde claro
            LineComponent(
                color = lightGreen.copy(alpha = 0.5f).hashCode(),
                thicknessDp = 24f
            )
        )
    )

    // 4. Define el gráfico de Línea (la línea principal)
    val lineChart = lineChart(
        lines = listOf(
            lineSpec(
                lineColor = Color(0xFFABBC8A),
                lineThickness = 2.dp,
                // El relleno de área bajo la línea
                lineBackgroundShader = verticalGradient(
                    arrayOf(Color(0xFFABBC8A).copy(alpha = 0.4f), Color(0xFFABBC8A).copy(alpha = 0.0f)),
                )
            )
        )
    )

    // 5. Combina ambos gráficos
    val composedChart = columnChart.plus(lineChart)

    // 6. Definir el formateador del eje X
    val days = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")
    val bottomAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, chartValues ->
            // Lo convertimos a Int y lo usamos como índice para nuestra lista de días
            days.getOrNull(value.toInt()) ?: ""
        }

    // 6. Muestra el gráfico
    Chart(
        chart = composedChart,
        model = columnEntryModel.plus(lineEntryModel), // Combina los datos
        modifier = Modifier
            .height(150.dp)
            .padding(horizontal = 2.dp),
        // Ocultamos el eje Y (startAxis) para que se vea limpio como en tu imagen
        startAxis = null,
        // Ocultamos el eje X (bottomAxis) porque ya ponemos las etiquetas manualmente
        bottomAxis = bottomAxis(
            valueFormatter = bottomAxisValueFormatter,
            // Personaliza la apariencia del texto
            label = axisLabelComponent(
                color = lightGrayText,
                horizontalPadding = 1.dp
                // Puedes ajustar el tamaño, etc.
                // textSize = 12.sp,
            ),
            // Oculta la línea del eje y los "ticks" (marcas)
            axis = null,
            tick = null,
            guideline = null
        )
    )
}