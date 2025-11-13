package com.softfocus.features.auth.presentation.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.R
import com.softfocus.core.networking.ApiConstants
import com.softfocus.ui.theme.SoftFocusMobileTheme
import com.softfocus.features.auth.presentation.di.PresentationModule.getLoginViewModel
import com.softfocus.ui.theme.Black
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Gray828
import com.softfocus.ui.theme.GrayE0
import com.softfocus.ui.theme.Green37
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.YellowEB
import com.softfocus.ui.theme.SourceSansRegular
import com.softfocus.ui.theme.SourceSansBold
import androidx.compose.ui.platform.LocalContext
import com.softfocus.core.data.local.UserSession
import com.softfocus.features.admin.presentation.di.AdminPresentationModule
import com.softfocus.features.therapy.presentation.di.TherapyPresentationModule
import com.softfocus.features.psychologist.presentation.di.PsychologistPresentationModule

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onAdminLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToRegisterWithOAuth: (email: String, fullName: String, tempToken: String) -> Unit,
    onNavigateToPendingVerification: () -> Unit,
    onNavigateToForgotPassword: () -> Unit = {}
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val oauthData by viewModel.oauthDataForRegistration.collectAsState()
    val googleSignInIntent by viewModel.googleSignInIntent.collectAsState()
    val psychologistPendingVerification by viewModel.psychologistPendingVerification.collectAsState()

    val isPasswordVisible = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val userSession = remember { UserSession(context) }

    // Activity Result Launcher for Google Sign-In
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.handleGoogleSignInResult(result.data)
    }

    // Launch Google Sign-In Intent when available
    googleSignInIntent?.let { intent ->
        googleSignInLauncher.launch(intent)
        viewModel.clearGoogleSignInIntent()
    }

    // Navigate on login success based on user type
    user?.let { currentUser ->
        userSession.saveUser(currentUser)
        if (currentUser.userType == com.softfocus.features.auth.domain.models.UserType.ADMIN) {
            currentUser.token?.let { AdminPresentationModule.setAuthToken(it) }
            onAdminLoginSuccess()
        } else {
            currentUser.token?.let { token ->
                if (currentUser.userType == com.softfocus.features.auth.domain.models.UserType.PSYCHOLOGIST) {
                    PsychologistPresentationModule.setAuthToken(token)
                }
            }
            onLoginSuccess()
        }
    }

    // Navigate to register with OAuth data
    oauthData?.let { data ->
        onNavigateToRegisterWithOAuth(data.email, data.fullName, data.tempToken)
        viewModel.clearOAuthData()
    }

    // Navigate to pending verification if psychologist is not verified
    if (psychologistPendingVerification) {
        onNavigateToPendingVerification()
        viewModel.clearPendingVerification()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Iniciar Sesión",
            style = CrimsonSemiBold,
            fontSize = 40.sp,
            color = Green49,
            modifier = Modifier
                .padding( 11.dp)
                .align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(1.dp))

        Image(
            painter = painterResource(id = R.drawable.soft_panda_black),
            contentDescription = "Soft Focus Logo",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(60.dp))

        // Email field
        OutlinedTextField(
            value = email,
            onValueChange = { viewModel.updateEmail(it) },
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
                    text = "Nombre de usuario",
                    style = SourceSansRegular,
                    color = Gray828
                )
            },
            leadingIcon = {
                Icon(Icons.Default.Person,
                    contentDescription = null,
                    tint = Green37)
            },
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))


        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.updatePassword(it) },
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
                    text = "************",
                    style = SourceSansRegular,
                    color = Gray828
                )
            },
            leadingIcon = {
                Icon(Icons.Default.Lock,
                    contentDescription = null,
                    tint = Green37)
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
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Forgot Password Link
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onNavigateToForgotPassword) {
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    color = Green49,
                    style = SourceSansRegular,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Login button
        Button(
            onClick = { viewModel.login() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Green49
            ),
            enabled = !isLoading && email.isNotEmpty() && password.isNotEmpty()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White
                )
            } else {
                Text(
                    text = "Iniciar Sesión",
                    style = SourceSansBold,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Error message
        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Divider text con líneas
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = 1.dp,
                color = GrayE0
            )
            Text(
                text = "o continuar con",
                style = SourceSansRegular,
                color = Gray828,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = 1.dp,
                color = GrayE0
            )
        }

        // Google button
        OutlinedButton(
            onClick = {
                viewModel.signInWithGoogle(ApiConstants.GOOGLE_SERVER_CLIENT_ID)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = YellowEB
            ),
            enabled = !isLoading
        ) {
            Image(
                painter = painterResource(id = R.drawable.google_icon),
                contentDescription = "Google",
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text("Google", color = Black)
        }

        Spacer(modifier = Modifier.height(6.dp))


        TextButton(onClick = onNavigateToRegister) {
            Text(
                text = "No tienes cuenta? ",
                color = Color.Gray
            )
            Text(
                text = "Regístrate",
                color = Green49,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(13.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val viewModel = getLoginViewModel(context)
    SoftFocusMobileTheme {
        LoginScreen(
            viewModel = viewModel,
            onLoginSuccess = {},
            onAdminLoginSuccess = {},
            onNavigateToRegister = {},
            onNavigateToRegisterWithOAuth = { _, _, _ -> },
            onNavigateToPendingVerification = {},
            onNavigateToForgotPassword = {}
        )
    }
}
