package com.softfocus.features.crisis.presentation.psychologist.components

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.softfocus.R
import com.softfocus.core.utils.LocationHelper
import com.softfocus.ui.components.ProfileAvatar
import com.softfocus.features.crisis.domain.models.CrisisAlert
import com.softfocus.features.crisis.domain.models.EmotionalContext
import com.softfocus.features.crisis.domain.models.Location
import com.softfocus.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PatientCrisisCard(
    alert: CrisisAlert,
    onViewProfile: () -> Unit,
    onSendMessage: () -> Unit,
    onUpdateStatus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var formattedLocation by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(alert.location) {
        alert.location?.let { location ->
            scope.launch {
                formattedLocation = LocationHelper.getCityAndCountry(
                    context,
                    android.location.Location("").apply {
                        latitude = location.latitude!!
                        longitude = location.longitude!!
                    }
                )
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GreenF2),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .border(2.dp, Green49, CircleShape)
                            .padding(2.dp)
                    ) {
                        ProfileAvatar(
                            imageUrl = alert.patientPhotoUrl,
                            fullName = alert.patientName,
                            size = 70.dp,
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = alert.patientName,
                            style = CrimsonBold,
                            fontSize = 18.sp,
                            color = Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = getTimeAgo(alert.createdAt),
                            fontSize = 12.sp,
                            color = Gray89
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        StatusBadge(status = alert.status)
                    }
                }

                SeverityBadge(severity = alert.severity)
            }

            Spacer(modifier = Modifier.height(12.dp))

            alert.emotionalContext?.lastDetectedEmotion?.let { emotion ->
                InfoRow(
                    icon = R.drawable.ic_emotion,
                    label = "Emoción detectada",
                    value = emotion
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            formattedLocation?.let { location ->
                InfoRow(
                    icon = R.drawable.ic_location_pin,
                    label = "Ubicación",
                    value = location
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            InfoRow(
                icon = R.drawable.ic_trigger,
                label = "Origen",
                value = formatTriggerSource(alert.triggerSource)
            )

            alert.triggerReason?.let { reason ->
                Spacer(modifier = Modifier.height(8.dp))


                Row(

                    modifier = Modifier.padding(start = 28.dp),

                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Filled.QuestionMark,
                        contentDescription = "Razón",
                        modifier = Modifier.size(16.dp),
                        tint = DarkGray
                    )

                    // 5. Un pequeño espacio entre el ícono y el texto
                    Spacer(modifier = Modifier.width(4.dp))

                    // 6. Tu texto
                    Text(
                        text = "Razón: $reason",
                        fontSize = 13.sp,
                        color = DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewProfile,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Green49
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Green49)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_profile_user),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Perfil", fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = onSendMessage,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Green49
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Green49)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_message),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Escribir", fontSize = 13.sp)
                }

                Button(
                    onClick = onUpdateStatus,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = getStatusColor(alert.status)
                    )
                ) {
                    Text(getNextStatusText(alert.status), fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun SeverityBadge(severity: String) {
    val (backgroundColor, textColor, text) = when (severity.uppercase()) {
        "CRITICAL" -> Triple(RedE8, Color.White, "Crisis")
        "HIGH" -> Triple(OrangeFB, Color.White, "Alta")
        "MODERATE" -> Triple(GreenAB, Color.Black, "Moderada")
        else -> Triple(Gray89, Color.Black, severity)
    }

    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StatusBadge(status: String) {
    val (backgroundColor, text) = when (status.uppercase()) {
        "PENDING" -> Pair(Pending, "Pendiente")
        "ATTENDED" -> Pair(Attended, "Atendido")
        "RESOLVED" -> Pair(Resolved, "Resuelto")
        "DISMISSED" -> Pair(Dismissed, "Descartado")
        else -> Pair(Gray89, status)
    }

    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Estado: $text",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}

@Composable
private fun InfoRow(icon: Int, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = Green49,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label: ",
            fontSize = 13.sp,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Black
        )
    }
}

private fun formatTriggerSource(source: String): String {
    return when (source.uppercase()) {
        "MANUAL_BUTTON" -> "Manual (Botón SOS)"
        "AI_CHAT" -> "Chat IA"
        "EMOTION_ANALYSIS" -> "Análisis de emoción"
        "CHECK_IN" -> "Check-in diario"
        else -> source
    }
}

private fun getNextStatusText(currentStatus: String): String {
    return when (currentStatus.uppercase()) {
        "PENDING" -> "Atender"
        "ATTENDED" -> "Resolver"
        "RESOLVED" -> "Marcar Pendiente"
        else -> "Actualizar"
    }
}

private fun getStatusColor(status: String): Color {
    return when (status.uppercase()) {
        "PENDING" -> PendingButton
        "ATTENDED" -> AttendedButton
        "RESOLVED" -> Green49
        else -> Green49
    }
}

private fun getTimeAgo(dateString: String): String {
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")
        val date = format.parse(dateString) ?: return dateString

        val now = Date()
        val diff = now.time - date.time

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        when {
            days > 0 -> "Hace ${days}d"
            hours > 0 -> "Hace ${hours}h"
            minutes > 0 -> "Hace ${minutes}m"
            else -> "Ahora"
        }
    } catch (e: Exception) {
        dateString
    }
}

@Preview(showBackground = true)
@Composable
fun PatientCrisisCardPreview() {
    val sampleAlert = CrisisAlert(
        id = "1",
        patientId = "patient123",
        patientName = "María González",
        patientPhotoUrl = null,
        psychologistId = "psych123",
        severity = "Moderate",
        status = "Attended",
        triggerSource = "Manual",
        triggerReason = "Ansiedad severa",
        location = Location(
            latitude = -12.0464,
            longitude = -77.0428,
            displayString = "Lima, Perú"
        ),
        emotionalContext = EmotionalContext(
            lastDetectedEmotion = "Ansiedad",
            lastEmotionDetectedAt = "2025-11-09T10:30:00.000Z",
            emotionSource = "AI_Chat"
        ),
        psychologistNotes = null,
        createdAt = "2025-11-09T10:30:00.000Z",
        attendedAt = null,
        resolvedAt = null
    )

    PatientCrisisCard(
        alert = sampleAlert,
        onViewProfile = {},
        onSendMessage = {},
        onUpdateStatus = {}
    )
}
