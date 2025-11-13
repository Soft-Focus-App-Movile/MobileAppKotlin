package com.softfocus.features.auth.presentation.forgotpassword

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.R
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Gray828
import com.softfocus.ui.theme.GrayE0
import com.softfocus.ui.theme.Green37
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.SourceSansBold
import com.softfocus.ui.theme.SourceSansRegular

@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val email by viewModel.email.collectAsState()
    val token by viewModel.token.collectAsState()
    val newPassword by viewModel.newPassword.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()

    val emailError by viewModel.emailError.collectAsState()
    val tokenError by viewModel.tokenError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val confirmPasswordError by viewModel.confirmPasswordError.collectAsState()

    val isPasswordVisible = remember { mutableStateOf(false) }
    val isConfirmPasswordVisible = remember { mutableStateOf(false) }

    val isLoading = uiState is ForgotPasswordUiState.Loading

    // Navegar de vuelta al login cuando la contraseña se restablece exitosamente
    LaunchedEffect(uiState) {
        if (uiState is ForgotPasswordUiState.Success) {
            // Esperamos un poco para que el usuario vea el mensaje de éxito
            kotlinx.coroutines.delay(1500)
            onNavigateBack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .padding(top = 48.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = if (uiState is ForgotPasswordUiState.CodeSent)
                "Restablecer Contraseña"
            else
                "Recuperar Contraseña",
            style = CrimsonSemiBold,
            fontSize = 34.sp,
            color = Green49,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 11.dp)
                .align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(1.dp))

        Image(
            painter = painterResource(id = R.drawable.soft_panda_black),
            contentDescription = "Soft Focus Logo",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Mostrar mensaje según el estado
        when (val state = uiState) {
            is ForgotPasswordUiState.EnterEmail -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Ingresa tu correo electrónico y te",
                        style = SourceSansRegular,
                        fontSize = 14.sp,
                        color = Gray828,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "enviaremos un código de recuperación",
                        style = SourceSansRegular,
                        fontSize = 14.sp,
                        color = Gray828,
                        textAlign = TextAlign.Center
                    )
                }
            }
            is ForgotPasswordUiState.CodeSent -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Si el email es válido, recibirás un",
                        style = SourceSansRegular,
                        fontSize = 14.sp,
                        color = Green49,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "código para restablecer tu contraseña",
                        style = SourceSansRegular,
                        fontSize = 14.sp,
                        color = Green49,
                        textAlign = TextAlign.Center
                    )
                }
            }
            is ForgotPasswordUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "¡Contraseña restablecida!",
                        style = SourceSansBold,
                        fontSize = 16.sp,
                        color = Green49,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tu contraseña ha sido actualizada",
                        style = SourceSansRegular,
                        fontSize = 14.sp,
                        color = Gray828,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "correctamente",
                        style = SourceSansRegular,
                        fontSize = 14.sp,
                        color = Gray828,
                        textAlign = TextAlign.Center
                    )
                }
            }
            is ForgotPasswordUiState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    style = SourceSansRegular,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                )
            }
            else -> {}
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Pantalla 1: Solo Email
        if (uiState !is ForgotPasswordUiState.CodeSent) {
            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.onEmailChange(it) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Green37,
                    unfocusedIndicatorColor = GrayE0
                ),
                placeholder = {
                    Text(
                        text = "Correo electrónico",
                        style = SourceSansRegular,
                        color = Gray828
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = null,
                        tint = Green37
                    )
                },
                singleLine = true,
                enabled = !isLoading,
                isError = emailError != null,
                supportingText = emailError?.let {
                    { Text(text = it, color = MaterialTheme.colorScheme.error) }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Send Code Button
            Button(
                onClick = { viewModel.sendResetCode() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green49
                ),
                enabled = !isLoading && email.isNotEmpty()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Enviar Código",
                        style = SourceSansBold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
        // Pantalla 2: Token + Email + Password + Confirm Password
        else {
            // Token field
            OutlinedTextField(
                value = token,
                onValueChange = { viewModel.onTokenChange(it) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Green37,
                    unfocusedIndicatorColor = GrayE0
                ),
                placeholder = {
                    Text(
                        text = "Código de verificación",
                        style = SourceSansRegular,
                        color = Gray828
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.VpnKey,
                        contentDescription = null,
                        tint = Green37
                    )
                },
                singleLine = true,
                enabled = !isLoading,
                isError = tokenError != null,
                supportingText = tokenError?.let {
                    { Text(text = it, color = MaterialTheme.colorScheme.error) }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email field (read-only para mostrar el email)
            OutlinedTextField(
                value = email,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Green37,
                    unfocusedIndicatorColor = GrayE0,
                    disabledIndicatorColor = GrayE0
                ),
                placeholder = {
                    Text(
                        text = "Correo electrónico",
                        style = SourceSansRegular,
                        color = Gray828
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = null,
                        tint = Green37
                    )
                },
                singleLine = true,
                enabled = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            // New Password field
            OutlinedTextField(
                value = newPassword,
                onValueChange = { viewModel.onNewPasswordChange(it) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Green37,
                    unfocusedIndicatorColor = GrayE0
                ),
                placeholder = {
                    Text(
                        text = "Nueva contraseña",
                        style = SourceSansRegular,
                        color = Gray828
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = Green37
                    )
                },
                visualTransformation = if (isPasswordVisible.value) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible.value = !isPasswordVisible.value }) {
                        Icon(
                            imageVector = if (isPasswordVisible.value) {
                                Icons.Default.Visibility
                            } else {
                                Icons.Default.VisibilityOff
                            },
                            contentDescription = if (isPasswordVisible.value) {
                                "Ocultar contraseña"
                            } else {
                                "Mostrar contraseña"
                            },
                            tint = Green37
                        )
                    }
                },
                singleLine = true,
                enabled = !isLoading,
                isError = passwordError != null,
                supportingText = passwordError?.let {
                    { Text(text = it, color = MaterialTheme.colorScheme.error) }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { viewModel.onConfirmPasswordChange(it) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Green37,
                    unfocusedIndicatorColor = GrayE0
                ),
                placeholder = {
                    Text(
                        text = "Confirmar contraseña",
                        style = SourceSansRegular,
                        color = Gray828
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = Green37
                    )
                },
                visualTransformation = if (isConfirmPasswordVisible.value) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    IconButton(onClick = { isConfirmPasswordVisible.value = !isConfirmPasswordVisible.value }) {
                        Icon(
                            imageVector = if (isConfirmPasswordVisible.value) {
                                Icons.Default.Visibility
                            } else {
                                Icons.Default.VisibilityOff
                            },
                            contentDescription = if (isConfirmPasswordVisible.value) {
                                "Ocultar contraseña"
                            } else {
                                "Mostrar contraseña"
                            },
                            tint = Green37
                        )
                    }
                },
                singleLine = true,
                enabled = !isLoading,
                isError = confirmPasswordError != null,
                supportingText = confirmPasswordError?.let {
                    { Text(text = it, color = MaterialTheme.colorScheme.error) }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Reset Password Button
            Button(
                onClick = { viewModel.resetPassword() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green49
                ),
                enabled = !isLoading && token.isNotEmpty() && newPassword.isNotEmpty() && confirmPassword.isNotEmpty()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Restablecer Contraseña",
                        style = SourceSansBold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón de volver al login (solo si no está cargando o si hubo éxito)
        if (uiState !is ForgotPasswordUiState.Success) {
            androidx.compose.material3.TextButton(onClick = onNavigateBack) {
                Text(
                    text = "Volver al inicio de sesión",
                    color = Green49,
                    style = SourceSansBold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
