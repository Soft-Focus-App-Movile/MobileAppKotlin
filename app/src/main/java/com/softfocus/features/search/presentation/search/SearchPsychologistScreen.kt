package com.softfocus.features.search.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.res.painterResource
import com.softfocus.R
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.softfocus.features.search.domain.models.Psychologist
import com.softfocus.features.search.presentation.components.PsychologistCard
import com.softfocus.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPsychologistScreen(
    onNavigateBack: () -> Unit,
    onPsychologistClick: (String) -> Unit,
    viewModel: SearchPsychologistViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()
    var showFilters by remember { mutableStateOf(false) }

    // Detectar cuándo se llega al final para cargar más
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && lastVisibleIndex >= state.psychologists.size - 3) {
                    viewModel.loadNextPage()
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Buscar Psicólogos",
                        style = CrimsonSemiBold,
                        fontSize = 20.sp,
                        color = Green65
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Green49
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_filter),
                            contentDescription = "Filtros",
                            tint = Color.Unspecified
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            // Barra de búsqueda
            TextField(
                value = state.searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = {
                    Text(
                        text = "Buscar por nombre o especialidad...",
                        style = SourceSansRegular,
                        fontSize = 14.sp,
                        color = Gray787
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Green49
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            // Mostrar filtros si están activos
            if (showFilters) {
                FiltersSection(
                    selectedSpecialties = state.selectedSpecialties,
                    onSpecialtyToggle = { viewModel.onSpecialtyToggle(it) },
                    onlyAvailable = state.onlyAvailable,
                    onAvailabilityToggle = { viewModel.onAvailabilityToggle() },
                    onClearFilters = { viewModel.clearFilters() }
                )
            }

            // Lista de resultados
            when {
                state.isLoading && state.psychologists.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Green49)
                    }
                }
                state.error != null && state.psychologists.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = state.error ?: "Error desconocido",
                                style = SourceSansRegular,
                                fontSize = 14.sp,
                                color = Gray787
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.retry() },
                                colors = ButtonDefaults.buttonColors(containerColor = Green49)
                            ) {
                                Text("Reintentar", color = Color.White)
                            }
                        }
                    }
                }
                state.psychologists.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron psicólogos",
                            style = SourceSansRegular,
                            fontSize = 14.sp,
                            color = Gray787
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Text(
                                text = "${state.totalCount} psicólogos encontrados",
                                style = SourceSansSemiBold,
                                fontSize = 14.sp,
                                color = Gray787,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }

                        items(state.psychologists) { psychologist ->
                            PsychologistCard(
                                psychologist = psychologist,
                                onClick = { onPsychologistClick(psychologist.id) }
                            )
                        }

                        if (state.isLoading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = Green49)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FiltersSection(
    selectedSpecialties: List<String>,
    onSpecialtyToggle: (String) -> Unit,
    onlyAvailable: Boolean,
    onAvailabilityToggle: () -> Unit,
    onClearFilters: () -> Unit
) {
    val specialties = listOf("Ansiedad", "Depresión", "Estrés", "Parejas", "Familiar", "Infantil")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filtros",
                    style = CrimsonSemiBold,
                    fontSize = 16.sp,
                    color = Green65
                )
                TextButton(onClick = onClearFilters) {
                    Text(
                        text = "Limpiar",
                        style = SourceSansSemiBold,
                        fontSize = 14.sp,
                        color = Green49
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Especialidades
            Text(
                text = "Especialidades",
                style = SourceSansSemiBold,
                fontSize = 14.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Chips de especialidades
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                specialties.take(3).forEach { specialty ->
                    FilterChip(
                        selected = specialty in selectedSpecialties,
                        onClick = { onSpecialtyToggle(specialty) },
                        label = {
                            Text(
                                text = specialty,
                                style = SourceSansRegular,
                                fontSize = 12.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Green49,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Disponibilidad
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = onlyAvailable,
                    onCheckedChange = { onAvailabilityToggle() },
                    colors = CheckboxDefaults.colors(checkedColor = Green49)
                )
                Text(
                    text = "Solo psicólogos disponibles",
                    style = SourceSansRegular,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FiltersPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        FiltersSection(
            selectedSpecialties = listOf("Ansiedad", "Depresión"),
            onSpecialtyToggle = {},
            onlyAvailable = true,
            onAvailabilityToggle = {},
            onClearFilters = {}
        )
    }
}
