package com.softfocus.features.therapy.presentation.psychologist.patiendetail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.WbSunny
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
import com.softfocus.features.therapy.presentation.psychologist.patiendetail.cardBackground
import com.softfocus.features.therapy.presentation.psychologist.patiendetail.lightGrayText
import com.softfocus.features.therapy.presentation.psychologist.patiendetail.primaryGreen
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.SourceSansRegular
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCard(
    assignment: Assignment
) { // <-- Acepta el objeto Tarea

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
                imageVector = getIconForContentType(assignment.content.type),
                contentDescription = assignment.content.title, // Descripción para accesibilidad
                tint = primaryGreen,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = assignment.content.title,
                    style = CrimsonSemiBold.copy(fontSize = 18.sp),
                )
                Text(
                    text = if (assignment.isCompleted) "Completada" else "Pendiente",
                    style = SourceSansRegular.copy(fontSize = 11.sp),
                    color =
                        if (assignment.isCompleted) primaryGreen
                        else lightGrayText
                )
                Text(
                    text = formatAssignmentDate(assignment.createdAt),
                    style = SourceSansRegular.copy(fontSize = 13.sp),
                    color = lightGrayText
                )
            }
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
        ContentType.Weather -> Icons.Default.WbSunny
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