package com.softfocus.features.auth.presentation.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
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
import com.softfocus.core.ui.theme.SoftFocusTheme
import com.softfocus.features.auth.domain.models.UserType
import com.softfocus.features.auth.domain.models.PsychologySpecialty
import com.softfocus.features.auth.domain.models.User
import com.softfocus.features.auth.presentation.di.PresentationModule
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Gray828
import com.softfocus.ui.theme.GrayE0
import com.softfocus.ui.theme.Green37
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.SourceSansRegular
import com.softfocus.ui.theme.SourceSansBold

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    oauthEmail: String? = null,
    oauthFullName: String? = null,
    oauthTempToken: String? = null,
    onRegisterSuccess: (UserType) -> Unit,
    onAutoLogin: (User) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToPendingVerification: () -> Unit
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val emailError by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val confirmPasswordError by viewModel.confirmPasswordError.collectAsState()
    val userType by viewModel.userType.collectAsState()
    val registrationResultRegular by viewModel.registrationResultRegular.collectAsState()
    val registrationResultOAuth by viewModel.registrationResultOAuth.collectAsState()
    val psychologistPendingVerification by viewModel.psychologistPendingVerification.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Pre-fill from OAuth if available
    val nameParts = oauthFullName?.split(" ") ?: listOf()
    var firstName by remember { mutableStateOf(nameParts.firstOrNull() ?: "") }
    var lastName by remember { mutableStateOf(nameParts.drop(1).joinToString(" ")) }

    // Set OAuth email in ViewModel if provided
    if (oauthEmail != null && email.isEmpty()) {
        viewModel.updateEmail(oauthEmail)
    }

    // Set OAuth temp token in ViewModel if provided
    if (oauthTempToken != null) {
        viewModel.setOAuthTempToken(oauthTempToken)
    }

    var acceptedTerms by remember { mutableStateOf(false) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    // Campos adicionales para Psicólogo
    var licenseNumber by remember { mutableStateOf("") }
    var yearsOfExperience by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }
    var selectedSpecialties by remember { mutableStateOf(setOf<String>()) }
    var specialtiesExpanded by remember { mutableStateOf(false) }
    var university by remember { mutableStateOf("") }
    var graduationYear by remember { mutableStateOf("") }

    // Documentos
    var licenseFile by remember { mutableStateOf<String?>(null) }
    var diplomaFile by remember { mutableStateOf<String?>(null) }
    var dniFile by remember { mutableStateOf<String?>(null) }
    var certificationsFile by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    // Launchers para seleccionar archivos
    val licenseLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            licenseFile = it.toString()
        }
    }

    val diplomaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            diplomaFile = it.toString()
        }
    }

    val dniLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            dniFile = it.toString()
        }
    }

    val certificationsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            certificationsFile = it.toString()
        }
    }

    // Navigate on success - Regular registration (userId, email)
    registrationResultRegular?.let { (userId, email) ->
        userType?.let { type ->
            // If psychologist, navigate to pending verification screen
            if (type == UserType.PSYCHOLOGIST) {
                onNavigateToPendingVerification()
            } else {
                onRegisterSuccess(type)
            }
        }
        viewModel.clearRegistrationResult()
    }

    // Navigate on success - OAuth registration (User with JWT token for auto-login)
    registrationResultOAuth?.let { user ->
        onAutoLogin(user)
        viewModel.clearRegistrationResult()
    }

    // Navigate to pending verification if psychologist registration is pending
    if (psychologistPendingVerification) {
        onNavigateToPendingVerification()
        viewModel.clearRegistrationResult()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(5.dp))

        // Title
        Text(
            text = "Regístrate",
            style = CrimsonSemiBold,
            fontSize = 40.sp,
            color = Green49,
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(1.dp))

        // Panda logo
        Image(
            painter = painterResource(id = R.drawable.soft_panda_black),
            contentDescription = "Soft Focus Logo",
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // First name and Last name in row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Green37,
                    unfocusedIndicatorColor = GrayE0
                ),
                placeholder = {
                    Text(
                        text = "Nombre",
                        style = SourceSansRegular,
                        color = Gray828
                    )
                },
                singleLine = true,
                enabled = !isLoading
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Green37,
                    unfocusedIndicatorColor = GrayE0
                ),
                placeholder = {
                    Text(
                        text = "Apellido",
                        style = SourceSansRegular,
                        color = Gray828
                    )
                },
                singleLine = true,
                enabled = !isLoading
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Email
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.updateEmail(it) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Green37,
                    unfocusedIndicatorColor = GrayE0,
                    disabledContainerColor = GrayE0.copy(alpha = 0.2f),
                    disabledIndicatorColor = GrayE0
                ),
                placeholder = {
                    Text(
                        text = "Correo",
                        style = SourceSansRegular,
                        color = Gray828
                    )
                },
                singleLine = true,
                enabled = !isLoading && oauthEmail == null, // Disable if from OAuth
                readOnly = oauthEmail != null
            )
            if (emailError != null) {
                Text(
                    text = emailError!!,
                    color = Color.Red,
                    style = SourceSansRegular,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Only show password fields if not from OAuth
        if (oauthEmail == null) {
            // Password
            Column(modifier = Modifier.fillMaxWidth()) {
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
                            text = "Contraseña",
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
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) {
                                    Icons.Default.Visibility
                                } else {
                                    Icons.Default.VisibilityOff
                                },
                                contentDescription = if (isPasswordVisible) {
                                    "Ocultar contraseña"
                                } else {
                                    "Mostrar contraseña"
                                },
                                tint = Green37
                            )
                        }
                    },
                    visualTransformation = if (isPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    singleLine = true,
                    enabled = !isLoading
                )
                if (passwordError != null) {
                    Text(
                        text = passwordError!!,
                        color = Color.Red,
                        style = SourceSansRegular,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { viewModel.updateConfirmPassword(it) },
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
                            text = "Confirme su contraseña",
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
                    trailingIcon = {
                        IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                            Icon(
                                imageVector = if (isConfirmPasswordVisible) {
                                    Icons.Default.Visibility
                                } else {
                                    Icons.Default.VisibilityOff
                                },
                                contentDescription = if (isConfirmPasswordVisible) {
                                    "Ocultar contraseña"
                                } else {
                                    "Mostrar contraseña"
                                },
                                tint = Green37
                            )
                        }
                    },
                    visualTransformation = if (isConfirmPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    singleLine = true,
                    enabled = !isLoading
                )
                if (confirmPasswordError != null) {
                    Text(
                        text = confirmPasswordError!!,
                        color = Color.Red,
                        style = SourceSansRegular,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // User Type Switch
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = GrayE0.copy(alpha = 0.3f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tipo de cuenta",
                        style = SourceSansBold,
                        fontSize = 16.sp,
                        color = Gray828
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = "General",
                        style = SourceSansRegular,
                        fontSize = 14.sp,
                        color = if (userType == UserType.GENERAL) Green49 else Gray828
                    )
                    Switch(
                        checked = userType == UserType.PSYCHOLOGIST,
                        onCheckedChange = { checked ->
                            viewModel.updateUserType(
                                if (checked) UserType.PSYCHOLOGIST else UserType.GENERAL
                            )
                        },
                        enabled = !isLoading,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Green49,
                            checkedTrackColor = Green49.copy(alpha = 0.5f),
                            uncheckedThumbColor = Gray828,
                            uncheckedTrackColor = GrayE0
                        )
                    )
                    Text(
                        text = "Psicólogo",
                        style = SourceSansRegular,
                        fontSize = 14.sp,
                        color = if (userType == UserType.PSYCHOLOGIST) Green49 else Gray828
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campos adicionales para Psicólogo
        if (userType == UserType.PSYCHOLOGIST) {
            // Número de licencia y Años de experiencia en fila
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = licenseNumber,
                    onValueChange = { licenseNumber = it },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Green37,
                        unfocusedIndicatorColor = GrayE0
                    ),
                    placeholder = {
                        Text(
                            text = "Número de licencia",
                            style = SourceSansRegular,
                            color = Gray828
                        )
                    },
                    singleLine = true,
                    enabled = !isLoading
                )

                OutlinedTextField(
                    value = yearsOfExperience,
                    onValueChange = { yearsOfExperience = it },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Green37,
                        unfocusedIndicatorColor = GrayE0
                    ),
                    placeholder = {
                        Text(
                            text = "Años de experiencia",
                            style = SourceSansRegular,
                            color = Gray828
                        )
                    },
                    singleLine = true,
                    enabled = !isLoading
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Especialidades (Card clickeable con Dialog)
            val specialtiesList = PsychologySpecialty.getAllDisplayNames()

            OutlinedTextField(
                value = if (selectedSpecialties.isEmpty()) "" else "${selectedSpecialties.size} seleccionada(s)",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !isLoading) { specialtiesExpanded = true },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Green37,
                    unfocusedIndicatorColor = GrayE0,
                    disabledIndicatorColor = GrayE0,
                    disabledContainerColor = Color.Transparent
                ),
                placeholder = {
                    Text(
                        text = "Selecciona especialidades",
                        style = SourceSansRegular,
                        color = Gray828
                    )
                },
                enabled = false
            )

            // Dialog para seleccionar especialidades
            if (specialtiesExpanded) {
                AlertDialog(
                    onDismissRequest = { specialtiesExpanded = false },
                    title = {
                        Text(
                            text = "Selecciona tus especialidades",
                            style = SourceSansBold,
                            fontSize = 18.sp
                        )
                    },
                    text = {
                        LazyColumn(
                            modifier = Modifier.height(400.dp)
                        ) {
                            items(specialtiesList) { specialty ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedSpecialties = if (selectedSpecialties.contains(specialty)) {
                                                selectedSpecialties - specialty
                                            } else {
                                                selectedSpecialties + specialty
                                            }
                                        }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = selectedSpecialties.contains(specialty),
                                        onCheckedChange = { isChecked ->
                                            selectedSpecialties = if (isChecked) {
                                                selectedSpecialties + specialty
                                            } else {
                                                selectedSpecialties - specialty
                                            }
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = Green49,
                                            uncheckedColor = Gray828,
                                            checkmarkColor = Color.White
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = specialty,
                                        style = SourceSansRegular,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { specialtiesExpanded = false }) {
                            Text(
                                text = "Listo",
                                style = SourceSansBold,
                                color = Green49
                            )
                        }
                    }
                )
            }

            // Chips de especialidades seleccionadas
            if (selectedSpecialties.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    selectedSpecialties.forEach { specialty ->
                        Row(
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = Green37,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .background(
                                    color = Green49.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = specialty,
                                style = SourceSansRegular,
                                fontSize = 13.sp,
                                color = Gray828
                            )
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Eliminar $specialty",
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable(enabled = !isLoading) {
                                        selectedSpecialties = selectedSpecialties - specialty
                                    },
                                tint = Gray828
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Universidad con autocomplete (fila completa)
            val universitySuggestions by viewModel.universitySuggestions.collectAsState()
            var showUniversitySuggestions by remember { mutableStateOf(false) }

            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = university,
                    onValueChange = {
                        university = it
                        viewModel.searchUniversities(it)
                        showUniversitySuggestions = it.isNotEmpty()
                    },
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
                            text = "Universidad",
                            style = SourceSansRegular,
                            color = Gray828
                        )
                    },
                    singleLine = true,
                    enabled = !isLoading
                )

                // Dropdown de sugerencias
                if (showUniversitySuggestions && universitySuggestions.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 200.dp)
                        ) {
                            items(universitySuggestions.take(5)) { suggestion ->
                                Text(
                                    text = suggestion.name,
                                    style = SourceSansRegular,
                                    fontSize = 14.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            university = suggestion.name
                                            region = suggestion.region
                                            showUniversitySuggestions = false
                                            viewModel.clearUniversitySuggestions()
                                        }
                                        .padding(12.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Región de colegiatura y Año de graduación en fila
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = region,
                    onValueChange = { region = it },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Green37,
                        unfocusedIndicatorColor = GrayE0
                    ),
                    placeholder = {
                        Text(
                            text = "Región",
                            style = SourceSansRegular,
                            color = Gray828
                        )
                    },
                    singleLine = true,
                    enabled = !isLoading
                )

                OutlinedTextField(
                    value = graduationYear,
                    onValueChange = { graduationYear = it },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Green37,
                        unfocusedIndicatorColor = GrayE0
                    ),
                    placeholder = {
                        Text(
                            text = "Año de graduación",
                            style = SourceSansRegular,
                            color = Gray828
                        )
                    },
                    singleLine = true,
                    enabled = !isLoading
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Documentos
            Text(
                text = "Documentos",
                style = SourceSansBold,
                fontSize = 16.sp,
                color = Gray828,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Grid 2x2 para los documentos
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Primera fila: Licencia y Diploma
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Licencia
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp)
                            .clickable(enabled = !isLoading) {
                                licenseLauncher.launch("application/pdf")
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (licenseFile != null) Green49.copy(alpha = 0.1f) else GrayE0.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (licenseFile != null) Icons.Filled.Description else Icons.Outlined.Description,
                                contentDescription = "Licencia",
                                modifier = Modifier.size(40.dp),
                                tint = if (licenseFile != null) Green49 else Gray828
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Licencia",
                                style = SourceSansRegular,
                                fontSize = 13.sp,
                                color = Gray828,
                                textAlign = TextAlign.Center
                            )
                            if (licenseFile != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Eliminar",
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable(enabled = !isLoading) {
                                            licenseFile = null
                                        },
                                    tint = Gray828
                                )
                            }
                        }
                    }

                    // Diploma
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp)
                            .clickable(enabled = !isLoading) {
                                diplomaLauncher.launch("application/pdf")
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (diplomaFile != null) Green49.copy(alpha = 0.1f) else GrayE0.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (diplomaFile != null) Icons.Filled.Description else Icons.Outlined.Description,
                                contentDescription = "Diploma",
                                modifier = Modifier.size(40.dp),
                                tint = if (diplomaFile != null) Green49 else Gray828
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Diploma",
                                style = SourceSansRegular,
                                fontSize = 13.sp,
                                color = Gray828,
                                textAlign = TextAlign.Center
                            )
                            if (diplomaFile != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Eliminar",
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable(enabled = !isLoading) {
                                            diplomaFile = null
                                        },
                                    tint = Gray828
                                )
                            }
                        }
                    }
                }

                // Segunda fila: DNI y Certificaciones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // DNI
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp)
                            .clickable(enabled = !isLoading) {
                                dniLauncher.launch("*/*")
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (dniFile != null) Green49.copy(alpha = 0.1f) else GrayE0.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (dniFile != null) Icons.Filled.Description else Icons.Outlined.Description,
                                contentDescription = "DNI",
                                modifier = Modifier.size(40.dp),
                                tint = if (dniFile != null) Green49 else Gray828
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "DNI",
                                style = SourceSansRegular,
                                fontSize = 13.sp,
                                color = Gray828,
                                textAlign = TextAlign.Center
                            )
                            if (dniFile != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Eliminar",
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable(enabled = !isLoading) {
                                            dniFile = null
                                        },
                                    tint = Gray828
                                )
                            }
                        }
                    }

                    // Certificaciones
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp)
                            .clickable(enabled = !isLoading) {
                                certificationsLauncher.launch("application/pdf")
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (certificationsFile != null) Green49.copy(alpha = 0.1f) else GrayE0.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (certificationsFile != null) Icons.Filled.Description else Icons.Outlined.Description,
                                contentDescription = "Certificaciones",
                                modifier = Modifier.size(40.dp),
                                tint = if (certificationsFile != null) Green49 else Gray828
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Certificaciones",
                                style = SourceSansRegular,
                                fontSize = 13.sp,
                                color = Gray828,
                                textAlign = TextAlign.Center
                            )
                            if (certificationsFile != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Eliminar",
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable(enabled = !isLoading) {
                                            certificationsFile = null
                                        },
                                    tint = Gray828
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = acceptedTerms,
                onCheckedChange = { acceptedTerms = it },
                enabled = !isLoading,
                colors = CheckboxDefaults.colors(
                    checkedColor = Green49,
                    uncheckedColor = Gray828,
                    checkmarkColor = Color.White
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Acepto la política de privacidad",
                style = SourceSansRegular,
                fontSize = 14.sp,
                color = Gray828
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Register button
        Button(
            onClick = {
                if (userType == UserType.GENERAL) {
                    // Register general user
                    viewModel.registerGeneralUser(
                        firstName = firstName,
                        lastName = lastName,
                        acceptsPrivacyPolicy = acceptedTerms
                    )
                } else if (userType == UserType.PSYCHOLOGIST) {
                    // Register psychologist
                    val specialtiesString = selectedSpecialties.joinToString(",")
                    val certList = if (certificationsFile != null) listOf(certificationsFile!!) else null

                    viewModel.registerPsychologist(
                        firstName = firstName,
                        lastName = lastName,
                        professionalLicense = licenseNumber,
                        yearsOfExperience = yearsOfExperience.toIntOrNull() ?: 0,
                        collegiateRegion = region,
                        university = university,
                        graduationYear = graduationYear.toIntOrNull() ?: 2020,
                        acceptsPrivacyPolicy = acceptedTerms,
                        licenseDocumentUri = licenseFile ?: "",
                        diplomaDocumentUri = diplomaFile ?: "",
                        dniDocumentUri = dniFile ?: "",
                        specialties = if (specialtiesString.isNotEmpty()) specialtiesString else null,
                        certificationDocumentUris = certList
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Green49
            ),
            enabled = !isLoading &&
                    firstName.isNotEmpty() &&
                    lastName.isNotEmpty() &&
                    email.isNotEmpty() &&
                    (oauthEmail != null || (password.isNotEmpty() && confirmPassword.isNotEmpty())) &&
                    acceptedTerms &&
                    (userType != UserType.PSYCHOLOGIST ||
                        (licenseFile != null && diplomaFile != null && dniFile != null &&
                         licenseNumber.isNotEmpty() && region.isNotEmpty() &&
                         university.isNotEmpty() && yearsOfExperience.isNotEmpty() &&
                         graduationYear.isNotEmpty()))
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White
                )
            } else {
                Text(
                    text = "Registrar",
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

        Spacer(modifier = Modifier.height(6.dp))

        // Login link
        TextButton(onClick = onNavigateToLogin) {
            Text(
                text = "Ya tienes cuenta? ",
                color = Color.Gray
            )
            Text(
                text = "Iniciar Sesión",
                color = Green49,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(13.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    val context = LocalContext.current
    val viewModel = PresentationModule.getRegisterViewModel(context)
    SoftFocusTheme {
        RegisterScreen(
            viewModel = viewModel,
            oauthEmail = null,
            oauthFullName = null,
            oauthTempToken = null,
            onRegisterSuccess = { _ -> },
            onAutoLogin = { _ -> },
            onNavigateToLogin = {},
            onNavigateToPendingVerification = {}
        )
    }
}
