package com.softfocus.features.therapy.presentation.psychologist.patiendetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.features.library.domain.models.Assignment
import com.softfocus.features.library.domain.models.ContentType
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Gray222
import com.softfocus.ui.theme.Gray89
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.Green65
import com.softfocus.ui.theme.GreenF2
import com.softfocus.ui.theme.SourceSansRegular
import com.softfocus.ui.theme.White
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCard(
    assignment: Assignment,
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
                imageVector = getIconForContentType(assignment.content.type),
                contentDescription = assignment.content.title,
                tint = Green65,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 1. weight(1f) es CRÍTICO para que la columna sepa que debe respetar el ancho
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = assignment.content.title,
                    // 2. SOLUCIÓN AL SOLAPAMIENTO: Definimos lineHeight un poco mayor que fontSize
                    style = CrimsonSemiBold.copy(
                        fontSize = 18.sp,
                        lineHeight = 24.sp // <-- Esto separa las líneas verticalmente
                    ),
                    // Color fijo oscuro: la tarjeta es siempre verde claro (GreenF2), así que en
                    // modo oscuro el color por defecto se volvía blanco e ilegible.
                    color = Gray222
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (assignment.isCompleted) "Completada" else "Pendiente",
                    style = SourceSansRegular.copy(fontSize = 11.sp),
                    color = if (assignment.isCompleted) Green65 else Gray89
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = formatAssignmentDate(assignment.createdAt),
                    style = SourceSansRegular.copy(fontSize = 13.sp),
                    color = Gray89
                )
            }

            // Checkbox de completar: solo aparece cuando el paciente puede completar (onComplete != null)
            if (onComplete != null) {
                Spacer(modifier = Modifier.width(12.dp))
                TaskCompleteCheckbox(isCompleted = assignment.isCompleted, onComplete = onComplete)
            }
        }
    }
}

/**
 * Cuadrito verde para marcar una tarea como completada. Se usa en la vista del paciente
 * (en la del psicólogo no se pasa onComplete, así que no aparece).
 */
@Composable
internal fun TaskCompleteCheckbox(
    isCompleted: Boolean,
    onComplete: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .then(
                if (isCompleted) Modifier.background(Green49, RoundedCornerShape(4.dp))
                else Modifier.border(2.dp, Green49, RoundedCornerShape(4.dp))
            )
            .then(if (!isCompleted) Modifier.clickable(onClick = onComplete) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (isCompleted) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Completada",
                tint = White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun getIconForContentType(type: ContentType): ImageVector {
    return when (type) {
        ContentType.Movie -> Icons.Default.Movie
        ContentType.Music -> Icons.Default.MusicNote
        ContentType.Video -> Icons.Default.PlayCircleOutline
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun formatAssignmentDate(dateTime: LocalDateTime): String {
    val formatter = remember {
        // "d" para el día sin cero inicial, "MMMM" para el nombre completo del mes
        DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("es", "ES"))
    }
    return "Asignado el ${dateTime.format(formatter)}"
}
