package com.softfocus.features.home.presentation.patient.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.features.therapy.domain.models.PatientTask
import com.softfocus.ui.theme.Black
import com.softfocus.ui.theme.Green65
import com.softfocus.ui.theme.SourceSansRegular
import com.softfocus.ui.theme.YellowCB9D

/**
 * Item de una tarea/propósito de texto libre asignada por el psicólogo. Usa la misma
 * píldora amarilla ([YellowCB9D]), el mismo cuadrito de marcado y la misma separación
 * que las tareas de biblioteca ([TaskItem]) para que todas se vean iguales dentro de
 * [PatientTasksSection]. El cuadrito es clickeable para marcar la tarea como completada.
 */
@Composable
fun CustomTaskItem(
    task: PatientTask,
    onComplete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = YellowCB9D
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cuadrito de marcado, igual que el de las tareas de biblioteca (arriba)
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(3.dp)
                    )
                    .then(
                        if (!task.isCompleted) Modifier.clickable(onClick = onComplete)
                        else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completada",
                        tint = Green65,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = task.title,
                    style = SourceSansRegular.copy(fontSize = 14.sp, lineHeight = 20.sp),
                    color = Black,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        style = SourceSansRegular.copy(fontSize = 12.sp, lineHeight = 18.sp),
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}
