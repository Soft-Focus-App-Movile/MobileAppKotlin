package com.softfocus.features.therapy.presentation.psychologist.patientlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.SoftFocusMobileTheme

// --- Definición de Colores y Datos ---
val primaryGreen = Color(0xFF4B634B)
val cardBackground = Color(0xFFF7F7F3) // Un beige/verde muy claro
val lightGrayText = Color.Gray

// Datos de ejemplo para la lista
data class Patient(val name: String, val connection: String, val image: Any) // 'Any' para un placeholder

val dummyPatients = listOf(
    Patient("Ana García", "Conexión: Hace 3 meses", Icons.Default.AccountCircle),
    Patient("Luis Torres", "Conexión: Hace 1 mes", Icons.Default.AccountCircle),
    Patient("María Lopes", "Conexión: Hace 1 día", Icons.Default.AccountCircle),
    Patient("Renzo Rivera", "Conexión: Hace 3 meses", Icons.Default.AccountCircle),
    Patient("Maria Perez", "Conexión: Hace 1 mes", Icons.Default.AccountCircle)
)

// --- Composable Principal de la Pantalla ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientListScreen() {
    Scaffold(
        topBar = { PacientesTopAppBar() },

    ) { paddingValues ->
        PatientList(
            paddingValues = paddingValues,
            patients = dummyPatients
        )
    }
}

// --- Componentes de la Pantalla ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PacientesTopAppBar() {
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
                    contentDescription = " ",
                    tint = Color.Black
                )
            }
        },
        actions = {
            Spacer(modifier = Modifier.width(48.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientList(paddingValues: PaddingValues, patients: List<Patient>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp) // Espacio entre cada tarjeta
    ) {
        item { Spacer(modifier = Modifier.height(16.dp)) } // Espacio superior

        items(patients) { patient ->
            PatientCard(patient = patient)
        }

        item { Spacer(modifier = Modifier.height(16.dp)) } // Espacio inferior
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientCard(patient: Patient) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder para la imagen de perfil

            Box(
                modifier = Modifier
                    .size(56.dp) // Tamaño de 56dp x 56dp
                    .clip(RoundedCornerShape(8.dp)) // Esquinas redondeadas de 8dp
                    .background(Color.Gray) // Usamos el color de la data como fondo
            ) {

                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.align(Alignment.Center).size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Columna para los textos
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = patient.name,
                    style = CrimsonSemiBold.copy(fontSize = 24.sp),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = patient.connection,
                    fontSize = 14.sp,
                    color = lightGrayText
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

// --- Preview para ver el diseño ---

@Preview(showBackground = true)
@Composable
fun PatientListScreenPreview() {
    SoftFocusMobileTheme() {
        PatientListScreen()
    }
}