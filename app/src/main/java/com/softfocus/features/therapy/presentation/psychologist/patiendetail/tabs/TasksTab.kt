package com.softfocus.features.therapy.presentation.psychologist.patiendetail.tabs

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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.softfocus.features.therapy.presentation.psychologist.patiendetail.components.TaskCard
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.SourceSansRegular
import kotlin.collections.forEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksTab(
    tasksState: AssignmentsUiState
)
{
    val context = LocalContext.current

    // --- Variables de estado para tu filtro ---
    var isFilterMenuExpanded by remember { mutableStateOf(false) }
    val filterOptions = listOf("Todos", "Completados", "Pendientes")
    var selectedFilterOption by remember { mutableStateOf(filterOptions[0]) }

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
                    Text(selectedFilterOption, color = Color.Black, fontSize = 16.sp)

                    // El icono de flecha que cambia
                    Icon(
                        imageVector = if (isFilterMenuExpanded) {
                            Icons.Filled.ArrowDropUp // Flecha arriba si está expandido
                        } else {
                            Icons.Filled.ArrowDropDown // Flecha abajo si está contraído
                        },
                        contentDescription = "Abrir filtro",
                        tint = Color.Gray
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
                                // TODO: Aquí conectarías al ViewModel para filtrar la lista
                            },
                            text = {Text(option, style = SourceSansRegular.copy(fontSize = 11.sp), )},
                            modifier = if (selectedFilterOption == option) {
                                Modifier.background(Color.LightGray.copy(alpha = 0.3f))
                            } else {
                                Modifier
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
                        color = Green49, // Asumo que este color existe en tu Theme
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_filter), // Asumo que este drawable existe
                    contentDescription = "Filtrar",
                    tint = Color.White,
                    modifier=Modifier.size(15.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp)) // Espacio entre el filtro y la lista

        // --- Lógica de Carga de Tareas (Loading, Error, Empty, Data) ---
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
                if (tasksState.assignments.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Aún no hay tareas asignadas.")
                    }
                }

                else {
                    PatientTasksList(
                        filterAssignments(
                            assignments = tasksState.assignments,
                            option = selectedFilterOption
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
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
    val context = LocalContext.current
    val completedAssignments = assignments.filter { it.isCompleted }
    val pendingAssignments = assignments.filter { !it.isCompleted }

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