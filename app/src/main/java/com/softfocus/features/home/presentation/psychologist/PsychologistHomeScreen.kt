package com.softfocus.features.home.presentation.psychologist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.features.psychologist.presentation.di.PsychologistPresentationModule
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Gray828
import com.softfocus.ui.theme.SourceSansRegular

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PsychologistHomeScreen(viewModel: PsychologistHomeViewModel) {
    val invitationCode = viewModel.invitationCode.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Lima, Peru",
                            style = SourceSansRegular,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notificaciones",
                            tint = Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (isLoading.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF6B8E6F))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .background(Color.White)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Hola Dra Sanchez ,",
                    style = CrimsonSemiBold,
                    fontSize = 24.sp,
                    color = Gray828,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = "👥",
                        title = "Pacientes Activos",
                        value = "2",
                        subtitle = "Ver el calendario activo"
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = "🔔",
                        title = "Alertas Pendientes",
                        value = "3",
                        subtitle = "3 alertas"
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = "⚠️",
                        title = "Sin registro Hoy",
                        value = "5",
                        subtitle = "Pacientes sin completar registro"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Mi código de invitación",
                    style = CrimsonSemiBold,
                    fontSize = 20.sp,
                    color = Gray828,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8DC)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Tienes código de tu psicólogo?",
                                style = SourceSansRegular,
                                fontSize = 14.sp,
                                color = Gray828
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = invitationCode.value?.code ?: "Cargando...",
                                style = CrimsonSemiBold,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.copyCodeToClipboard() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFC5D9A4)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Copiar",
                                        style = SourceSansRegular,
                                        fontSize = 14.sp,
                                        color = Color.Black
                                    )
                                }
                                Button(
                                    onClick = { viewModel.shareCode() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFC5D9A4)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Compartir",
                                        style = SourceSansRegular,
                                        fontSize = 14.sp,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF5F5F5))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pacientes con actividad reciente",
                        style = CrimsonSemiBold,
                        fontSize = 18.sp,
                        color = Gray828
                    )
                    TextButton(onClick = { }) {
                        Text(
                            text = "Ver todos",
                            style = SourceSansRegular,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                PatientActivityCard(
                    name = "Ana García",
                    status = "Completó registro",
                    time = "hace 4h"
                )

                PatientActivityCard(
                    name = "Luis Torres",
                    status = "No realizó registro",
                    time = "Hoy"
                )

                PatientActivityCard(
                    name = "María Lopes",
                    status = "Completó registro",
                    time = "Ayer"
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icon: String,
    title: String,
    value: String,
    subtitle: String
) {
    Card(
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = icon,
                fontSize = 32.sp
            )
            Text(
                text = title,
                style = SourceSansRegular,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Gray828
            )
            Text(
                text = value,
                style = CrimsonSemiBold,
                fontSize = 32.sp,
                color = Color.Black
            )
            Text(
                text = subtitle,
                style = SourceSansRegular,
                fontSize = 9.sp,
                color = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PsychologistHomeScreenPreview() {
    val context = LocalContext.current
    val viewModel = PsychologistPresentationModule.getPsychologistHomeViewModel(context)
    PsychologistHomeScreen(viewModel)
}

@Composable
fun PatientActivityCard(
    name: String,
    status: String,
    time: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = SourceSansRegular,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = status,
                    style = SourceSansRegular,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = time,
                    style = SourceSansRegular,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
