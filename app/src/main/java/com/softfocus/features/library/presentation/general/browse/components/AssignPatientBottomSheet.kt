package com.softfocus.features.library.presentation.general.browse.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.softfocus.features.therapy.domain.models.PatientDirectory
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.Gray828
import com.softfocus.ui.theme.SourceSansSemiBold
import com.softfocus.ui.theme.SourceSansRegular

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignPatientBottomSheet(
    selectedCount: Int,
    patients: List<PatientDirectory>,
    isLoading: Boolean,
    errorMessage: String?,
    onPatientSelected: (patientId: String, patientName: String) -> Unit,
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAllPatients by remember { mutableStateOf(false) }

    // Últimos 4 pacientes (asumiendo que la lista ya viene ordenada por fecha)
    val recentPatients = remember(patients) {
        patients.take(4)
    }

    val filteredPatients = remember(searchQuery, patients, showAllPatients) {
        if (searchQuery.isBlank() && !showAllPatients) {
            recentPatients // Mostrar solo los 4 recientes si no hay búsqueda
        } else if (searchQuery.isBlank()) {
            patients // Mostrar todos si se expandió
        } else {
            patients.filter { patient ->
                patient.patientName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1C1C1C),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Seleccione el Paciente",
                        style = SourceSansSemiBold.copy(fontSize = 20.sp),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$selectedCount ${if (selectedCount == 1) "contenido seleccionado" else "contenidos seleccionados"}",
                        style = SourceSansRegular.copy(fontSize = 14.sp),
                        color = Gray828
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Título de sección "Pacientes Recientes" o "Buscar Paciente"
            if (searchQuery.isBlank() && !showAllPatients) {
                Text(
                    text = "Pacientes Recientes",
                    style = SourceSansSemiBold.copy(fontSize = 16.sp),
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Search bar (solo visible cuando no está mostrando recientes O cuando hay búsqueda activa)
            if (showAllPatients || searchQuery.isNotEmpty()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        if (it.isNotEmpty()) showAllPatients = true
                    },
                    placeholder = {
                        Text(
                            text = "Buscar paciente...",
                            style = SourceSansRegular.copy(fontSize = 14.sp),
                            color = Gray828
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = Gray828
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Limpiar",
                                    tint = Gray828
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Green49,
                        unfocusedBorderColor = Gray828,
                        cursorColor = Green49
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Green49)
                    }
                }

                errorMessage != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = errorMessage,
                                style = SourceSansRegular.copy(fontSize = 14.sp),
                                color = Gray828
                            )
                            Button(
                                onClick = onRetry,
                                colors = ButtonDefaults.buttonColors(containerColor = Green49)
                            ) {
                                Text("Reintentar", color = Color.Black)
                            }
                        }
                    }
                }

                filteredPatients.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron pacientes",
                            style = SourceSansRegular.copy(fontSize = 14.sp),
                            color = Gray828
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredPatients) { patient ->
                            PatientItem(
                                patient = patient,
                                onClick = {
                                    onPatientSelected(patient.patientId, patient.patientName)
                                    onDismiss()
                                }
                            )
                        }

                        // Botón "Buscar más pacientes" si no se está mostrando todos
                        if (!showAllPatients && searchQuery.isBlank() && patients.size > 4) {
                            item {
                                Button(
                                    onClick = { showAllPatients = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF2D2D2D),
                                        contentColor = Green49
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = null,
                                            tint = Green49,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Buscar más pacientes",
                                            style = SourceSansSemiBold.copy(fontSize = 15.sp),
                                            color = Green49
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PatientItem(
    patient: PatientDirectory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF2D2D2D)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = patient.profilePhotoUrl,
                contentDescription = patient.patientName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Gray828)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = patient.patientName,
                    style = SourceSansSemiBold.copy(fontSize = 16.sp),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = patient.age.toString(),
                    style = SourceSansRegular.copy(fontSize = 13.sp),
                    color = Gray828,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
