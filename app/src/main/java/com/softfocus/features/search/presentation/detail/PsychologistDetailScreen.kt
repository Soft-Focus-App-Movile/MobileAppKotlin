package com.softfocus.features.search.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.softfocus.features.search.domain.models.Psychologist
import com.softfocus.features.search.presentation.components.ContactDialog
import com.softfocus.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PsychologistDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: PsychologistDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showContactDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Green49)
                }
            }
            state.error != null -> {
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
            state.psychologist != null -> {
                PsychologistDetailContent(
                    psychologist = state.psychologist!!,
                    onContactClick = { showContactDialog = true },
                    modifier = Modifier.padding(paddingValues)
                )

                if (showContactDialog) {
                    ContactDialog(
                        psychologist = state.psychologist!!,
                        onDismiss = { showContactDialog = false }
                    )
                }
            }
        }
    }
}

@Composable
private fun PsychologistDetailContent(
    psychologist: Psychologist,
    onContactClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header con foto y nombre
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(Green49)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Foto de perfil
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    if (psychologist.profileImageUrl != null) {
                        AsyncImage(
                            model = psychologist.profileImageUrl,
                            contentDescription = psychologist.fullName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Green49,
                            modifier = Modifier.size(70.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = psychologist.fullName,
                    style = CrimsonSemiBold,
                    fontSize = 24.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Rating
                if (psychologist.averageRating != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = YellowCB9D,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", psychologist.averageRating),
                            style = SourceSansSemiBold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Text(
                            text = " (${psychologist.totalReviews} reseñas)",
                            style = SourceSansRegular,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Contenido
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Información básica
            InfoCard {
                InfoRow(label = "Años de experiencia", value = "${psychologist.yearsOfExperience} años")
                if (psychologist.city != null) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    InfoRow(label = "Ciudad", value = psychologist.city)
                }
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                InfoRow(
                    label = "Disponibilidad",
                    value = if (psychologist.isAcceptingNewPatients) "Disponible" else "No disponible",
                    valueColor = if (psychologist.isAcceptingNewPatients) Green49 else Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Formación académica
            if (psychologist.university != null || psychologist.degree != null) {
                SectionTitle("Formación Académica")
                Spacer(modifier = Modifier.height(8.dp))
                InfoCard {
                    if (psychologist.university != null) {
                        InfoRow(label = "Universidad", value = psychologist.university)
                    }
                    if (psychologist.degree != null) {
                        if (psychologist.university != null) {
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                        InfoRow(label = "Grado", value = psychologist.degree)
                    }
                    if (psychologist.graduationYear != null) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        InfoRow(label = "Año de graduación", value = psychologist.graduationYear.toString())
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Información profesional
            if (psychologist.licenseNumber != null || psychologist.professionalCollege != null) {
                SectionTitle("Colegiatura")
                Spacer(modifier = Modifier.height(8.dp))
                InfoCard {
                    if (psychologist.licenseNumber != null) {
                        InfoRow(label = "Número de colegiatura", value = psychologist.licenseNumber)
                    }
                    if (psychologist.professionalCollege != null) {
                        if (psychologist.licenseNumber != null) {
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                        InfoRow(label = "Colegio profesional", value = psychologist.professionalCollege)
                    }
                    if (psychologist.collegeRegion != null) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        InfoRow(label = "Región", value = psychologist.collegeRegion)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Especialidades
            if (psychologist.specialties.isNotEmpty()) {
                SectionTitle("Especialidades")
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        psychologist.specialties.forEach { specialty ->
                            Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text(
                                    text = "•",
                                    style = SourceSansSemiBold,
                                    fontSize = 14.sp,
                                    color = Green49
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = specialty,
                                    style = SourceSansRegular,
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Bio profesional
            if (psychologist.professionalBio != null) {
                SectionTitle("Acerca de")
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = psychologist.professionalBio,
                        style = SourceSansRegular,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Justify,
                        lineHeight = 20.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Idiomas
            if (!psychologist.languages.isNullOrEmpty()) {
                SectionTitle("Idiomas")
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = psychologist.languages.joinToString(", "),
                        style = SourceSansRegular,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Botón de contacto
            Button(
                onClick = onContactClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = YellowCB9D),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Contactar",
                    style = SourceSansSemiBold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun InfoCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String, valueColor: Color = Color.Black) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = SourceSansRegular,
            fontSize = 14.sp,
            color = Gray787
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = SourceSansSemiBold,
            fontSize = 14.sp,
            color = valueColor
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = CrimsonSemiBold,
        fontSize = 18.sp,
        color = Green65
    )
}

@Preview(showBackground = true)
@Composable
fun PsychologistDetailContentPreview() {
    PsychologistDetailContent(
        psychologist = Psychologist(
            id = "1",
            fullName = "Dra. María García López",
            profileImageUrl = null,
            professionalBio = "Psicóloga clínica con más de 8 años de experiencia en terapia cognitivo-conductual. Especializada en el tratamiento de ansiedad, depresión y estrés. Mi enfoque es crear un espacio seguro y de confianza donde puedas explorar tus emociones y desarrollar herramientas efectivas para tu bienestar mental.",
            specialties = listOf("Ansiedad", "Depresión", "Estrés", "Terapia Cognitivo-Conductual"),
            yearsOfExperience = 8,
            city = "Lima",
            languages = listOf("Español", "Inglés"),
            isAcceptingNewPatients = true,
            averageRating = 4.8,
            totalReviews = 127,
            allowsDirectMessages = true,
            targetAudience = listOf("Adultos", "Adolescentes"),
            email = "maria.garcia@example.com",
            phone = "+51 999 888 777",
            whatsApp = "+51999888777",
            corporateEmail = "maria@clinica.com",
            university = "Universidad Nacional Mayor de San Marcos",
            graduationYear = 2015,
            degree = "Licenciatura en Psicología",
            licenseNumber = "CPsP 12345",
            professionalCollege = "Colegio de Psicólogos del Perú",
            collegeRegion = "Lima"
        ),
        onContactClick = {}
    )
}
