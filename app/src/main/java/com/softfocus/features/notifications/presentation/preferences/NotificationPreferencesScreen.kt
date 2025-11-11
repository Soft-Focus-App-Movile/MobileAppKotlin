package com.softfocus.features.notifications.presentation.preferences

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.softfocus.features.auth.domain.models.UserType
import com.softfocus.ui.theme.*
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationPreferencesScreen(
    userType: UserType,
    onNavigateBack: () -> Unit,
    viewModel: NotificationPreferencesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showTimePickerDialog by remember { mutableStateOf(false) }

    // Una sola preferencia maestra
    val masterPreference = remember(state.preferences) {
        state.preferences.firstOrNull()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notificaciones",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = Gray222
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Gray222
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White,
                    titleContentColor = Gray222
                )
            )
        },
        containerColor = White
    ) { padding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Green49)
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = state.error!!,
                            color = RedE8
                        )
                        Button(
                            onClick = { viewModel.loadPreferences() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Green49
                            )
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp, vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            colors = CardDefaults.cardColors(
                                containerColor = GrayF5
                            ),
                            shape = MaterialTheme.shapes.medium,
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "Configuración",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Gray222
                                )

                                masterPreference?.let { pref ->
                                    // Switch principal
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Recibir notificaciones",
                                                fontSize = 14.sp,
                                                color = Gray222,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = if (pref.isEnabled) "Activado" else "Desactivado",
                                                fontSize = 12.sp,
                                                color = Gray808,
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }

                                        Switch(
                                            checked = pref.isEnabled,
                                            onCheckedChange = { viewModel.toggleMasterPreference() },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = White,
                                                checkedTrackColor = Green49,
                                                uncheckedThumbColor = White,
                                                uncheckedTrackColor = GrayB2
                                            )
                                        )
                                    }

                                    // Configuración de horario SOLO para psicólogos
                                    if (userType == UserType.PSYCHOLOGIST && pref.isEnabled) {
                                        HorizontalDivider(
                                            color = GrayD9,
                                            thickness = 0.5.dp,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )

                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Text(
                                                text = "Horario de recepción",
                                                fontSize = 14.sp,
                                                color = Gray222,
                                                fontWeight = FontWeight.Medium
                                            )

                                            Card(
                                                colors = CardDefaults.cardColors(
                                                    containerColor = White
                                                ),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(12.dp),
                                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Column {
                                                            Text(
                                                                text = "Desde",
                                                                fontSize = 12.sp,
                                                                color = Gray808
                                                            )
                                                            Text(
                                                                text = pref.schedule?.startTime?.format(
                                                                    DateTimeFormatter.ofPattern("HH:mm")
                                                                ) ?: "07:00",
                                                                fontSize = 16.sp,
                                                                color = Gray222,
                                                                fontWeight = FontWeight.Medium
                                                            )
                                                        }

                                                        Text(
                                                            text = "—",
                                                            fontSize = 16.sp,
                                                            color = Gray808
                                                        )

                                                        Column(horizontalAlignment = Alignment.End) {
                                                            Text(
                                                                text = "Hasta",
                                                                fontSize = 12.sp,
                                                                color = Gray808
                                                            )
                                                            Text(
                                                                text = pref.schedule?.endTime?.format(
                                                                    DateTimeFormatter.ofPattern("HH:mm")
                                                                ) ?: "00:00",
                                                                fontSize = 16.sp,
                                                                color = Gray222,
                                                                fontWeight = FontWeight.Medium
                                                            )
                                                        }

                                                        IconButton(
                                                            onClick = { showTimePickerDialog = true }
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.Info,
                                                                contentDescription = "Cambiar",
                                                                tint = Green49,
                                                                modifier = Modifier.size(20.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            Card(
                                                colors = CardDefaults.cardColors(
                                                    containerColor = GreenEC.copy(alpha = 0.5f)
                                                ),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(12.dp),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Info,
                                                        contentDescription = null,
                                                        tint = Green37,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Column {
                                                        Text(
                                                            text = "Dentro del horario:",
                                                            fontSize = 12.sp,
                                                            color = Green37,
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                        Text(
                                                            text = "Recibirás todas las notificaciones incluyendo alertas de crisis",
                                                            fontSize = 11.sp,
                                                            color = Green37
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                            text = "Fuera del horario:",
                                                            fontSize = 12.sp,
                                                            color = Green37,
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                        Text(
                                                            text = "Solo notificaciones no urgentes",
                                                            fontSize = 11.sp,
                                                            color = Green37
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Info adicional para pacientes/general
                                    if (userType != UserType.PSYCHOLOGIST && pref.isEnabled) {
                                        HorizontalDivider(
                                            color = GrayD9,
                                            thickness = 0.5.dp,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )

                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = GreenEC.copy(alpha = 0.5f)
                                            ),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Info,
                                                    contentDescription = null,
                                                    tint = Green37,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Text(
                                                    text = "Recibirás todas las notificaciones del sistema",
                                                    fontSize = 12.sp,
                                                    color = Green37
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Indicador de guardado
                        if (state.isSaving) {
                            Spacer(modifier = Modifier.height(16.dp))
                            CircularProgressIndicator(
                                color = Green49,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Mensaje de éxito
                        if (state.successMessage != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = GreenEC
                                )
                            ) {
                                Text(
                                    text = state.successMessage!!,
                                    modifier = Modifier.padding(12.dp),
                                    color = Green37,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo de selector de horario
    if (showTimePickerDialog && masterPreference != null) {
        TimeRangePickerDialog(
            startTime = masterPreference.schedule?.startTime ?: LocalTime.of(7, 0),
            endTime = masterPreference.schedule?.endTime ?: LocalTime.of(0, 0),
            onDismiss = { showTimePickerDialog = false },
            onConfirm = { start, end ->
                viewModel.updateSchedule(start, end)
                showTimePickerDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeRangePickerDialog(
    startTime: LocalTime,
    endTime: LocalTime,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime, LocalTime) -> Unit
) {
    var isSelectingStart by remember { mutableStateOf(true) }
    var tempStartTime by remember { mutableStateOf(startTime) }
    var tempEndTime by remember { mutableStateOf(endTime) }

    val timePickerState = rememberTimePickerState(
        initialHour = if (isSelectingStart) tempStartTime.hour else tempEndTime.hour,
        initialMinute = if (isSelectingStart) tempStartTime.minute else tempEndTime.minute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    if (isSelectingStart) {
                        tempStartTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                        isSelectingStart = false
                    } else {
                        tempEndTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                        onConfirm(tempStartTime, tempEndTime)
                    }
                }
            ) {
                Text(
                    if (isSelectingStart) "Siguiente" else "Guardar",
                    color = Green49
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Gray808)
            }
        },
        title = {
            Text(
                text = if (isSelectingStart) "Hora de inicio" else "Hora de fin",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Gray222
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSelectingStart) {
                    Text(
                        text = "Desde qué hora deseas recibir notificaciones",
                        fontSize = 14.sp,
                        color = Gray808,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                } else {
                    Text(
                        text = "Hasta qué hora deseas recibir todas las notificaciones",
                        fontSize = 14.sp,
                        color = Gray808,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = GrayF5,
                        clockDialSelectedContentColor = White,
                        clockDialUnselectedContentColor = Gray222,
                        selectorColor = Green49,
                        containerColor = White,
                        timeSelectorSelectedContainerColor = Green49,
                        timeSelectorUnselectedContainerColor = GrayF5,
                        timeSelectorSelectedContentColor = White,
                        timeSelectorUnselectedContentColor = Gray222
                    )
                )
            }
        },
        containerColor = White
    )
}