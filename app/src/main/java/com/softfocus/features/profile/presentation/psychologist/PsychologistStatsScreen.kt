package com.softfocus.features.profile.presentation.psychologist

import androidx.compose.animation.core.animateFloatAsState
import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.R
import com.softfocus.features.home.presentation.psychologist.PsychologistHomeViewModel
import com.softfocus.features.home.presentation.psychologist.components.getEmotionalEmoji
import com.softfocus.ui.theme.*
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PsychologistStatsScreen(
    viewModel: PsychologistHomeViewModel,
    onNavigateBack: () -> Unit
) {
    val stats by viewModel.stats.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    var fromDate by remember { mutableStateOf<String?>(null) }
    var toDate by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mis Estadísticas",
                        style = CrimsonSemiBold,
                        color = Green37,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Green37
                        )
                    }
                },
                actions = {
                    // Botón de refresh
                    val rotation by animateFloatAsState(
                        targetValue = if (isRefreshing) 360f else 0f,
                        label = "refresh_rotation"
                    )

                    IconButton(
                        onClick = { viewModel.refreshStats(fromDate, toDate) },
                        enabled = !isRefreshing
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizar",
                            tint = Green65,
                            modifier = Modifier.rotate(rotation)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White
                )
            )
        },
        containerColor = White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Filtro de fechas
            DateFilterCard(
                fromDate = fromDate,
                toDate = toDate,
                onFromDateSelected = { date -> fromDate = date },
                onToDateSelected = { date -> toDate = date },
                onClearFilter = {
                    fromDate = null
                    toDate = null
                    viewModel.refreshStats(null, null)
                },
                onApplyFilter = {
                    viewModel.refreshStats(fromDate, toDate)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            stats?.let { statsData ->
                // Card: Pacientes Activos
                StatsDetailCard(
                    icon = R.drawable.ic_profile_user,
                    title = "Pacientes Activos",
                    value = if (statsData.activePatientsCount > 0)
                        statsData.activePatientsCount.toString()
                    else "-",
                    subtitle = if (statsData.activePatientsCount > 0)
                        "Pacientes en tratamiento activo"
                    else "No tienes pacientes activos"
                )

                // Card: Alertas Pendientes
                StatsDetailCard(
                    icon = R.drawable.ic_alert,
                    title = "Alertas de Crisis Pendientes",
                    value = if (statsData.pendingCrisisAlerts > 0)
                        statsData.pendingCrisisAlerts.toString()
                    else "-",
                    subtitle = if (statsData.pendingCrisisAlerts > 0)
                        "Alertas que requieren tu atención"
                    else "No hay alertas pendientes",
                    isAlert = statsData.pendingCrisisAlerts > 0
                )

                // Card: Check-ins Completados Hoy
                StatsDetailCard(
                    icon = R.drawable.ic_calendar,
                    title = "Check-ins Completados Hoy",
                    value = if (statsData.todayCheckInsCompleted > 0)
                        statsData.todayCheckInsCompleted.toString()
                    else "-",
                    subtitle = if (statsData.todayCheckInsCompleted > 0)
                        "${statsData.todayCheckInsCompleted} de ${statsData.activePatientsCount} pacientes completaron su check-in"
                    else "Ningún paciente ha completado su check-in hoy"
                )

                // Card: Adherencia Promedio
                StatsDetailCard(
                    icon = null,
                    title = "Tasa de Adherencia (Últimos 30 días)",
                    value = if (statsData.averageAdherenceRate > 0)
                        "${String.format("%.1f", statsData.averageAdherenceRate)}%"
                    else "-",
                    subtitle = if (statsData.averageAdherenceRate > 0)
                        "Porcentaje promedio de cumplimiento de check-ins diarios"
                    else "Sin datos de adherencia"
                )

                // Card: Pacientes Nuevos Este Mes
                StatsDetailCard(
                    icon = R.drawable.ic_profile_user,
                    title = "Pacientes Nuevos Este Mes",
                    value = if (statsData.newPatientsThisMonth > 0)
                        statsData.newPatientsThisMonth.toString()
                    else "-",
                    subtitle = if (statsData.newPatientsThisMonth > 0)
                        "Nuevos pacientes que iniciaron tratamiento este mes"
                    else "No has recibido pacientes nuevos este mes"
                )

                // Card: Estado Emocional Promedio con Emoji
                StatsEmotionalCard(
                    emoji = if (statsData.averageEmotionalLevel > 0)
                        getEmotionalEmoji(statsData.averageEmotionalLevel)
                    else R.drawable.calendar_emoji_serius,
                    title = "Estado Emocional Promedio",
                    value = if (statsData.averageEmotionalLevel > 0)
                        String.format("%.1f", statsData.averageEmotionalLevel)
                    else "-",
                    maxValue = "10.0",
                    subtitle = if (statsData.averageEmotionalLevel > 0)
                        "Promedio del nivel emocional de tus pacientes hoy"
                    else "Sin datos de estado emocional"
                )

                // Fecha de generación
                Text(
                    text = "Última actualización: ${formatDateTime(statsData.statsGeneratedAt)}",
                    style = SourceSansRegular,
                    fontSize = 12.sp,
                    color = GrayA2,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            } ?: run {
                // Loading state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Green65)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StatsDetailCard(
    icon: Int?,
    title: String,
    value: String,
    subtitle: String,
    isAlert: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAlert) Color(0xFFFFF3E0) else White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header con ícono y título
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (icon != null) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        tint = if (isAlert) Color(0xFFFF6B00) else Green65,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = title,
                    style = CrimsonSemiBold,
                    fontSize = 18.sp,
                    color = Black
                )
            }

            // Valor grande
            Text(
                text = value,
                style = CrimsonSemiBold,
                fontSize = 48.sp,
                color = if (isAlert) Color(0xFFFF6B00) else Green65,
                textAlign = TextAlign.Start
            )

            // Subtítulo descriptivo
            Text(
                text = subtitle,
                style = SourceSansRegular,
                fontSize = 14.sp,
                color = GrayA2,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun StatsEmotionalCard(
    emoji: Int,
    title: String,
    value: String,
    maxValue: String,
    subtitle: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header con emoji y título
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = painterResource(id = emoji),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = title,
                    style = CrimsonSemiBold,
                    fontSize = 18.sp,
                    color = Black
                )
            }

            // Valor con escala
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = value,
                    style = CrimsonSemiBold,
                    fontSize = 48.sp,
                    color = Green65
                )
                Text(
                    text = "/ $maxValue",
                    style = SourceSansRegular,
                    fontSize = 20.sp,
                    color = GrayA2,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Subtítulo descriptivo
            Text(
                text = subtitle,
                style = SourceSansRegular,
                fontSize = 14.sp,
                color = GrayA2,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun DateFilterCard(
    fromDate: String?,
    toDate: String?,
    onFromDateSelected: (String) -> Unit,
    onToDateSelected: (String) -> Unit,
    onClearFilter: () -> Unit,
    onApplyFilter: () -> Unit
) {
    val context = LocalContext.current

    // DatePicker para "Desde"
    val fromDatePickerDialog = remember {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                onFromDateSelected(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    // DatePicker para "Hasta"
    val toDatePickerDialog = remember {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                onToDateSelected(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Título
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = Green65,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Filtro de Fechas",
                        style = CrimsonSemiBold,
                        fontSize = 16.sp,
                        color = Black
                    )
                }

                // Botón limpiar
                if (fromDate != null || toDate != null) {
                    TextButton(onClick = onClearFilter) {
                        Text(
                            text = "Limpiar",
                            style = SourceSansRegular,
                            fontSize = 14.sp,
                            color = Green65
                        )
                    }
                }
            }

            // Descripción
            Text(
                text = if (fromDate != null && toDate != null) {
                    "Estadísticas desde $fromDate hasta $toDate"
                } else {
                    "Selecciona un rango de fechas para filtrar"
                },
                style = SourceSansRegular,
                fontSize = 12.sp,
                color = GrayA2
            )

            // Botones de selección de fechas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón "Desde"
                OutlinedButton(
                    onClick = { fromDatePickerDialog.show() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (fromDate != null) Green65.copy(alpha = 0.1f) else Color.Transparent
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = Green65,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Desde",
                                style = SourceSansRegular,
                                fontSize = 12.sp,
                                color = Green65
                            )
                        }
                        Text(
                            text = fromDate ?: "No seleccionada",
                            style = if (fromDate != null) CrimsonSemiBold else SourceSansRegular,
                            fontSize = 13.sp,
                            color = if (fromDate != null) Black else GrayA2
                        )
                    }
                }

                // Botón "Hasta"
                OutlinedButton(
                    onClick = { toDatePickerDialog.show() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (toDate != null) Green65.copy(alpha = 0.1f) else Color.Transparent
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = Green65,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Hasta",
                                style = SourceSansRegular,
                                fontSize = 12.sp,
                                color = Green65
                            )
                        }
                        Text(
                            text = toDate ?: "No seleccionada",
                            style = if (toDate != null) CrimsonSemiBold else SourceSansRegular,
                            fontSize = 13.sp,
                            color = if (toDate != null) Black else GrayA2
                        )
                    }
                }
            }

            // Botón aplicar filtro
            if (fromDate != null && toDate != null) {
                Button(
                    onClick = { onApplyFilter() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Green65)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Aplicar Filtro",
                        style = CrimsonSemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

private fun formatDateTime(dateTimeString: String): String {
    return try {
        // Asumiendo formato ISO, puedes ajustarlo según necesites
        val parts = dateTimeString.split("T")
        if (parts.size >= 2) {
            val date = parts[0]
            val time = parts[1].split(".")[0]
            "$date $time"
        } else {
            dateTimeString
        }
    } catch (e: Exception) {
        dateTimeString
    }
}
