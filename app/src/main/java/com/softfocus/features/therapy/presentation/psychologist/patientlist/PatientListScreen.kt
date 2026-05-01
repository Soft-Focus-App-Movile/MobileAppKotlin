package com.softfocus.features.therapy.presentation.psychologist.patientlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.features.therapy.domain.models.PatientDirectory
import com.softfocus.ui.components.ProfileAvatar
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Green49
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

// --- Definición de Colores (del archivo original) ---
private val primaryGreen = Color(0xFF4B634B)
private val cardBackground = Color(0xFFF7F7F3)
private val lightGrayText = Color(0xFF8B8B8B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientListScreen(
    viewModel: PatientListViewModel,
    onBack: () -> Unit,
    onPatientClick: (patient: PatientDirectory) -> Unit
) {
    // Recolecta el estado del ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // Llama a loadPatients() una sola vez cuando la pantalla se compone
    LaunchedEffect(Unit) {
        viewModel.loadPatients()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // --- Barra Superior (Diseño Original) ---
        TopAppBar(
            title = {
                Text(
                    text = "Pacientes",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = CrimsonSemiBold.copy(fontSize = 32.sp),
                    color = Green49
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.Black
                    )
                }
            },
            actions = {
                Spacer(modifier = Modifier.width(48.dp)) // Espaciador para centrar título
            }
        )

        // --- Contenido de la Pantalla ---
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                // Estado de Carga
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = primaryGreen
                    )
                }

                // Estado de Error
                uiState.error != null -> {
                    Text(
                        text = uiState.error ?: "Error al cargar pacientes",
                        color = Color.Red,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }

                // Estado de Éxito (Lista de pacientes)
                uiState.patients.isNotEmpty() -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.patients) { patient ->
                            PatientCard(
                                patient = patient,
                                onClick = { onPatientClick(patient) } // Llama a la acción de navegación
                            )
                        }
                    }
                }

                // Estado Vacío (Éxito pero sin pacientes)
                else -> {
                    Text(
                        text = "Aún no tienes pacientes asignados.",
                        color = lightGrayText,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun PatientCard(
    patient: PatientDirectory,
    onClick: () -> Unit
) {
    // Define el formato que deseas (ej. "10/10/2024")
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // Interpreta la fecha completa y formátala
    val formattedDate = try {
        val zonedDateTime = ZonedDateTime.parse(patient.startDate)
        zonedDateTime.format(formatter)
    } catch (e: Exception) {
        patient.startDate.substringBefore("T")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick), // El Card completo es clickeable
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 13.dp, end = 16.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- Foto de Perfil (cargada desde URL) ---
            ProfileAvatar(
                imageUrl = patient.profilePhotoUrl.takeIf { it.isNotEmpty() },
                fullName = patient.patientName,
                size = 110.dp,
                fontSize = 30.sp,
                backgroundColor = Color(0xFFE8F5E9),
                textColor = Green49,
                shape = CircleShape
            )

            Spacer(modifier = Modifier.width(16.dp))

            // --- Columna para los textos (Diseño Original) ---
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = patient.patientName,
                    style = CrimsonSemiBold.copy(fontSize = 21.sp),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Conectado desde: $formattedDate",
                    fontSize = 12.sp,
                    color = lightGrayText
                )
                Spacer(modifier = Modifier.height(2.dp))

                // Texto de Sesiones (reemplaza "Ver Perfil" para coincidir con la imagen)
                Text(
                    text = "Sesiones: ${patient.sessionCount}",
                    fontSize = 12.sp,
                    color = lightGrayText,
                    fontWeight = FontWeight.Normal // Ajustado para ser info, no un botón
                )

                Spacer(modifier = Modifier.height(1.8.dp))

                TextButton(
                    onClick = onClick,
                    contentPadding = PaddingValues(start = 0.dp, end = 0.dp, top = 0.dp, bottom = 0.dp)
                ) {
                    Text(
                        text = "Ver Perfil",
                        fontSize = 13.sp,
                        color = primaryGreen,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}