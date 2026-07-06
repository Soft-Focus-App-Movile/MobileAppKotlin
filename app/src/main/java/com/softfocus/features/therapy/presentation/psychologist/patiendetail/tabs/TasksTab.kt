package com.softfocus.features.therapy.presentation.psychologist.patiendetail.tabs

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.R
import com.softfocus.features.library.assignments.presentation.AssignmentsUiState
import com.softfocus.features.library.domain.models.Assignment
import com.softfocus.features.therapy.domain.models.PatientTask
import com.softfocus.features.therapy.presentation.psychologist.patiendetail.CustomTasksState
import com.softfocus.features.therapy.presentation.psychologist.patiendetail.components.PatientTaskCard
import com.softfocus.features.therapy.presentation.psychologist.patiendetail.components.TaskCard
import com.softfocus.ui.theme.GrayD9
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.Green65
import com.softfocus.ui.theme.SourceSansRegular
import com.softfocus.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksTab(
    tasksState: AssignmentsUiState,
    customTasksState: CustomTasksState,
    onCreateTask: (String, String, (Boolean, String?) -> Unit) -> Unit
)
{
    val context = LocalContext.current

    // --- Variables de estado para tu filtro ---
    var isFilterMenuExpanded by remember { mutableStateOf(false) }
    val filterOptions = listOf("Todos", "Completados", "Pendientes")
    var selectedFilterOption by remember { mutableStateOf(filterOptions[0]) }

    // Estado del diálogo para crear una tarea personalizada
    var showAddDialog by remember { mutableStateOf(false) }

    // Usamos un Column para poner el filtro arriba y la lista debajo
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp) // Padding lateral para todo el contenido del tab
    ) {

        Spacer(modifier = Modifier.height(20.dp)) // Espacio superior

        // --- INICIO: CÓDIGO DE FILTRO QUE QUERÍAS MANTENER ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                Row(
                    modifier = Modifier
                        .clickable { isFilterMenuExpanded = true } // Al hacer clic, expande el menú
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // El texto de la opción seleccionada
                    Text(selectedFilterOption, color = Color.Black, fontSize = 14.sp)

                    // El icono de flecha que cambia
                    Icon(
                        imageVector = if (isFilterMenuExpanded) {
                            Icons.Filled.ArrowDropUp // Flecha arriba si está expandido
                        } else {
                            Icons.Filled.ArrowDropDown // Flecha abajo si está contraído
                        },
                        contentDescription = "Abrir filtro",
                        tint = GrayD9
                    )
                }

                // 5. Este es el menú desplegable que aparece y desaparece
                DropdownMenu(
                    expanded = isFilterMenuExpanded,
                    onDismissRequest = { isFilterMenuExpanded = false } // Para cerrar si se toca fuera
                ) {
                    filterOptions.forEach { option ->
                        DropdownMenuItem(
                            onClick = {
                                selectedFilterOption = option // Actualiza la opción seleccionada
                                isFilterMenuExpanded = false // Cierra el menú
                            },
                            text = {Text(option, style = SourceSansRegular.copy(fontSize = 14.sp), )},
                            modifier = if (selectedFilterOption == option) {
                                Modifier.background(Color.LightGray.copy(alpha = 0.3f))
                            } else {
                                Modifier.background(White)
                            }
                        )
                    }
                }
            } // Fin del Box del Dropdown

            Spacer(modifier = Modifier.width(4.dp))

            // --- Icono de Filtro ---
            IconButton(
                onClick = {

                },
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        color = Green49,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_filter),
                    contentDescription = "Filtrar",
                    tint = Color.White,
                    modifier=Modifier.size(15.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // --- Botón "+" para agregar una tarea personalizada (mismo estilo que el filtro) ---
            IconButton(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        color = Green49,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Agregar tarea",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp)) // Espacio entre el filtro y la lista

        // --- Tareas personalizadas (texto libre) ---
        CustomTasksSection(
            state = customTasksState,
            filterOption = selectedFilterOption
        )

        // --- Lógica de Carga de Tareas de Biblioteca (Loading, Error, Empty, Data) ---
        when (tasksState) {
            is AssignmentsUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is AssignmentsUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Error: ${tasksState.message}",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }

            is AssignmentsUiState.Success -> {
                val filteredAssignments = filterAssignments(
                    assignments = tasksState.assignments,
                    option = selectedFilterOption
                )
                if (filteredAssignments.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    PatientTasksList(filteredAssignments)
                }
            }
        }
    }

    // --- Diálogo para escribir y asignar una tarea personalizada ---
    if (showAddDialog) {
        AddCustomTaskDialog(
            isCreating = customTasksState.isCreating,
            onDismiss = { showAddDialog = false },
            onConfirm = { title, description ->
                onCreateTask(title, description) { success, error ->
                    if (success) {
                        showAddDialog = false
                        Toast.makeText(context, "Tarea asignada", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            context,
                            error ?: "No se pudo asignar la tarea",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        )
    }
}

/**
 * Sección que muestra las tareas de texto libre asignadas por el psicólogo.
 */
@Composable
private fun CustomTasksSection(
    state: CustomTasksState,
    filterOption: String
) {
    when {
        state.isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        state.error != null -> {
            Text(
                text = "Error: ${state.error}",
                color = MaterialTheme.colorScheme.error,
                style = SourceSansRegular.copy(fontSize = 14.sp),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        else -> {
            val filtered = filterCustomTasks(state.tasks, filterOption)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                filtered.forEach { task ->
                    PatientTaskCard(task = task)
                }
            }
        }
    }
}

/**
 * Diálogo con dos campos (título y descripción) para redactar la tarea.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCustomTaskDialog(
    isCreating: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { if (!isCreating) onDismiss() },
        title = {
            Text(
                text = "Nueva tarea",
                style = SourceSansRegular.copy(fontSize = 18.sp),
                color = Green65
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título / propósito") },
                    singleLine = true,
                    enabled = !isCreating,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción (opcional)") },
                    enabled = !isCreating,
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title.trim(), description.trim()) },
                enabled = title.isNotBlank() && !isCreating,
                colors = ButtonDefaults.buttonColors(containerColor = Green49)
            ) {
                if (isCreating) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Asignar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isCreating) {
                Text("Cancelar", color = Green65)
            }
        }
    )
}

private fun filterCustomTasks(
    tasks: List<PatientTask>,
    option: String
): List<PatientTask> {
    return when (option) {
        "Completados" -> tasks.filter { it.isCompleted }
        "Pendientes" -> tasks.filter { !it.isCompleted }
        else -> tasks
    }
}

private fun filterAssignments(
    assignments: List<Assignment>,
    option: String
): List<Assignment> {
    return when (option) {
        "Completados" -> assignments.filter { it.isCompleted }
        "Pendientes" -> assignments.filter { !it.isCompleted }
        else -> assignments // "Todos"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientTasksList(
    assignments: List<Assignment>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        assignments.forEach { assignment ->
            TaskCard(
                assignment = assignment
            )
        }
    }
}
