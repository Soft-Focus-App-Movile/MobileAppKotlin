package com.softfocus.features.notifications.presentation.list

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.softfocus.features.notifications.domain.models.*
import com.softfocus.ui.theme.AppColors
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.SourceSansRegular
import kotlinx.coroutines.delay
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onNavigateBack: () -> Unit,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    // Auto-refresh cada 5 segundos
    LaunchedEffect(Unit) {
        while (true) {
            delay(5_000L)
            viewModel.refreshNotifications()
        }
    }

    Scaffold(
        containerColor = AppColors.background,
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
                            Icons.AutoMirrored.Filled.ArrowBack,
                            "Volver",
                            tint = Green49
                        )
                    }
                },
                actions = {
                    if (state.isRefreshing) {
                        CircularProgressIndicator(
                            color = Green49,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(20.dp),
                            strokeWidth = 2.dp
                        )
                    }

                    IconButton(
                        onClick = { viewModel.refreshNotifications() },
                        enabled = !state.isRefreshing && !state.isLoading
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            "Actualizar",
                            tint = if (state.isRefreshing) Green49.copy(alpha = 0.4f) else Green49
                        )
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
                    containerColor = AppColors.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(AppColors.background)
        ) {
            if (state.isRefreshing) {
                LinearProgressIndicator(
                    color = Green49,
                    trackColor = Green49.copy(alpha = 0.1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(2.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
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
                            color = if (selectedTab == 0) Green49 else AppColors.textSecondary,
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

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            selectedTab = 1
                            viewModel.filterNotifications(DeliveryStatus.DELIVERED)
                        }
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "No leídas",
                                style = SourceSansRegular,
                                fontSize = 16.sp,
                                color = if (selectedTab == 1) Green49 else AppColors.textSecondary,
                                fontWeight = if (selectedTab == 1) FontWeight.SemiBold else FontWeight.Normal
                            )
                            val unreadCount = state.notifications.count { it.status != DeliveryStatus.READ }
                            if (unreadCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE53935)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (unreadCount > 99) "99+" else unreadCount.toString(),
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
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

            HorizontalDivider(color = AppColors.outline)

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
                    // Si no hay notificaciones Y están desactivadas, mostrar mensaje especial
                    if (!state.notificationsEnabled) {
                        NotificationsDisabledView()
                    } else {
                        EmptyNotificationsView()
                    }
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
            HorizontalDivider(color = AppColors.outline)
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
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                isExpanded = !isExpanded
                if (!isExpanded) {
                    onClick()
                }
            },
        color = if (notification.readAt != null) {
            AppColors.surface
        } else {
            AppColors.surfaceVariant
        }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .animateContentSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
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
                            color = AppColors.textPrimary,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        if (notification.priority == Priority.HIGH || notification.priority == Priority.CRITICAL) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = Color(0xFFE53935).copy(alpha = 0.15f),
                                        shape = MaterialTheme.shapes.extraSmall
                                    )
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (notification.priority == Priority.CRITICAL) "!" else "Alta",
                                    style = SourceSansRegular,
                                    fontSize = 9.sp,
                                    color = Color(0xFFE53935),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = formatTimeAgo(notification.createdAt),
                        style = SourceSansRegular,
                        fontSize = 12.sp,
                        color = AppColors.textSecondary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = notification.content,
                        style = SourceSansRegular,
                        fontSize = 14.sp,
                        color = AppColors.textSecondary,
                        maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                        overflow = if (isExpanded) TextOverflow.Visible else TextOverflow.Ellipsis,
                        lineHeight = 20.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                    )

                    if (!isExpanded && notification.content.length > 120) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Ver más",
                            style = SourceSansRegular,
                            fontSize = 12.sp,
                            color = Green49,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (isExpanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(
                        color = AppColors.outline,
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Text(
                        text = "Recibida el ${notification.createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm"))}",
                        style = SourceSansRegular,
                        fontSize = 12.sp,
                        color = AppColors.textSecondary.copy(alpha = 0.7f)
                    )
                }
            }

            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Eliminar",
                    tint = AppColors.textSecondary,
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
                    style = SourceSansRegular,
                    lineHeight = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp)
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
private fun NotificationsDisabledView() {
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
                imageVector = Icons.Default.NotificationsOff,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = AppColors.textSecondary.copy(alpha = 0.3f)
            )
            Text(
                text = "Notificaciones desactivadas",
                style = SourceSansRegular,
                fontSize = 16.sp,
                color = AppColors.textSecondary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Activa las notificaciones en configuración para ver tus nuevos mensajes",
                style = SourceSansRegular,
                fontSize = 14.sp,
                color = AppColors.textSecondary.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )
        }
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
                tint = AppColors.textSecondary.copy(alpha = 0.3f)
            )
            Text(
                text = "No hay notificaciones",
                style = SourceSansRegular,
                fontSize = 16.sp,
                color = AppColors.textSecondary
            )
            Text(
                text = "Cuando recibas notificaciones aparecerán aquí",
                style = SourceSansRegular,
                fontSize = 14.sp,
                color = AppColors.textSecondary.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
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
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = Color(0xFFE53935).copy(alpha = 0.5f)
            )
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