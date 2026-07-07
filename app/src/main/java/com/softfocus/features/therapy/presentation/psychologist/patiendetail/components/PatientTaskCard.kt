package com.softfocus.features.therapy.presentation.psychologist.patiendetail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.features.therapy.domain.models.PatientTask
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Gray222
import com.softfocus.ui.theme.Gray89
import com.softfocus.ui.theme.Green65
import com.softfocus.ui.theme.GreenF2
import com.softfocus.ui.theme.SourceSansRegular
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Card para mostrar una tarea/propósito de texto libre asignada por el psicólogo.
 * Usa el mismo formato que [TaskCard] (ícono + título + info) para que todas las tareas
 * del paciente se vean iguales. El ícono de nota/lápiz distingue que es de texto libre.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientTaskCard(
    task: PatientTask,
    onComplete: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GreenF2)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.EditNote,
                contentDescription = "Tarea",
                tint = Green65,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = CrimsonSemiBold.copy(fontSize = 18.sp, lineHeight = 24.sp),
                    color = Gray222
                )

                if (task.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.description,
                        style = SourceSansRegular.copy(fontSize = 13.sp, lineHeight = 18.sp),
                        color = Gray89
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (task.isCompleted) "Completada" else "Pendiente",
                    style = SourceSansRegular.copy(fontSize = 11.sp),
                    color = if (task.isCompleted) Green65 else Gray89
                )

                formatAssignedDate(task.assignedAt)?.let { dateText ->
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = dateText,
                        style = SourceSansRegular.copy(fontSize = 13.sp),
                        color = Gray89
                    )
                }
            }

            // Checkbox de completar: solo en la vista del paciente (cuando se pasa onComplete)
            if (onComplete != null) {
                Spacer(modifier = Modifier.width(12.dp))
                TaskCompleteCheckbox(isCompleted = task.isCompleted, onComplete = onComplete)
            }
        }
    }
}

/** Formatea "Asignado el d de MMMM" a partir del ISO del backend; null si no se puede parsear. */
private fun formatAssignedDate(assignedAt: String?): String? {
    if (assignedAt.isNullOrBlank()) return null
    return try {
        val formatter = DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("es", "ES"))
        val dateTime = LocalDateTime.parse(assignedAt.substringBefore("Z").substringBefore("."))
        "Asignado el ${dateTime.format(formatter)}"
    } catch (e: Exception) {
        null
    }
}
