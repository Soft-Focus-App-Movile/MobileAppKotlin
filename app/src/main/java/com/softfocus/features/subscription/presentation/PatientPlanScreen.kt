package com.softfocus.features.subscription.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientPlanScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mi plan",
                        style = CrimsonSemiBold,
                        fontSize = 25.sp,
                        color = GreenA3
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Atrás",
                            tint = GreenA3
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(AppColors.background)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Green29
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "PACIENTE",
                        style = CrimsonSemiBold,
                        fontSize = 16.sp,
                        color = White,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Plan compartido",
                        style = CrimsonSemiBold,
                        fontSize = 32.sp,
                        color = White
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Tienes el mismo plan de tu psicólogo",
                        style = SourceSansRegular,
                        fontSize = 18.sp,
                        color = White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = White.copy(alpha = 0.3f),
                        thickness = 1.dp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        PlanFeatureItem("Acceso a todas las funciones de tu psicólogo")
                        PlanFeatureItem("Comunicación ilimitada")
                        PlanFeatureItem("Herramientas de seguimiento compartidas")
                        PlanFeatureItem("Recursos emocionales completos")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = GreenA3.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Información",
                        style = CrimsonSemiBold,
                        fontSize = 18.sp,
                        color = Green29
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Como paciente, tu plan está vinculado al de tu psicólogo. Todos los beneficios y límites se comparten con tu terapeuta.",
                        style = SourceSansRegular,
                        fontSize = 14.sp,
                        color = Gray828,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PlanFeatureItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "• ",
            style = SourceSansRegular,
            fontSize = 18.sp,
            color = White,
            modifier = Modifier.padding(end = 4.dp)
        )
        Text(
            text = text,
            style = SourceSansRegular,
            fontSize = 16.sp,
            color = White,
            modifier = Modifier.weight(1f)
        )
    }
}
