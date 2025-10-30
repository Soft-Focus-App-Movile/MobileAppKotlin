package com.softfocus.features.therapy.presentation.connect

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.features.therapy.presentation.di.TherapyPresentationModule
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Gray828
import com.softfocus.ui.theme.SourceSansRegular

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectPsychologistScreen(
    viewModel: ConnectPsychologistViewModel,
    onNavigateBack: () -> Unit,
    onConnectionSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var code by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is ConnectUiState.Success) {
            onConnectionSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Conectar con Psicólogo",
                        style = CrimsonSemiBold,
                        fontSize = 20.sp,
                        color = Gray828
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8DC)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(150.dp)
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Tienes código de tu psicólogo?",
                            style = SourceSansRegular,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Gray828,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = code,
                            onValueChange = { code = it.uppercase().take(8) },
                            placeholder = {
                                Text(
                                    text = "Ingresa código aquí",
                                    style = SourceSansRegular,
                                    fontSize = 14.sp
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFF6B8E6F),
                                focusedBorderColor = Color(0xFF6B8E6F),
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White
                            ),
                            enabled = uiState !is ConnectUiState.Loading
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (code.isNotBlank()) {
                                    viewModel.connectWithPsychologist(code)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFC5D9A4)
                            ),
                            enabled = code.isNotBlank() && uiState !is ConnectUiState.Loading
                        ) {
                            if (uiState is ConnectUiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.Black
                                )
                            } else {
                                Text(
                                    text = "Conectar",
                                    style = SourceSansRegular,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black
                                )
                            }
                        }

                        if (uiState is ConnectUiState.Error) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = (uiState as ConnectUiState.Error).message,
                                style = SourceSansRegular,
                                fontSize = 14.sp,
                                color = Color.Red,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        TextButton(onClick = { }) {
                            Text(
                                text = "¿No tienes código? Busca psicólogos aquí",
                                style = SourceSansRegular,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConnectPsychologistScreenPreview() {
    val context = LocalContext.current
    val viewModel = TherapyPresentationModule.getConnectPsychologistViewModel(context)
    ConnectPsychologistScreen(
        viewModel = viewModel,
        onNavigateBack = {},
        onConnectionSuccess = {}
    )
}
