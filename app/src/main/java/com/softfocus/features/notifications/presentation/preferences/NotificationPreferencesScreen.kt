package com.softfocus.features.notifications.presentation.preferences

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.softfocus.features.notifications.domain.models.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationPreferencesScreen(
    onNavigateBack: () -> Unit,
    viewModel: NotificationPreferencesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preferencias de notificaciones") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showResetDialog = true }) {
                        Icon(Icons.Default.Refresh, "Restaurar")
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading && state.preferences.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
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
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = state.error!!,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { viewModel.loadPreferences() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "Configura qué notificaciones deseas recibir y cómo",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    items(state.preferences) { preference ->
                        NotificationPreferenceCard(
                            preference = preference,
                            onToggle = { viewModel.togglePreference(preference) },
                            onDeliveryMethodChange = { method ->
                                viewModel.updateDeliveryMethod(preference, method)
                            },
                            onScheduleChange = { schedule ->
                                viewModel.updateSchedule(preference, schedule)
                            }
                        )
                    }

                    if (state.isSaving) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    if (state.successMessage != null) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(state.successMessage!!)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Restaurar configuración") },
            text = { Text("¿Deseas restaurar todas las preferencias a sus valores por defecto?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetToDefaults()
                        showResetDialog = false
                    }
                ) {
                    Text("Restaurar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}


@Composable
private fun NotificationPreferenceCard(
    preference: NotificationPreference,
    onToggle: () -> Unit,
    onDeliveryMethodChange: (DeliveryMethod) -> Unit,
    onScheduleChange: (NotificationSchedule?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = getNotificationTypeLabel(preference.notificationType),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = getNotificationTypeDescription(preference.notificationType),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = preference.isEnabled,
                    onCheckedChange = { onToggle() }
                )
            }

            // Expanded settings
            if (preference.isEnabled && expanded) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Delivery method
                Text(
                    text = "Método de entrega",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                DeliveryMethodSelector(
                    selected = preference.deliveryMethod,
                    onSelect = onDeliveryMethodChange
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Schedule (only for non-critical notifications)
                if (preference.notificationType != NotificationType.CRISIS_ALERT) {
                    Text(
                        text = "Horario",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    ScheduleSelector(
                        schedule = preference.schedule,
                        onScheduleChange = onScheduleChange
                    )
                }
            }

            // Toggle expand
            if (preference.isEnabled) {
                TextButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(if (expanded) "Ocultar opciones" else "Más opciones")
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
private fun DeliveryMethodSelector(
    selected: DeliveryMethod,
    onSelect: (DeliveryMethod) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        DeliveryMethod.entries.filter { it != DeliveryMethod.NONE }.forEach { method ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                RadioButton(
                    selected = selected == method,
                    onClick = { onSelect(method) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = when (method) {
                            DeliveryMethod.PUSH -> "Notificación push"
                            DeliveryMethod.EMAIL -> "Correo electrónico"
                            DeliveryMethod.BOTH -> "Ambos"
                            DeliveryMethod.NONE -> "Ninguno"
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = when (method) {
                            DeliveryMethod.PUSH -> "En la app"
                            DeliveryMethod.EMAIL -> "A tu correo"
                            DeliveryMethod.BOTH -> "Push y email"
                            DeliveryMethod.NONE -> ""
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}


@Composable
private fun ScheduleSelector(
    schedule: NotificationSchedule?,
    onScheduleChange: (NotificationSchedule?) -> Unit
) {
    var hasSchedule by remember(schedule) { mutableStateOf(schedule != null) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Checkbox(
            checked = hasSchedule,
            onCheckedChange = { enabled ->
                hasSchedule = enabled
                if (!enabled) {
                    onScheduleChange(null)
                } else {
                    onScheduleChange(
                        NotificationSchedule(
                            startTime = java.time.LocalTime.of(8, 0),
                            endTime = java.time.LocalTime.of(22, 0),
                            daysOfWeek = listOf(1, 2, 3, 4, 5, 6, 7)
                        )
                    )
                }
            }
        )
        Text("Programar horario de notificaciones")
    }

    if (hasSchedule && schedule != null) {
        Column(
            modifier = Modifier.padding(start = 40.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "De ${schedule.startTime} a ${schedule.endTime}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Días: ${schedule.daysOfWeek.joinToString(", ") { getDayName(it) }}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun getNotificationTypeLabel(type: NotificationType) = when (type) {
    NotificationType.INFO -> "Notificaciones informativas"
    NotificationType.ALERT -> "Alertas importantes"
    NotificationType.WARNING -> "Advertencias"
    NotificationType.EMERGENCY -> "Emergencias"
    NotificationType.CHECKIN_REMINDER -> "Recordatorios de registro"
    NotificationType.CRISIS_ALERT -> "Alertas de crisis"
    NotificationType.MESSAGE_RECEIVED -> "Mensajes recibidos"
    NotificationType.ASSIGNMENT_DUE -> "Tareas pendientes"
    NotificationType.APPOINTMENT_REMINDER -> "Recordatorios de citas"
    NotificationType.SYSTEM_UPDATE -> "Actualizaciones del sistema"
}

private fun getNotificationTypeDescription(type: NotificationType) = when (type) {
    NotificationType.INFO -> "Información general y actualizaciones"
    NotificationType.ALERT -> "Alertas que requieren tu atención"
    NotificationType.WARNING -> "Advertencias sobre situaciones importantes"
    NotificationType.EMERGENCY -> "Notificaciones de emergencia crítica"
    NotificationType.CHECKIN_REMINDER -> "Te recuerda registrar tu estado de ánimo"
    NotificationType.CRISIS_ALERT -> "Notificaciones críticas que requieren atención inmediata"
    NotificationType.MESSAGE_RECEIVED -> "Cuando recibes un nuevo mensaje"
    NotificationType.ASSIGNMENT_DUE -> "Cuando se acerca la fecha límite de una tarea"
    NotificationType.APPOINTMENT_REMINDER -> "Te recuerda tus citas programadas"
    NotificationType.SYSTEM_UPDATE -> "Noticias y actualizaciones de la plataforma"
}

private fun getDayName(day: Int) = when (day) {
    1 -> "Lun"
    2 -> "Mar"
    3 -> "Mié"
    4 -> "Jue"
    5 -> "Vie"
    6 -> "Sáb"
    7 -> "Dom"
    else -> ""
}