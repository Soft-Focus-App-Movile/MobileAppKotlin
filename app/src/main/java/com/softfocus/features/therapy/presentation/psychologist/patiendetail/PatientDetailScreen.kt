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
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.SourceSansRegular
import com.softfocus.ui.theme.SourceSansSemiBold

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
                                    // navController.navigate("chat/ana_garcia")
                                }
                            },
                            text = {
                                Text(
                                    text = title,
                                    style = SourceSansRegular.copy(fontSize = 15.sp),
                                    color = if (selectedTabIndex == index) primaryGreen else Color.Gray
                                )
                            }
                        )
                    }
                }
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
            style = SourceSansSemiBold.copy(fontSize = 11.sp),
            color = lightGrayText
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Paciente desde Enero 2025",
            style = SourceSansSemiBold.copy(fontSize = 11.sp),
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
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Último registro",
            style = CrimsonSemiBold.copy(fontSize = 21.sp),
            color = primaryGreen
        )
        Spacer(modifier = Modifier.height(16.dp))
        UltimoRegistroCard()
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Evolución",
            style = CrimsonSemiBold.copy(fontSize = 21.sp),
            color = primaryGreen
        )
        Spacer(modifier = Modifier.height(16.dp))
        EvolucionChartPlaceholder() // Placeholder para el gráfico
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UltimoRegistroCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    text = "9/10",
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
                // Tags
                Text(
                    "Ansiedad",
                    modifier = Modifier
                        .background(
                            color = Color(0xFFCBCD9C),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    style = SourceSansRegular.copy(fontSize = 10.sp),
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Depresión",
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
            Text(
                text = "Me siento abrumada por el trabajo",
                style = SourceSansRegular.copy(fontSize = 12.sp),
                color = Color.Black
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvolucionChartPlaceholder() {
    // Placeholder para el gráfico
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(cardBackground, RoundedCornerShape(16.dp))
            .border(1.dp, lightGreen, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text("Placeholder para Gráfico de Evolución", color = lightGrayText)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        val days = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")
        days.forEach { day ->
            Text(
                text = day,
                color = if (day == "Th") primaryGreen else lightGrayText,
                fontWeight = if (day == "Th") FontWeight.Bold else FontWeight.Normal
            )
        }
    }
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

// --- Preview ---
@Preview(showBackground = true)
@Composable
fun PatientDetailScreenPreview() {
    MaterialTheme {
        PatientDetailScreen()
    }
}