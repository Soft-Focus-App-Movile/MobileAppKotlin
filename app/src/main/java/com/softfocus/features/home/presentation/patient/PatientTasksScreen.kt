package com.softfocus.features.home.presentation.patient

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.softfocus.features.home.presentation.patient.di.patientHomeViewModel
import com.softfocus.ui.components.navigation.PatientBottomNav
import com.softfocus.features.library.assignments.presentation.AssignmentsUiState
import com.softfocus.features.therapy.presentation.psychologist.patiendetail.components.PatientTaskCard
import com.softfocus.features.therapy.presentation.psychologist.patiendetail.components.TaskCard
import com.softfocus.ui.theme.AppColors
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Green49

/**
 * Pantalla "Tareas" del paciente: muestra sus tareas de biblioteca (asignaciones) y sus
 * propósitos de texto libre usando las MISMAS cards con las que el psicólogo ve las tareas
 * de sus pacientes ([TaskCard] y [PatientTaskCard]). Incluye un filtro Todos/Completados/Pendientes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientTasksScreen(
    onNavigateBack: () -> Unit,
    navController: NavController,
    viewModel: PatientHomeViewModel = patientHomeViewModel()
) {
    val assignmentsState by viewModel.assignmentsState.collectAsState()
    val customTasksState by viewModel.customTasksState.collectAsState()

    val customTasks = (customTasksState as? CustomTasksUiState.Success)?.tasks ?: emptyList()
    val assignments = (assignmentsState as? AssignmentsUiState.Success)?.assignments ?: emptyList()
    val isLoading = assignmentsState is AssignmentsUiState.Loading ||
        customTasksState is CustomTasksUiState.Loading

    val filterOptions = listOf("Todos", "Completados", "Pendientes")
    var selectedFilter by remember { mutableStateOf(filterOptions[0]) }

    val filteredCustom = when (selectedFilter) {
        "Completados" -> customTasks.filter { it.isCompleted }
        "Pendientes" -> customTasks.filter { !it.isCompleted }
        else -> customTasks
    }
    val filteredAssignments = when (selectedFilter) {
        "Completados" -> assignments.filter { it.isCompleted }
        "Pendientes" -> assignments.filter { !it.isCompleted }
        else -> assignments
    }

    Scaffold(
        containerColor = AppColors.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Tareas",
                        style = CrimsonSemiBold,
                        fontSize = 22.sp,
                        color = Green49
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Green49
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.background
                )
            )
        },
        bottomBar = { PatientBottomNav(navController) }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Green49)
                }
            }

            customTasks.isEmpty() && assignments.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aún no tienes tareas asignadas",
                        color = AppColors.textSecondary
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Filtro (Todos / Completados / Pendientes)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TaskFilterDropdown(
                            options = filterOptions,
                            selected = selectedFilter,
                            onSelect = { selectedFilter = it }
                        )
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Propósitos de texto libre del psicólogo
                        items(filteredCustom) { task ->
                            PatientTaskCard(
                                task = task,
                                onComplete = { viewModel.completeCustomTask(task.id) }
                            )
                        }
                        // Tareas de biblioteca (contenido asignado)
                        items(filteredAssignments) { assignment ->
                            TaskCard(
                                assignment = assignment,
                                onComplete = { viewModel.completeAssignment(assignment.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Dropdown de filtro con el mismo estilo que el de la vista del psicólogo. */
@Composable
private fun TaskFilterDropdown(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Row(
            modifier = Modifier
                .clickable { expanded = true }
                .border(1.dp, AppColors.outline, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(selected, color = AppColors.textPrimary, fontSize = 14.sp)
            Icon(
                imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                contentDescription = "Abrir filtro",
                tint = AppColors.textSecondary
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, fontSize = 14.sp) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
