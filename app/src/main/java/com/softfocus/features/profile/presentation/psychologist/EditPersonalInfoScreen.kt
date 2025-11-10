package com.softfocus.features.profile.presentation.psychologist

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.BitmapFactory
import androidx.hilt.navigation.compose.hiltViewModel
import com.softfocus.features.profile.presentation.ProfileViewModel
import com.softfocus.ui.theme.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditPersonalInfoScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
    psychologistViewModel: PsychologistProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val psychProfile by psychologistViewModel.profile.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Cargar el perfil profesional completo al iniciar
    LaunchedEffect(Unit) {
        psychologistViewModel.loadProfile()
    }

    // Estados iniciales - Personal
    var initialFirstName by remember { mutableStateOf("") }
    var initialLastName by remember { mutableStateOf("") }
    var initialDateOfBirth by remember { mutableStateOf("") }

    // Estados iniciales - Profesional
    var initialProfessionalBio by remember { mutableStateOf("") }
    var initialBusinessName by remember { mutableStateOf("") }
    var initialBusinessAddress by remember { mutableStateOf("") }
    var initialBankAccount by remember { mutableStateOf("") }
    var initialPaymentMethods by remember { mutableStateOf("") }
    var initialMaxPatientsCapacity by remember { mutableStateOf("") }
    var initialLanguages by remember { mutableStateOf<Set<String>>(emptySet()) }
    var initialTargetAudience by remember { mutableStateOf<Set<String>>(emptySet()) }
    var initialIsAcceptingNewPatients by remember { mutableStateOf(true) }
    var initialIsProfileVisibleInDirectory by remember { mutableStateOf(true) }
    var initialAllowsDirectMessages by remember { mutableStateOf(true) }

    // Estados actuales - Personal
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    // Estados actuales - Profesional
    var professionalBio by remember { mutableStateOf("") }
    var businessName by remember { mutableStateOf("") }
    var businessAddress by remember { mutableStateOf("") }
    var bankAccount by remember { mutableStateOf("") }
    var paymentMethods by remember { mutableStateOf("") }
    var maxPatientsCapacity by remember { mutableStateOf("") }
    var selectedLanguages by remember { mutableStateOf<Set<String>>(emptySet()) }
    var selectedTargetAudience by remember { mutableStateOf<Set<String>>(emptySet()) }
    var isAcceptingNewPatients by remember { mutableStateOf(true) }
    var isProfileVisibleInDirectory by remember { mutableStateOf(true) }
    var allowsDirectMessages by remember { mutableStateOf(true) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profileImageUri = uri
    }

    // Cargar datos del usuario (personal)
    LaunchedEffect(user) {
        user?.let {
            // Guardar valores iniciales
            initialFirstName = it.firstName ?: ""
            initialLastName = it.lastName ?: ""
            initialDateOfBirth = it.dateOfBirth ?: ""

            // Copiar a estados actuales
            firstName = initialFirstName
            lastName = initialLastName
            dateOfBirth = initialDateOfBirth
        }
    }

    // Cargar datos profesionales
    LaunchedEffect(psychProfile) {
        psychProfile?.let { profile ->
            // Guardar valores iniciales profesionales
            initialProfessionalBio = profile.professionalBio ?: ""
            initialBusinessName = profile.businessName ?: ""
            initialBusinessAddress = profile.businessAddress ?: ""
            initialBankAccount = profile.bankAccount ?: ""
            initialPaymentMethods = profile.paymentMethods ?: ""
            initialMaxPatientsCapacity = profile.maxPatientsCapacity?.toString() ?: ""
            initialLanguages = profile.languages?.toSet() ?: emptySet()
            initialTargetAudience = profile.targetAudience?.toSet() ?: emptySet()
            initialIsAcceptingNewPatients = profile.isAcceptingNewPatients
            initialIsProfileVisibleInDirectory = profile.isProfileVisibleInDirectory
            initialAllowsDirectMessages = profile.allowsDirectMessages

            // Copiar a estados actuales
            professionalBio = initialProfessionalBio
            businessName = initialBusinessName
            businessAddress = initialBusinessAddress
            bankAccount = initialBankAccount
            paymentMethods = initialPaymentMethods
            maxPatientsCapacity = initialMaxPatientsCapacity
            selectedLanguages = initialLanguages
            selectedTargetAudience = initialTargetAudience
            isAcceptingNewPatients = initialIsAcceptingNewPatients
            isProfileVisibleInDirectory = initialIsProfileVisibleInDirectory
            allowsDirectMessages = initialAllowsDirectMessages
        }
    }

    // Detectar si hay cambios
    val hasChanges = remember(
        firstName, lastName, dateOfBirth, profileImageUri,
        professionalBio, businessName, businessAddress, bankAccount, paymentMethods,
        maxPatientsCapacity, selectedLanguages, selectedTargetAudience,
        isAcceptingNewPatients, isProfileVisibleInDirectory, allowsDirectMessages
    ) {
        // Cambios personales
        firstName != initialFirstName ||
        lastName != initialLastName ||
        dateOfBirth != initialDateOfBirth ||
        profileImageUri != null ||
        // Cambios profesionales
        professionalBio != initialProfessionalBio ||
        businessName != initialBusinessName ||
        businessAddress != initialBusinessAddress ||
        bankAccount != initialBankAccount ||
        paymentMethods != initialPaymentMethods ||
        maxPatientsCapacity != initialMaxPatientsCapacity ||
        selectedLanguages != initialLanguages ||
        selectedTargetAudience != initialTargetAudience ||
        isAcceptingNewPatients != initialIsAcceptingNewPatients ||
        isProfileVisibleInDirectory != initialIsProfileVisibleInDirectory ||
        allowsDirectMessages != initialAllowsDirectMessages
    }

    // Mostrar mensaje de éxito
    LaunchedEffect(uiState) {
        if (uiState is com.softfocus.features.profile.presentation.ProfileUiState.UpdateSuccess) {
            // Reload psychologist profile to get updated data (including image)
            psychologistViewModel.loadProfile()

            snackbarHostState.showSnackbar(
                message = "Perfil actualizado correctamente ✓",
                duration = SnackbarDuration.Short
            )
        } else if (uiState is com.softfocus.features.profile.presentation.ProfileUiState.Error) {
            snackbarHostState.showSnackbar(
                message = (uiState as com.softfocus.features.profile.presentation.ProfileUiState.Error).message,
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Editar información Personal",
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
                    containerColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 48.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Image with Camera Button
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                when {
                    // Si hay una imagen nueva seleccionada localmente
                    profileImageUri != null -> {
                        val bitmap = remember(profileImageUri) {
                            context.contentResolver.openInputStream(profileImageUri!!)?.use { stream ->
                                BitmapFactory.decodeStream(stream)
                            }
                        }

                        bitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = "Foto de perfil",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .clickable { imagePickerLauncher.launch("image/*") },
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    // Si el usuario ya tiene una imagen guardada en el servidor
                    user?.profileImageUrl != null -> {
                        ProfileImageFromUrl(
                            imageUrl = user?.profileImageUrl!!,
                            onClick = { imagePickerLauncher.launch("image/*") }
                        )
                    }
                    // Placeholder si no hay imagen
                    else -> {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(GreenEB2)
                                .clickable { imagePickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(50.dp),
                                tint = White
                            )
                        }
                    }
                }

                // Camera icon button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(Green29)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Cambiar foto",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Nombre y Apellido en la misma fila
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PsychologistEditableField(
                    label = "Nombre",
                    value = firstName,
                    onValueChange = { firstName = it },
                    modifier = Modifier.weight(1f)
                )
                PsychologistEditableField(
                    label = "Apellido",
                    value = lastName,
                    onValueChange = { lastName = it },
                    modifier = Modifier.weight(1f)
                )
            }

            // Fecha de Cumpleaños
            PsychologistDateOfBirthPicker(
                selectedDate = dateOfBirth,
                onDateSelected = { dateOfBirth = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Contact Section
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Contacto",
                    style = SourceSansRegular,
                    fontSize = 16.sp,
                    color = Yellow7E
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Email (read-only)
                ContactItem(
                    icon = Icons.Default.Email,
                    text = user?.email ?: ""
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Phone (read-only)
                ContactItem(
                    icon = Icons.Default.Phone,
                    text = user?.phone ?: ""
                )

                Spacer(modifier = Modifier.height(8.dp))

                // WhatsApp (muestra el mismo que teléfono)
                ContactItem(
                    icon = Icons.Default.Chat,
                    text = user?.phone ?: "Whatsapp"
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ========== SECCIÓN PROFESIONAL ==========
            Text(
                text = "Información Profesional",
                style = CrimsonSemiBold,
                fontSize = 20.sp,
                color = Green37
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Professional Bio (max 1000 chars)
            PsychologistEditableField(
                label = "Descripción Profesional",
                value = professionalBio,
                onValueChange = { if (it.length <= 1000) professionalBio = it },
                minLines = 8,
                isExpandable = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Languages Selection
            ChipSelectionField(
                label = "Idiomas",
                options = listOf("Español", "Inglés", "Francés", "Portugués", "Alemán", "Italiano", "Chino", "Japonés"),
                selectedOptions = selectedLanguages,
                onSelectionChange = { selectedLanguages = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Target Audience Selection
            ChipSelectionField(
                label = "Público Objetivo",
                options = listOf("Adultos", "Adolescentes", "Niños", "Parejas", "Familias", "Tercera Edad"),
                selectedOptions = selectedTargetAudience,
                onSelectionChange = { selectedTargetAudience = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Business Info (max 100 chars)
            PsychologistEditableField(
                label = "Nombre del Negocio",
                value = businessName,
                onValueChange = { if (it.length <= 100) businessName = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PsychologistEditableField(
                label = "Dirección del Consultorio",
                value = businessAddress,
                onValueChange = { if (it.length <= 200) businessAddress = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Info (max 50 chars)
            PsychologistEditableField(
                label = "Cuenta Bancaria",
                value = bankAccount,
                onValueChange = { if (it.length <= 50) bankAccount = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PsychologistEditableField(
                label = "Métodos de Pago",
                value = paymentMethods,
                onValueChange = { if (it.length <= 200) paymentMethods = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Max Patients Capacity (1-500)
            PsychologistEditableField(
                label = "Capacidad Máxima de Pacientes",
                value = maxPatientsCapacity,
                onValueChange = { value ->
                    if (value.isEmpty() || (value.all { it.isDigit() } && value.toIntOrNull()?.let { it in 1..500 } == true)) {
                        maxPatientsCapacity = value
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Checkboxes Section
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isAcceptingNewPatients,
                        onCheckedChange = { isAcceptingNewPatients = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Green29,
                            uncheckedColor = Gray828
                        )
                    )
                    Text(
                        text = "Aceptando nuevos pacientes",
                        style = SourceSansRegular,
                        fontSize = 16.sp,
                        color = Black
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isProfileVisibleInDirectory,
                        onCheckedChange = { isProfileVisibleInDirectory = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Green29,
                            uncheckedColor = Gray828
                        )
                    )
                    Text(
                        text = "Perfil visible en directorio",
                        style = SourceSansRegular,
                        fontSize = 16.sp,
                        color = Black
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = allowsDirectMessages,
                        onCheckedChange = { allowsDirectMessages = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Green29,
                            uncheckedColor = Gray828
                        )
                    )
                    Text(
                        text = "Permitir mensajes directos",
                        style = SourceSansRegular,
                        fontSize = 16.sp,
                        color = Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Cancel Button
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Black,
                        containerColor = GreenEB2
                    ),
                    border = null,
                    contentPadding = PaddingValues(vertical = 10.dp, horizontal = 16.dp)
                ) {
                    Text(
                        text = "Cancelar",
                        style = SourceSansRegular,
                        fontSize = 14.sp
                    )
                }

                // Save Button
                Button(
                    onClick = {
                        scope.launch {
                            // Convertir el género al formato correcto del backend
                            val genderBackend = when (user?.gender) {
                                "Femenino" -> "Female"
                                "Masculino" -> "Male"
                                "Otro" -> "Other"
                                "Prefiero no decir" -> "PreferNotToSay"
                                else -> user?.gender
                            }

                            // Guardar información personal si hay cambios
                            val hasPersonalChanges = firstName != initialFirstName ||
                                lastName != initialLastName ||
                                dateOfBirth != initialDateOfBirth ||
                                profileImageUri != null

                            // Guardar información profesional si hay cambios
                            val hasProfessionalChanges = professionalBio != initialProfessionalBio ||
                                businessName != initialBusinessName ||
                                businessAddress != initialBusinessAddress ||
                                bankAccount != initialBankAccount ||
                                paymentMethods != initialPaymentMethods ||
                                maxPatientsCapacity != initialMaxPatientsCapacity ||
                                selectedLanguages != initialLanguages ||
                                selectedTargetAudience != initialTargetAudience ||
                                isAcceptingNewPatients != initialIsAcceptingNewPatients ||
                                isProfileVisibleInDirectory != initialIsProfileVisibleInDirectory ||
                                allowsDirectMessages != initialAllowsDirectMessages

                            android.util.Log.d("EditPersonalInfo", "Personal changes: $hasPersonalChanges")
                            android.util.Log.d("EditPersonalInfo", "Professional changes: $hasProfessionalChanges")
                            android.util.Log.d("EditPersonalInfo", "Professional bio changed: ${professionalBio != initialProfessionalBio} ('$professionalBio' vs '$initialProfessionalBio')")
                            android.util.Log.d("EditPersonalInfo", "Languages changed: ${selectedLanguages != initialLanguages} ($selectedLanguages vs $initialLanguages)")

                            if (hasPersonalChanges) {
                                android.util.Log.d("EditPersonalInfo", "Calling updateProfile...")
                                viewModel.updateProfile(
                                    firstName = firstName.takeIf { it.isNotBlank() },
                                    lastName = lastName.takeIf { it.isNotBlank() },
                                    dateOfBirth = dateOfBirth.takeIf { it.isNotBlank() },
                                    gender = genderBackend,
                                    phone = user?.phone,
                                    bio = user?.bio,
                                    country = user?.country,
                                    city = user?.city,
                                    interests = user?.interests,
                                    mentalHealthGoals = user?.mentalHealthGoals,
                                    emailNotifications = user?.emailNotifications,
                                    pushNotifications = user?.pushNotifications,
                                    isProfilePublic = user?.isProfilePublic,
                                    profileImageUri = profileImageUri
                                )
                            }

                            if (hasProfessionalChanges) {
                                android.util.Log.d("EditPersonalInfo", "Calling updateProfessionalProfile...")
                                viewModel.updateProfessionalProfile(
                                    professionalBio = professionalBio.takeIf { it.isNotBlank() },
                                    isAcceptingNewPatients = isAcceptingNewPatients,
                                    maxPatientsCapacity = maxPatientsCapacity.toIntOrNull(),
                                    targetAudience = selectedTargetAudience.toList().takeIf { it.isNotEmpty() },
                                    languages = selectedLanguages.toList().takeIf { it.isNotEmpty() },
                                    businessName = businessName.takeIf { it.isNotBlank() },
                                    businessAddress = businessAddress.takeIf { it.isNotBlank() },
                                    bankAccount = bankAccount.takeIf { it.isNotBlank() },
                                    paymentMethods = paymentMethods.takeIf { it.isNotBlank() },
                                    isProfileVisibleInDirectory = isProfileVisibleInDirectory,
                                    allowsDirectMessages = allowsDirectMessages
                                )
                            }
                        }
                    },
                    enabled = hasChanges,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = YellowCB9C,
                        contentColor = Black,
                        disabledContainerColor = GrayD9,
                        disabledContentColor = Gray767
                    ),
                    contentPadding = PaddingValues(vertical = 10.dp, horizontal = 16.dp)
                ) {
                    Text(
                        text = "Guardar",
                        style = SourceSansRegular,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PsychologistEditableField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    isExpandable: Boolean = false
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = SourceSansRegular,
            fontSize = 16.sp,
            color = Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = GreenEB2,
                focusedContainerColor = GreenEB2,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Green37
            ),
            textStyle = SourceSansRegular,
            minLines = minLines,
            maxLines = if (isExpandable) Int.MAX_VALUE else 1,
            singleLine = !isExpandable && minLines == 1
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PsychologistDateOfBirthPicker(
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    val datePickerState = rememberDatePickerState()
    var showDialog by remember { mutableStateOf(false) }

    val displayDate = remember(selectedDate) {
        if (selectedDate.isNotBlank()) {
            try {
                val date = LocalDate.parse(selectedDate, DateTimeFormatter.ISO_DATE_TIME)
                date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault()))
            } catch (e: Exception) {
                "Seleccionar fecha"
            }
        } else {
            "Seleccionar fecha"
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Fecha de Cumpleaños",
            style = SourceSansRegular,
            fontSize = 16.sp,
            color = Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = displayDate,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { showDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Seleccionar fecha",
                        tint = Black
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = GreenEB2,
                focusedContainerColor = GreenEB2,
                disabledContainerColor = GreenEB2,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Green37
            ),
            shape = RoundedCornerShape(12.dp),
            textStyle = SourceSansRegular
        )
    }

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                            val isoDate = date.atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME) + "Z"
                            onDateSelected(isoDate)
                        }
                        showDialog = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun ContactItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Yellow7E,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = text,
            style = SourceSansRegular,
            fontSize = 14.sp,
            color = Yellow7E
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipSelectionField(
    label: String,
    options: List<String>,
    selectedOptions: Set<String>,
    onSelectionChange: (Set<String>) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = SourceSansRegular,
            fontSize = 16.sp,
            color = Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                val isSelected = selectedOptions.contains(option)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        val newSelection = if (isSelected) {
                            selectedOptions - option
                        } else {
                            selectedOptions + option
                        }
                        onSelectionChange(newSelection)
                    },
                    label = { Text(option, fontSize = 14.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = GreenEB2,
                        selectedContainerColor = Green29,
                        labelColor = Black,
                        selectedLabelColor = White
                    )
                )
            }
        }
    }
}

@Composable
fun ProfileImageFromUrl(
    imageUrl: String,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    LaunchedEffect(imageUrl) {
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val url = java.net.URL(imageUrl)
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .clickable(onClick = onClick),
            contentScale = ContentScale.Crop
        )
    } ?: Box(
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(GreenEB2)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(50.dp),
            tint = White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EditPersonalInfoScreenPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .padding(horizontal = 48.dp, vertical = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile Image with Camera Button
        Box(
            modifier = Modifier.size(100.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(GreenEB2),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp),
                    tint = White
                )
            }

            // Camera icon button
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(Green29),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Cambiar foto",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Nombre y Apellido
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PsychologistEditableField(
                label = "Nombre",
                value = "Patricia",
                onValueChange = {},
                modifier = Modifier.weight(1f)
            )
            PsychologistEditableField(
                label = "Apellido",
                value = "Sanchez",
                onValueChange = {},
                modifier = Modifier.weight(1f)
            )
        }

        // Fecha de Cumpleaños
        PsychologistDateOfBirthPicker(
            selectedDate = "1984-03-20T00:00:00Z",
            onDateSelected = {}
        )

        // Description Field
        PsychologistEditableField(
            label = "Descripción",
            value = "Psicóloga clínica especializada en terapia cognitivo-conductual...",
            onValueChange = {},
            minLines = 6,
            modifier = Modifier.height(150.dp)
        )

        // Contact Section
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Contacto",
                style = SourceSansRegular,
                fontSize = 16.sp,
                color = Yellow7E
            )
            Spacer(modifier = Modifier.height(12.dp))

            ContactItem(
                icon = Icons.Default.Email,
                text = "psychologist1@test.com"
            )

            Spacer(modifier = Modifier.height(8.dp))

            ContactItem(
                icon = Icons.Default.Phone,
                text = "+51 987 654 321"
            )

            Spacer(modifier = Modifier.height(8.dp))

            ContactItem(
                icon = Icons.Default.Chat,
                text = "+51 987 654 321"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Black,
                    containerColor = GreenEB2
                ),
                border = null,
                contentPadding = PaddingValues(vertical = 10.dp, horizontal = 16.dp)
            ) {
                Text(
                    text = "Cancelar",
                    style = SourceSansRegular,
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = { },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = YellowCB9C,
                    contentColor = Black
                ),
                contentPadding = PaddingValues(vertical = 10.dp, horizontal = 16.dp)
            ) {
                Text(
                    text = "Guardar",
                    style = SourceSansRegular,
                    fontSize = 14.sp
                )
            }
        }
    }
}
