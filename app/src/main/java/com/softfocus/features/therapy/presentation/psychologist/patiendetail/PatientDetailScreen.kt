package com.softfocus.features.therapy.presentation.psychologist.patiendetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatryk.vico.compose.axis.axisLabelComponent
import com.patrykandpatryk.vico.compose.axis.horizontal.bottomAxis
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.SourceSansRegular
import com.softfocus.ui.theme.SourceSansSemiBold
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
import com.softfocus.features.therapy.presentation.psychologist.patiendetail.tabs.PatientChatScreen

// --- Colores (puedes moverlos a un archivo Theme.kt) ---
val primaryGreen = Color(0xFF4B634B)
val lightGreen = Color(0xFFB5C9B5)
val cardBackground = Color(0xFFF7F7F3)
val lightGrayText = Color.Gray

// --- Pantalla Principal de Detalles ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientDetailScreen() {
    // Estado para saber qué pestaña está seleccionada
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Resumen", "Tareas", "Chat")

    Scaffold(
        topBar = { PatientDetailTopBar() }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Cabecera del Paciente ---
            item {
                PatientDetailHeader()
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
                                // El tab "Chat" (índice 2) no cambia el contenido aquí,
                                // sino que navegaría a la otra pantalla (PatientChatScreen)
                                if (index != 2) {
                                    selectedTabIndex = index
                                } else {
                                    // TODO: Aquí iría la lógica de navegación
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
                    0 -> ResumenTabContent()
                    1 -> TareasTabContent()
                }
            }
        }
    }
}

// --- Componentes de PatientDetailScreen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientDetailTopBar() {
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
            IconButton(onClick = { /* Sin acción */ }) {
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
fun PatientDetailHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Placeholder para la imagen
        Image(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Foto de Ana García",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Ana García",
            style = CrimsonSemiBold.copy(fontSize = 30.sp),
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "32 años",
            style = SourceSansSemiBold.copy(fontSize = 13.sp),
            color = lightGrayText
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Paciente desde Enero 2025",
            style = SourceSansSemiBold.copy(fontSize = 13.sp),
            color = primaryGreen
        )
    }
}

// --- Contenido de la Pestaña "Resumen" ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenTabContent() {
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
        // --- MODIFICACIÓN AQUÍ ---
        // 1. Define la lista de tags
        val tagsDelRegistro = listOf("Ansiedad", "Depresión")

        // 2. Pasa la lista a la tarjeta
        UltimoRegistroCard(tags = tagsDelRegistro)
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
            EvolucionChart() // El gráfico se dibujará dentro de este Column
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UltimoRegistroCard(tags: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Hoy",
                    style = CrimsonSemiBold.copy(fontSize = 20.sp),
                    color = Color.Black
                )
                Text(
                    text = "9/10", // Nivel de como se siente el paciente
                    modifier = Modifier
                        .background(
                            color = Color(0xFFCBCD9C),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    style = SourceSansRegular.copy(fontSize = 10.sp),
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                tags.forEach { tagText ->
                    TagItem(text = tagText)
                    Spacer(modifier = Modifier.width(8.dp)) // Espacio entre tags
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Me siento abrumada por el trabajo",
                style = SourceSansRegular.copy(fontSize = 12.sp),
                color = Color.Black
            )
        }
    }
}

@Composable
fun TagItem(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .background(
                color = Color(0xFFCBCD9C),
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        style = SourceSansRegular.copy(fontSize = 10.sp),
        color = Color.White
    )
}

// --- Contenido de la Pestaña "Tareas" ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareasTabContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dropdown (simulado)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Todo", fontSize = 16.sp)
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Filtrar")
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Lista de Tareas
        TareaItemCard(
            icon = Icons.Default.Movie,
            title = "Ver Inside Out",
            status = "Completado",
            date = "Asignado el 15 Enero"
        )
        Spacer(modifier = Modifier.height(12.dp))
        TareaItemCard(
            icon = Icons.Default.MusicNote,
            title = "Escuchar Música de relajación",
            status = "Pendiente",
            date = "Asignado el 15 Enero"
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { /* Sin acción */ },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCBCD9C))
        ) {
            Text(
                text = "Asignar nueva Tarea",
                color = Color.Black,
                style = SourceSansRegular.copy(fontSize = 13.sp),
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareaItemCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, status: String, date: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = primaryGreen,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = CrimsonSemiBold.copy(fontSize = 18.sp),
                )
                Text(
                    text = status,
                    style = SourceSansRegular.copy(fontSize = 11.sp),
                    color =
                        if (status == "Completado") primaryGreen
                        else lightGrayText
                )
                Text(
                    text = date,
                    style = SourceSansRegular.copy(fontSize = 13.sp),
                    color = lightGrayText
                )
            }
        }
    }
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

// --- Preview ---
@Preview(showBackground = true)
@Composable
fun PatientDetailScreenPreview() {
    MaterialTheme {
        PatientDetailScreen()
    }
}