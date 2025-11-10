package com.softfocus.features.home.presentation.psychologist.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.R
import com.softfocus.features.therapy.domain.models.PatientDirectory
import com.softfocus.ui.components.ProfileAvatar
import com.softfocus.ui.theme.Black
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Gray828
import com.softfocus.ui.theme.GrayA2
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.Green65
import com.softfocus.ui.theme.GreenF2
import com.softfocus.ui.theme.SourceSansRegular
import com.softfocus.ui.theme.White

@Composable
fun PatientsTracking(
    patients: List<PatientDirectory> = emptyList(),
    onPatientClick: (String) -> Unit = {},
    onViewAllClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pacientes con actividad reciente",
                style = CrimsonSemiBold,
                fontSize = 20.sp,
                color = Green65,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Estado vacío o lista de pacientes
        if (patients.isEmpty()) {
            EmptyPatientsState()
        } else {
            // Lista de pacientes (máximo 4)
            patients.take(4).forEach { patient ->
                PatientActivityCard(
                    patient = patient,
                    onClick = { onPatientClick(patient.patientId) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Botón "Ver todos"
            if (patients.size > 4) {
                TextButton(
                    onClick = onViewAllClick,
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Text(
                        text = "Ver todos",
                        style = SourceSansRegular,
                        fontSize = 14.sp,
                        color = Green65
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyPatientsState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Imagen del elefante
        Image(
            painter = painterResource(id = R.drawable.elephant_focus),
            contentDescription = "Sin pacientes",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mensaje
        Text(
            text = "Aún no tienes pacientes",
            style = CrimsonSemiBold,
            fontSize = 18.sp,
            color = Green65,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Conecta con alguno compartiendo tu código de invitación",
            style = SourceSansRegular,
            fontSize = 14.sp,
            color = Gray828,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
fun PatientActivityCard(
    patient: PatientDirectory,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GreenF2),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar del paciente (forma cuadrada redondeada)
            ProfileAvatar(
                imageUrl = patient.profilePhotoUrl.takeIf { it.isNotEmpty() },
                fullName = patient.patientName,
                size = 56.dp,
                fontSize = 20.sp,
                backgroundColor = Color(0xFFE8F5E9),
                textColor = Green49,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Información del paciente
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = patient.patientName,
                    style = CrimsonSemiBold,
                    fontSize = 16.sp,
                    color = Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${patient.sessionCount} sesiones",
                    style = SourceSansRegular,
                    fontSize = 13.sp,
                    color = GrayA2
                )
                patient.lastSessionDate?.let { lastSession ->
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Última sesión: $lastSession",
                        style = SourceSansRegular,
                        fontSize = 12.sp,
                        color = GrayA2
                    )
                }
            }
        }
    }
}

