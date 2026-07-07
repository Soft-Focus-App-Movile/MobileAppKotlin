package com.softfocus.features.home.presentation.patient.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.R
import com.softfocus.features.home.presentation.patient.CustomTasksUiState
import com.softfocus.features.library.assignments.presentation.AssignmentsUiState
import com.softfocus.features.library.domain.models.Assignment
import com.softfocus.features.library.domain.models.ContentType
import com.softfocus.features.therapy.domain.models.PatientTask
import com.softfocus.ui.theme.Black
import com.softfocus.ui.theme.CrimsonMixed
import com.softfocus.ui.theme.Green29
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.Green65
import com.softfocus.ui.theme.SourceSansRegular
import com.softfocus.ui.theme.White
import com.softfocus.ui.theme.YellowCB9D

/**
 * Sección unificada de tareas del paciente en el Inicio: muestra en UNA sola tarjeta y
 * UNA sola lista tanto las tareas de biblioteca (asignaciones) como los propósitos de
 * texto libre que le asignó su psicólogo. El encabezado cuenta TODAS las pendientes.
 */
@Composable
fun PatientTasksSection(
    assignmentsState: AssignmentsUiState,
    customTasksState: CustomTasksUiState,
    onTaskClick: (Assignment) -> Unit = {},
    onCompleteTask: (String) -> Unit = {},
    onCompleteAssignment: (String) -> Unit = {},
    onRetry: () -> Unit = {}
) {
    val customTasks = (customTasksState as? CustomTasksUiState.Success)?.tasks ?: emptyList()
    val pendingCustom = customTasks.filter { !it.isCompleted }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = YellowCB9D)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            when (val state = assignmentsState) {
                is AssignmentsUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Green65,
                            strokeWidth = 2.dp
                        )
                    }
                }

                is AssignmentsUiState.Error -> {
                    Text(
                        text = "Error al cargar tareas",
                        style = CrimsonMixed,
                        fontSize = 16.sp,
                        color = Green65
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.message,
                        style = SourceSansRegular,
                        fontSize = 14.sp,
                        color = Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onRetry) {
                        Text("Reintentar", color = Green65)
                    }
                    // Aunque la biblioteca falle, seguimos mostrando los propósitos del psicólogo
                    if (pendingCustom.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        pendingCustom.forEachIndexed { index, task ->
                            CustomTaskItem(task = task, onComplete = { onCompleteTask(task.id) })
                            if (index < pendingCustom.size - 1) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }

                is AssignmentsUiState.Success -> {
                    val pendingAssignments = state.assignments.filter { !it.isCompleted }
                    val total = pendingAssignments.size + pendingCustom.size

                    if (total == 0) {
                        // Distinguir "nunca tuvo tareas" de "las completó todas"
                        val hasEverHadTasks = state.assignments.isNotEmpty() || customTasks.isNotEmpty()
                        TaskSectionHeader(text = "No tienes tareas pendientes")
                        Text(
                            text = if (hasEverHadTasks)
                                "¡Excelente! Has completado todas tus tareas"
                            else
                                "Aún no tienes tareas asignadas",
                            style = SourceSansRegular,
                            fontSize = 14.sp,
                            color = Black
                        )
                    } else {
                        TaskSectionHeader(
                            text = "Tienes $total ${if (total == 1) "tarea" else "tareas"} por completar"
                        )
                        // Una sola lista: primero biblioteca, luego propósitos. Mismo estilo de píldora.
                        pendingAssignments.forEachIndexed { index, assignment ->
                            TaskItem(
                                assignment = assignment,
                                onClick = { onTaskClick(assignment) },
                                onComplete = { onCompleteAssignment(assignment.id) }
                            )
                            if (index < pendingAssignments.size - 1 || pendingCustom.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                        pendingCustom.forEachIndexed { index, task ->
                            CustomTaskItem(task = task, onComplete = { onCompleteTask(task.id) })
                            if (index < pendingCustom.size - 1) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Etiqueta en español para el tipo de contenido de la biblioteca.
 */
private fun contentTypeLabel(type: ContentType): String = when (type) {
    ContentType.Movie -> "Película"
    ContentType.Music -> "Música"
    ContentType.Video -> "Video"
}

@Composable
private fun TaskSectionHeader(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 12.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.tasks_icon),
            contentDescription = null,
            tint = Green29,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = CrimsonMixed,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = White
        )
    }
}

@Composable
fun TaskItem(
    assignment: Assignment,
    onClick: () -> Unit = {},
    onComplete: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox (marcador verde). Al tocarlo se marca la asignación como completada.
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .border(2.dp, Green49, RoundedCornerShape(4.dp))
                    .clickable(onClick = onComplete)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "${contentTypeLabel(assignment.content.type)}: ${assignment.content.title}",
                style = SourceSansRegular.copy(
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                ),
                color = Black
            )
        }
    }
}
