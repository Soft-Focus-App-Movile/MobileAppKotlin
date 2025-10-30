package com.softfocus.features.admin.presentation.verifypsychologist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.features.admin.domain.models.PsychologistDetail
import com.softfocus.ui.theme.*

@Composable
fun VerifyPsychologistScreen(
    userId: String,
    viewModel: VerifyPsychologistViewModel,
    onNavigateBack: () -> Unit
) {
    val psychologist by viewModel.psychologist.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val verificationSuccess by viewModel.verificationSuccess.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(userId) {
        viewModel.loadPsychologistDetail(userId)
    }

    LaunchedEffect(verificationSuccess) {
        if (verificationSuccess) {
            viewModel.resetVerificationSuccess()
            onNavigateBack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Green49)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Verificar Psicólogo",
                style = CrimsonSemiBold,
                fontSize = 24.sp,
                color = Green49
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Green49)
            }
        } else if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
            psychologist?.let { detail ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InfoSection(title = "Información Personal") {
                        InfoRow("Nombre completo", detail.fullName)
                        InfoRow("Email", detail.email)
                        detail.phone?.let { InfoRow("Teléfono", it) }
                    }

                    InfoSection(title = "Información Profesional") {
                        InfoRow("Licencia", detail.licenseNumber)
                        InfoRow("Colegio Profesional", detail.professionalCollege)
                        detail.collegeRegion?.let { InfoRow("Región Colegial", it) }
                        detail.university?.let { InfoRow("Universidad", it) }
                        detail.graduationYear?.let { InfoRow("Año de Graduación", it.toString()) }
                        InfoRow("Años de Experiencia", detail.yearsOfExperience.toString())

                        if (detail.specialties.isNotEmpty()) {
                            Text(
                                text = "Especialidades:",
                                style = SourceSansBold,
                                fontSize = 14.sp,
                                color = Black
                            )
                            detail.specialties.forEach { specialty ->
                                Text(
                                    text = "• ${specialty.name}",
                                    style = SourceSansRegular,
                                    fontSize = 14.sp,
                                    color = Gray828,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }

                    InfoSection(title = "Documentos") {
                        val hasDocuments = detail.licenseDocumentUrl != null ||
                                detail.diplomaCertificateUrl != null ||
                                detail.identityDocumentUrl != null ||
                                !detail.additionalCertificatesUrls.isNullOrEmpty()

                        if (hasDocuments) {
                            detail.licenseDocumentUrl?.let { url ->
                                DocumentButton("Ver Licencia Profesional") {
                                    uriHandler.openUri(url)
                                }
                            }
                            detail.diplomaCertificateUrl?.let { url ->
                                DocumentButton("Ver Diploma") {
                                    uriHandler.openUri(url)
                                }
                            }
                            detail.identityDocumentUrl?.let { url ->
                                DocumentButton("Ver DNI") {
                                    uriHandler.openUri(url)
                                }
                            }
                            detail.additionalCertificatesUrls?.forEach { url ->
                                DocumentButton("Ver Certificado Adicional") {
                                    uriHandler.openUri(url)
                                }
                            }
                        } else {
                            Text(
                                text = "Este psicólogo se registró vía OAuth y no ha subido documentos aún.",
                                style = SourceSansRegular,
                                fontSize = 14.sp,
                                color = Gray828
                            )
                        }
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { viewModel.updateNotes(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Notas (opcional)") },
                        minLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Green37,
                            unfocusedIndicatorColor = GrayE0
                        )
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.rejectPsychologist() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !isLoading
                    ) {
                        Text("Rechazar", color = MaterialTheme.colorScheme.error)
                    }

                    Button(
                        onClick = { viewModel.approvePsychologist() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Green49),
                        enabled = !isLoading
                    ) {
                        Text("Aprobar", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun InfoSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = CrimsonSemiBold,
                fontSize = 18.sp,
                color = Green49
            )
            Divider(color = GrayE0)
            content()
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = SourceSansBold,
            fontSize = 14.sp,
            color = Black
        )
        Text(
            text = value,
            style = SourceSansRegular,
            fontSize = 14.sp,
            color = Gray828
        )
    }
}

@Composable
fun DocumentButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Green37)
    ) {
        Text(text, color = Color.White)
    }
}
