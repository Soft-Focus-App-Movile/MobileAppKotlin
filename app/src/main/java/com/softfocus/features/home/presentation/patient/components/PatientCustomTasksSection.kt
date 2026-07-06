package com.softfocus.features.home.presentation.patient.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.R
import com.softfocus.features.home.presentation.patient.CustomTasksUiState
import com.softfocus.features.therapy.domain.models.PatientTask
import com.softfocus.ui.theme.Black
import com.softfocus.ui.theme.CrimsonMixed
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Green65
import com.softfocus.ui.theme.SourceSansRegular
import com.softfocus.ui.theme.White
import com.softfocus.ui.theme.YellowCB9D

/**
 * Muestra en el Inicio del paciente las tareas/propósitos de texto libre que su
 * psicólogo le asignó. Se oculta si no hay ninguna (para no saturar la pantalla).
 * El paciente puede marcarlas como completadas.
 */
@Composable
fun PatientCustomTasksSection(
    state: CustomTasksUiState,
    onCompleteTask: (String) -> Unit = {}
) {
    val tasks = (state as? CustomTasksUiState.Success)?.tasks ?: emptyList()
    if (tasks.isEmpty()) return

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Tareas de tu psicólogo",
            style = CrimsonSemiBold.copy(fontSize = 20.sp),
            color = Green65,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .background(color = White),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(4.dp, YellowCB9D),
            colors = CardDefaults.cardColors(containerColor = White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.tasks_icon),
                        contentDescription = null,
                        tint = Green65,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Propósitos que te asignó tu psicólogo",
                        style = CrimsonMixed,
                        fontSize = 16.sp,
                        color = Green65
                    )
                }

                tasks.forEachIndexed { index, task ->
                    CustomTaskItem(
                        task = task,
                        onComplete = { onCompleteTask(task.id) }
                    )
                    if (index < tasks.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomTaskItem(
    task: PatientTask,
    onComplete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { if (!task.isCompleted) onComplete() },
            enabled = !task.isCompleted,
            colors = CheckboxDefaults.colors(
                checkedColor = Green65,
                uncheckedColor = Green65
            )
        )
        Spacer(modifier = Modifier.width(4.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = task.title,
                style = SourceSansRegular.copy(fontSize = 14.sp),
                color = Black,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
            )
            if (task.description.isNotBlank()) {
                Text(
                    text = task.description,
                    style = SourceSansRegular.copy(fontSize = 12.sp),
                    color = Color.Gray
                )
            }
        }
    }
}
