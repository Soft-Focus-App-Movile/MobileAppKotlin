package com.softfocus.features.notifications.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.softfocus.features.notifications.domain.models.*
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.SourceSansRegular
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onNavigateBack: () -> Unit,
    //onNavigateToSettings: () -> Unit,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Notificaciones",
                        style = CrimsonSemiBold,
                        fontSize = 24.sp,
                        color = Green49
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            "Volver",
                            tint = Green49
                        )
                    }
                },
                actions = {
                    // Botón de Refresh - NUEVO
                    IconButton(
                        onClick = { viewModel.refreshNotifications() },
                        enabled = !state.isRefreshing && !state.isLoading
                    ) {
                        if (state.isRefreshing) {
                            CircularProgressIndicator(
                                color = Green49,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Icon(
                                Icons.Default.Refresh,
                                "Actualizar",
                                tint = Green49
                            )
                        }
                    }

                    if (state.notifications.any { it.status != DeliveryStatus.READ }) {
                        TextButton(onClick = { viewModel.markAllAsRead() }) {
                            Text(
                                "Marcar todas",
                                style = SourceSansRegular,
                                color = Green49
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            // Indicador de refresh - NUEVO
            if (state.isRefreshing) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LinearProgressIndicator(
                        color = Green49,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Tabs más centrados y con ancho limitado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    // Tab "Todas"
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            selectedTab = 0
                            viewModel.filterNotifications(null)
                        }
                    ) {
                        Text(
                            "Todas",
                            style = SourceSansRegular,
                            fontSize = 16.sp,
                            color = if (selectedTab == 0) Green49 else Color.Gray,
                            fontWeight = if (selectedTab == 0) FontWeight.SemiBold else FontWeight.Normal
                        )
                        if (selectedTab == 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(3.dp)
                                    .background(Green49)
                            )
                        }
                    }

                    // Tab "No leídas"
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            selectedTab = 1
                            viewModel.filterNotifications(DeliveryStatus.DELIVERED)
                        }
                    ) {
                        Text(
                            "No leídas",
                            style = SourceSansRegular,
                            fontSize = 16.sp,
                            color = if (selectedTab == 1) Green49 else Color.Gray,
                            fontWeight = if (selectedTab == 1) FontWeight.SemiBold else FontWeight.Normal
                        )
                        if (selectedTab == 1) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(3.dp)
                                    .background(Green49)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFE0E0E0))

            // Content
            when {
                state.isLoading && state.notifications.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Green49)
                    }
                }
                state.error != null -> {
                    ErrorView(
                        error = state.error!!,
                        onRetry = { viewModel.loadNotifications() }
                    )
                }
                state.notifications.isEmpty() -> {
                    EmptyNotificationsView()
                }
                else -> {
                    NotificationsList(
                        notifications = state.notifications,
                        onNotificationClick = { viewModel.markAsRead(it.id) },
                        onDeleteClick = { viewModel.deleteNotification(it.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationsList(
    notifications: List<Notification>,
    onNotificationClick: (Notification) -> Unit,
    onDeleteClick: (Notification) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(
            items = notifications,
            key = { it.id }
        ) { notification ->
            NotificationItem(
                notification = notification,
                onClick = { onNotificationClick(notification) },
                onDelete = { onDeleteClick(notification) }
            )
            HorizontalDivider(color = Color(0xFFE0E0E0))
        }
    }
}

@Composable
private fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = if (notification.readAt != null) {
            Color.White
        } else {
            Color(0xFFF5F9F5) // Verde muy tenue para no leídas
        }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(getNotificationColor(notification.type).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getNotificationIcon(notification.type),
                    contentDescription = null,
                    tint = getNotificationColor(notification.type),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = notification.title,
                        style = SourceSansRegular,
                        fontSize = 16.sp,
                        fontWeight = if (notification.status != DeliveryStatus.READ) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                        color = Color(0xFF2C2C2C)
                    )
                    Text(
                        text = formatTimeAgo(notification.createdAt),
                        style = SourceSansRegular,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.content,
                    style = SourceSansRegular,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (notification.priority == Priority.HIGH || notification.priority == Priority.CRITICAL) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Prioridad ${notification.priority.name.lowercase()}",
                        style = SourceSansRegular,
                        fontSize = 12.sp,
                        color = Color(0xFFE53935)
                    )
                }
            }

            // Delete button
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Eliminar",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "Eliminar notificación",
                    style = CrimsonSemiBold,
                    color = Green49
                )
            },
            text = {
                Text(
                    "¿Estás seguro de que deseas eliminar esta notificación?",
                    style = SourceSansRegular
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text(
                        "Eliminar",
                        style = SourceSansRegular,
                        color = Color(0xFFE53935)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(
                        "Cancelar",
                        style = SourceSansRegular,
                        color = Green49
                    )
                }
            }
        )
    }
}

@Composable
private fun EmptyNotificationsView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color.Gray.copy(alpha = 0.3f)
            )
            Text(
                text = "No hay notificaciones",
                style = SourceSansRegular,
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun ErrorView(error: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = error,
                style = SourceSansRegular,
                fontSize = 16.sp,
                color = Color(0xFFE53935)
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green49
                )
            ) {
                Text(
                    "Reintentar",
                    style = SourceSansRegular,
                    color = Color.White
                )
            }
        }
    }
}

private fun getNotificationIcon(type: NotificationType) = when (type) {
    NotificationType.INFO -> Icons.Default.Info
    NotificationType.ALERT -> Icons.Default.Warning
    NotificationType.WARNING -> Icons.Default.Warning
    NotificationType.EMERGENCY -> Icons.Default.Error
    NotificationType.CHECKIN_REMINDER -> Icons.Default.CheckCircle
    NotificationType.CRISIS_ALERT -> Icons.Default.Warning
    NotificationType.MESSAGE_RECEIVED -> Icons.Default.Email
    NotificationType.ASSIGNMENT_DUE -> Icons.Default.Assignment
    NotificationType.APPOINTMENT_REMINDER -> Icons.Default.Event
    NotificationType.SYSTEM_UPDATE -> Icons.Default.Info
}

private fun getNotificationColor(type: NotificationType) = when (type) {
    NotificationType.INFO -> Color(0xFF1E88E5)
    NotificationType.ALERT -> Color(0xFFFB8C00)
    NotificationType.WARNING -> Color(0xFFF57C00)
    NotificationType.EMERGENCY -> Color(0xFFE53935)
    NotificationType.CHECKIN_REMINDER -> Green49
    NotificationType.CRISIS_ALERT -> Color(0xFFE53935)
    NotificationType.MESSAGE_RECEIVED -> Color(0xFF1E88E5)
    NotificationType.ASSIGNMENT_DUE -> Color(0xFFFB8C00)
    NotificationType.APPOINTMENT_REMINDER -> Color(0xFF8E24AA)
    NotificationType.SYSTEM_UPDATE -> Color(0xFF546E7A)
}

private fun formatTimeAgo(dateTime: java.time.LocalDateTime): String {
    val now = java.time.LocalDateTime.now()
    val days = ChronoUnit.DAYS.between(dateTime, now)

    return when {
        days == 0L -> {
            val hours = ChronoUnit.HOURS.between(dateTime, now)
            when {
                hours == 0L -> {
                    val minutes = ChronoUnit.MINUTES.between(dateTime, now)
                    if (minutes <= 1) "Ahora" else "$minutes min"
                }
                hours == 1L -> "1 hora"
                else -> "$hours horas"
            }
        }
        days == 1L -> "Ayer"
        days < 7 -> "$days días"
        else -> dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }
}