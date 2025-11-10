package com.softfocus.features.profile.presentation.general

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.R
import com.softfocus.features.therapy.presentation.connect.ConnectPsychologistViewModel
import com.softfocus.features.therapy.presentation.connect.ConnectUiState
import com.softfocus.features.therapy.presentation.di.TherapyPresentationModule
import com.softfocus.ui.theme.*

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
                        color = Green37
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
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    YellowE8,
                                    YellowCB9C
                                )
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .padding(start = 80.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 4.dp, top = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Tienes código de tu psicólogo?",
                                style = SourceSansRegular,
                                fontSize = 12.sp,
                                color = Yellow7E,
                                textAlign = TextAlign.Center,
                                lineHeight = 14.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = code,
                                onValueChange = { code = it.uppercase().take(8) },
                                placeholder = {
                                    Text(
                                        text = "Ingresa código aquí",
                                        style = CrimsonSemiBold,
                                        fontSize = 15.sp,
                                        color = White
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedContainerColor = YellowB5,
                                    focusedContainerColor = YellowB5,
                                    unfocusedTextColor = Color.White,
                                    focusedTextColor = Color.White,
                                    cursorColor = Color.White
                                ),
                                textStyle = SourceSansRegular.copy(fontSize = 11.sp),
                                enabled = uiState !is ConnectUiState.Loading,
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Button(
                                onClick = {
                                    if (code.isNotBlank()) {
                                        viewModel.connectWithPsychologist(code)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .height(28.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = YellowD8,
                                    disabledContainerColor = GrayD9
                                ),
                                enabled = code.isNotBlank() && uiState !is ConnectUiState.Loading,
                                contentPadding = PaddingValues(vertical = 0.dp)
                            ) {
                                if (uiState is ConnectUiState.Loading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = Black,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        text = "Conectar",
                                        style = SourceSansRegular,
                                        fontSize = 12.sp,
                                        color = Black
                                    )
                                }
                            }

                            if (uiState is ConnectUiState.Error) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = (uiState as ConnectUiState.Error).message,
                                    style = SourceSansRegular,
                                    fontSize = 10.sp,
                                    color = Color.Red,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 12.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            TextButton(
                                onClick = { },
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.height(24.dp)
                            ) {
                                Text(
                                    text = "¿No tienes código? Busca psicólogos aquí",
                                    style = SourceSansRegular,
                                    fontSize = 10.sp,
                                    color = Yellow7E,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 11.sp
                                )
                            }
                        }
                    }
                }

                // Jirafa image overlapping the card
                Image(
                    painter = painterResource(id = R.drawable.jiraff_focus),
                    contentDescription = "Jirafa",
                    modifier = Modifier
                        .size(250.dp)
                        .align(Alignment.CenterStart)
                        .offset(x = (-70).dp, y = (-20).dp)
                )
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
