package com.softfocus.features.home.presentation.patient.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.R
import com.softfocus.features.library.assignments.presentation.AssignmentsUiState
import com.softfocus.features.library.domain.models.Assignment
import com.softfocus.ui.theme.Black
import com.softfocus.ui.theme.CrimsonMixed
import com.softfocus.ui.theme.Green65
import com.softfocus.ui.theme.SourceSansRegular
import com.softfocus.ui.theme.White
import com.softfocus.ui.theme.YellowCB9D

@Composable
fun TasksSection(
    assignmentsState: AssignmentsUiState,
    onTaskClick: (Assignment) -> Unit = {},
    onRetry: () -> Unit = {}
) {

    when (val state = assignmentsState) {
        is AssignmentsUiState.Loading -> {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .background(color = White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(4.dp, YellowCB9D),
                colors = CardDefaults.cardColors(containerColor = White)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Green65,
                        strokeWidth = 2.dp
                    )
                }
            }
        }

        is AssignmentsUiState.Success -> {
            val pendingAssignments = state.assignments.filter { !it.isCompleted }

            if (pendingAssignments.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .background(color = White),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(4.dp, YellowCB9D),
                    colors = CardDefaults.cardColors(containerColor = White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
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
                                text = "No tienes tareas pendientes",
                                style = CrimsonMixed,
                                fontSize = 16.sp,
                                color = Green65
                            )
                        }
                        Text(
                            text = "¡Excelente! Has completado todas tus tareas",
                            style = SourceSansRegular,
                            fontSize = 14.sp,
                            color = Black
                        )
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .background(color = White),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(4.dp, YellowCB9D),
                    colors = CardDefaults.cardColors(containerColor = White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
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
                                text = "Tienes ${pendingAssignments.size} ${if (pendingAssignments.size == 1) "tarea" else "tareas"} por completar",
                                style = CrimsonMixed,
                                fontSize = 16.sp,
                                color = Green65
                            )
                        }

                        pendingAssignments.take(3).forEachIndexed { index, assignment ->
                            TaskItem(
                                assignment = assignment,
                                onClick = { onTaskClick(assignment) }
                            )
                            if (index < pendingAssignments.take(3).size - 1) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }

        is AssignmentsUiState.Error -> {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .background(color = White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(4.dp, YellowCB9D),
                colors = CardDefaults.cardColors(containerColor = White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
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
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    assignment: Assignment,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = YellowCB9D
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox placeholder (cuadrito sin check)
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(3.dp)
                    )
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = assignment.content.title,
                style = SourceSansRegular,
                fontSize = 14.sp,
                color = Black
            )
        }
    }
}
