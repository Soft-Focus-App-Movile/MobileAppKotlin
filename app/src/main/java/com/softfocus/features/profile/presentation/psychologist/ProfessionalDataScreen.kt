package com.softfocus.features.profile.presentation.psychologist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.softfocus.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalDataScreen(
    onNavigateBack: () -> Unit,
    viewModel: PsychologistProfileViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Datos profesionales",
                        style = CrimsonSemiBold,
                        color = Green37,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Green37
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White
                )
            )
        },
        containerColor = White
    ) { paddingValues ->
        when (uiState) {
            is PsychologistProfileUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF6B7C59))
                }
            }

            is PsychologistProfileUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (uiState as PsychologistProfileUiState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            is PsychologistProfileUiState.Success -> {
                profile?.let { psychProfile ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Professional Info Container
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GreenF2,
                            modifier = Modifier.fillMaxWidth()
                                .wrapContentHeight()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // License Number
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Número de licencia",
                                        style = CrimsonSemiBold,
                                        fontSize = 16.sp,
                                        color = Black
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = psychProfile.licenseNumber,
                                        style = SourceSansSemiBold,
                                        fontSize = 16.sp,
                                        color = Gray070
                                    )
                                }

                                // University
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Universidad",
                                        style = CrimsonSemiBold,
                                        fontSize = 16.sp,
                                        color = Black
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = psychProfile.university ?: "No especificado",
                                        style = SourceSansSemiBold,
                                        fontSize = 16.sp,
                                        color = Gray070
                                    )
                                }

                                // Specialties
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Especialidades",
                                        style = CrimsonSemiBold,
                                        fontSize = 16.sp,
                                        color = Black
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = psychProfile.specialties.joinToString(", "),
                                        style = SourceSansSemiBold,
                                        fontSize = 16.sp,
                                        color = Gray070
                                    )
                                }

                                // Years of Experience
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Años de experiencia",
                                        style = CrimsonSemiBold,
                                        fontSize = 16.sp,
                                        color = Black
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${psychProfile.yearsOfExperience} años",
                                        style = SourceSansSemiBold,
                                        fontSize = 16.sp,
                                        color = Gray070
                                    )
                                }
                            }
                        }

                        // Diploma/Certificates Section
                        if (psychProfile.diplomaCertificateUrl != null ||
                            psychProfile.licenseDocumentUrl != null ||
                            !psychProfile.additionalCertificatesUrls.isNullOrEmpty()
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Diploma/certificados",
                                    style = CrimsonSemiBold,
                                    fontSize = 16.sp,
                                    color = Green37
                                )

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = GreenF2,
                                    modifier = Modifier.fillMaxWidth()
                                        .wrapContentHeight()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        psychProfile.licenseDocumentUrl?.let {
                                            DocumentItem(text = "• Licenciatura en Psicología (${psychProfile.graduationYear ?: ""})")
                                        }

                                        psychProfile.diplomaCertificateUrl?.let {
                                            DocumentItem(text = "• Diplomado en Psicoterapia Breve - PUCP (2021)")
                                        }

                                        psychProfile.additionalCertificatesUrls?.forEachIndexed { index, _ ->
                                            if (index == 0) {
                                                DocumentItem(text = "• Especialización en Mindfulness y Regulación Emocional - UNMSM (2023)")
                                            }
                                        }
                                    }
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
private fun DocumentItem(text: String) {
    Text(
        text = text,
        style = SourceSansSemiBold,
        fontSize = 14.sp,
        color = Gray070,
        lineHeight = 20.sp
    )
}

@Preview(showBackground = true)
@Composable
fun ProfessionalDataScreenPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Professional Info Container
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = GreenF2,
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // License Number
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Número de licencia",
                        style = CrimsonSemiBold,
                        fontSize = 16.sp,
                        color = Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "PSI-12345",
                        style = SourceSansSemiBold,
                        fontSize = 16.sp,
                        color = Gray070
                    )
                }

                // University
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Universidad",
                        style = CrimsonSemiBold,
                        fontSize = 16.sp,
                        color = Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Universidad Nacional Mayor de San Marcos",
                        style = SourceSansSemiBold,
                        fontSize = 16.sp,
                        color = Gray070
                    )
                }

                // Specialties
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Especialidades",
                        style = CrimsonSemiBold,
                        fontSize = 16.sp,
                        color = Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ansiedad, Depresión, Terapia Cognitiva",
                        style = SourceSansSemiBold,
                        fontSize = 16.sp,
                        color = Gray070
                    )
                }

                // Years of Experience
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Años de experiencia",
                        style = CrimsonSemiBold,
                        fontSize = 16.sp,
                        color = Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "8 años",
                        style = SourceSansSemiBold,
                        fontSize = 16.sp,
                        color = Gray070
                    )
                }
            }
        }

        // Diploma/Certificates Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Diploma/certificados",
                style = CrimsonSemiBold,
                fontSize = 16.sp,
                color = Green37
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = GreenF2,
                modifier = Modifier.fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DocumentItem(text = "• Licenciatura en Psicología (2015)")
                    DocumentItem(text = "• Diplomado en Psicoterapia Breve - PUCP (2021)")
                    DocumentItem(text = "• Especialización en Mindfulness y Regulación Emocional - UNMSM (2023)")
                }
            }
        }
    }
}
