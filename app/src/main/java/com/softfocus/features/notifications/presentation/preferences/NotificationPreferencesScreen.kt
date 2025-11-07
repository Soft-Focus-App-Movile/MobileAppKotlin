package com.softfocus.features.notifications.presentation.preferences

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.softfocus.features.notifications.domain.models.NotificationType
import com.softfocus.ui.theme.*
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationPreferencesScreen(
    onNavigateBack: () -> Unit,
    viewModel: NotificationPreferencesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showTimePickerDialog by remember { mutableStateOf(false) }

    // Extraer las preferencias específicas del estado
    val dailyCheckIn = remember(state.preferences) {
        state.preferences.find { it.notificationType == NotificationType.CHECKIN_REMINDER }
    }

    val dailySuggestions = remember(state.preferences) {
        state.preferences.find { it.notificationType == NotificationType.INFO }
    }

    val promotions = remember(state.preferences) {
        state.preferences.find { it.notificationType == NotificationType.SYSTEM_UPDATE }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notificaciones",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = Green49
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
            state.isLoading && state.preferences.isEmpty() -> {
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
                        // Card contenedor con título y preferencias
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
                                    .padding(20.dp)
                            ) {
                                // Título "Configuración"
                                Text(
                                    text = "Configuración",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Gray222,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )

                                // Preferencias
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Recordatorios de registro diario
                                    dailyCheckIn?.let { pref ->
                                        NotificationPreferenceItem(
                                            title = "Recordatorios de registro diario",
                                            subtitle = if (pref.isEnabled && pref.schedule != null) {
                                                pref.schedule.startTime.format(
                                                    DateTimeFormatter.ofPattern("HH:mm")
                                                )
                                            } else null,
                                            checked = pref.isEnabled,
                                            onCheckedChange = {
                                                viewModel.togglePreference(pref)
                                            },
                                            onItemClick = {
                                                if (pref.isEnabled) {
                                                    showTimePickerDialog = true
                                                }
                                            }
                                        )

                                        HorizontalDivider(color = GrayD9, thickness = 0.5.dp)
                                    }

                                    // Sugerencias diarias
                                    dailySuggestions?.let { pref ->
                                        NotificationPreferenceItem(
                                            title = "Sugerencias diarias",
                                            checked = pref.isEnabled,
                                            onCheckedChange = {
                                                viewModel.togglePreference(pref)
                                            }
                                        )

                                        HorizontalDivider(color = GrayD9, thickness = 0.5.dp)
                                    }

                                    // Promociones y novedades
                                    promotions?.let { pref ->
                                        NotificationPreferenceItem(
                                            title = "Promociones y novedades",
                                            checked = pref.isEnabled,
                                            onCheckedChange = {
                                                viewModel.togglePreference(pref)
                                            }
                                        )
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

                        // Mensaje de guardado exitoso
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

    // Diálogo de selector de hora
    if (showTimePickerDialog && dailyCheckIn != null) {
        val currentTime = dailyCheckIn.schedule?.startTime ?: LocalTime.of(9, 0)

        TimePickerDialog(
            onDismiss = { showTimePickerDialog = false },
            onConfirm = { hour, minute ->
                val newTime = LocalTime.of(hour, minute)
                viewModel.updateCheckInTime(dailyCheckIn, newTime)
                showTimePickerDialog = false
            },
            initialHour = currentTime.hour,
            initialMinute = currentTime.minute
        )
    }
}

@Composable
private fun NotificationPreferenceItem(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onItemClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = Gray222,
                fontWeight = FontWeight.Normal
            )
            subtitle?.let {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    color = Gray808,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = Green49,
                uncheckedColor = GrayB2,
                checkmarkColor = White
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    initialHour: Int = 9,
    initialMinute: Int = 0
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onConfirm(timePickerState.hour, timePickerState.minute)
            }) {
                Text("Aceptar", color = Green49)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Gray808)
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Seleccionar hora",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Gray222,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = GrayF5,
                        clockDialSelectedContentColor = White,
                        clockDialUnselectedContentColor = Gray222,
                        selectorColor = Green49,
                        containerColor = White,
                        periodSelectorBorderColor = GrayE0,
                        periodSelectorSelectedContainerColor = Green49,
                        periodSelectorUnselectedContainerColor = GrayF5,
                        periodSelectorSelectedContentColor = White,
                        periodSelectorUnselectedContentColor = Gray222,
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