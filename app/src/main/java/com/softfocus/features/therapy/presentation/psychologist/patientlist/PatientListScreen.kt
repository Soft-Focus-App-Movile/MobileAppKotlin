package com.softfocus.features.therapy.presentation.psychologist.patientlist

import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.softfocus.R
import com.softfocus.features.therapy.domain.models.PatientDirectory
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Green49
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

// --- Definición de Colores (del archivo original) ---
private val primaryGreen = Color(0xFF4B634B)
private val cardBackground = Color(0xFFF7F7F3)
private val lightGrayText = Color(0xFF8B8B8B)
private val dividerColor = Color(0xFFE0E0E0) // Color para el placeholder de la imagen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientListScreen(
    viewModel: PatientListViewModel,
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
                IconButton(onClick = { /* Sin acción */ }) {
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
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- Foto de Perfil (cargada desde URL) ---
            Image(
                painter = rememberAsyncImagePainter(
                    model = patient.profilePhotoUrl,
                    // Placeholder y error usan el ícono de perfil genérico
                    placeholder = painterResource(id = R.drawable.ic_profile_user),
                    error = painterResource(id = R.drawable.ic_profile_user)
                ),
                contentDescription = "Foto de ${patient.patientName}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(dividerColor)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // --- Columna para los textos (Diseño Original) ---
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = patient.patientName,
                    style = CrimsonSemiBold.copy(fontSize = 24.sp),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Conectado desde: $formattedDate",
                    fontSize = 14.sp,
                    color = lightGrayText
                )
                Spacer(modifier = Modifier.height(2.dp))

                // Texto de Sesiones (reemplaza "Ver Perfil" para coincidir con la imagen)
                Text(
                    text = "Sesiones: ${patient.sessionCount}",
                    fontSize = 14.sp,
                    color = lightGrayText,
                    fontWeight = FontWeight.Normal // Ajustado para ser info, no un botón
                )

                Spacer(modifier = Modifier.height(2.dp))

                TextButton(
                    onClick = { /* TODO: Acción para ver el perfil */ },
                    contentPadding = PaddingValues(start = 0.dp, end = 0.dp, top = 0.dp, bottom = 0.dp)
                ) {
                    Text(
                        text = "Ver Perfil",
                        fontSize = 14.sp,
                        color = primaryGreen,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}

// --- Preview para ver el diseño (con datos falsos) ---
/*
@Preview(showBackground = true)
@Composable
fun PatientListScreenPreview() {
    // Datos falsos para el preview
    val fakePatient = PatientDirectory(
        id = "1", psychologistId = "p1", patientId = "u1",
        patientName = "Ana Gómez", age = 28, profilePhotoUrl = "",
        status = "Active", startDate = "2024-10-10", sessionCount = 5, lastSessionDate = null
    )
    val fakeState = PatientListUiState(
        isLoading = false,
        patients = listOf(fakePatient, fakePatient.copy(id = "2", patientName = "Luis Torres", sessionCount = 12))
    )

    // ViewModel falso para el preview
    class FakePatientListViewModel : PatientListViewModel(GetMyPatientsUseCase(object : TherapyRepository {
        override suspend fun getMyRelationship() = Result.success(null)
        override suspend fun connectWithPsychologist(code: String) = Result.success("")
        override suspend fun getMyPatients() = Result.success(fakeState.patients)
    })) {
        init {
            _uiState.value = fakeState
        }
    }

    SoftFocusMobileTheme {
        PatientListScreen(
            viewModel = FakePatientListViewModel(),
            onPatientClick = {}
        )
    }
}

*/