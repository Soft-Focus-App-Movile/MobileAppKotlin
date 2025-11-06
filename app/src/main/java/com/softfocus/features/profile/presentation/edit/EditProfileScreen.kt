package com.softfocus.features.profile.presentation.edit

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.Image
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Estados iniciales
    var initialFirstName by remember { mutableStateOf("") }
    var initialLastName by remember { mutableStateOf("") }
    var initialBio by remember { mutableStateOf("") }
    var initialDateOfBirth by remember { mutableStateOf("") }
    var initialCountry by remember { mutableStateOf("") }
    var initialCity by remember { mutableStateOf("") }
    var initialGender by remember { mutableStateOf("") }
    var initialPhone by remember { mutableStateOf("") }
    var initialInterests by remember { mutableStateOf<List<String>>(emptyList()) }
    var initialGoals by remember { mutableStateOf<List<String>>(emptyList()) }

    // Estados actuales
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedInterests by remember { mutableStateOf<Set<String>>(emptySet()) }
    var selectedGoals by remember { mutableStateOf<Set<String>>(emptySet()) }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profileImageUri = uri
    }

    // Cargar datos del usuario
    LaunchedEffect(user) {
        user?.let {
            // Guardar valores iniciales
            initialFirstName = it.firstName ?: ""
            initialLastName = it.lastName ?: ""
            initialBio = it.bio ?: ""
            initialDateOfBirth = it.dateOfBirth ?: ""
            initialCountry = it.country ?: ""
            initialCity = it.city ?: ""
            initialGender = when (it.gender) {
                "Femenino" -> "Female"
                "Masculino" -> "Male"
                "Otro" -> "Other"
                "Prefiero no decir" -> "PreferNotToSay"
                else -> it.gender ?: ""
            }
            initialPhone = it.phone ?: ""
            initialInterests = it.interests ?: emptyList()
            initialGoals = it.mentalHealthGoals ?: emptyList()

            // Copiar a estados actuales
            firstName = initialFirstName
            lastName = initialLastName
            bio = initialBio
            dateOfBirth = initialDateOfBirth
            country = initialCountry
            city = initialCity
            gender = initialGender
            phone = initialPhone
            selectedInterests = initialInterests.toSet()
            selectedGoals = initialGoals.toSet()
        }
    }

    // Detectar si hay cambios
    val hasChanges = remember(firstName, lastName, bio, dateOfBirth, country, city, gender, phone, selectedInterests, selectedGoals, profileImageUri) {
        firstName != initialFirstName ||
        lastName != initialLastName ||
        bio != initialBio ||
        dateOfBirth != initialDateOfBirth ||
        country != initialCountry ||
        city != initialCity ||
        gender != initialGender ||
        phone != initialPhone ||
        selectedInterests != initialInterests.toSet() ||
        selectedGoals != initialGoals.toSet() ||
        profileImageUri != null
    }

    // Mostrar mensaje de éxito
    LaunchedEffect(uiState) {
        if (uiState is com.softfocus.features.profile.presentation.ProfileUiState.UpdateSuccess) {
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
                        text = "Editar Perfil",
                        style = CrimsonSemiBold,
                        fontSize = 20.sp,
                        color = Green37,
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
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 48.dp, vertical = 24.dp)
        ) {
            // Profile Image with Camera Button
            Box(
                modifier = Modifier
                    .size(150.dp)
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
                                    .size(150.dp)
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
                            onClick = { imagePickerLauncher.launch("image/*") },
                            size = 150.dp
                        )
                    }
                    // Placeholder si no hay imagen
                    else -> {
                        Box(
                            modifier = Modifier
                                .size(150.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFC5D9A4))
                                .clickable { imagePickerLauncher.launch("image/*") }
                        )
                    }
                }

                // Camera icon button
                Box(
                    modifier = Modifier
                        .size(40.dp)
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
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nombre y Apellido en la misma fila
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EditableTextField(
                    label = "Nombre",
                    value = firstName,
                    onValueChange = { firstName = it },
                    isEdited = firstName != initialFirstName,
                    modifier = Modifier.weight(1f)
                )
                EditableTextField(
                    label = "Apellido",
                    value = lastName,
                    onValueChange = { lastName = it },
                    isEdited = lastName != initialLastName,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bio
            EditableTextField(
                label = "Descripción",
                value = bio,
                onValueChange = { bio = it },
                isEdited = bio != initialBio,
                minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Fecha de Cumpleaños
            DateOfBirthPicker(
                selectedDate = dateOfBirth,
                onDateSelected = { dateOfBirth = it },
                isEdited = dateOfBirth != initialDateOfBirth
            )

            Spacer(modifier = Modifier.height(16.dp))

            // País y Ciudad en la misma fila
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EditableTextField(
                    label = "País",
                    value = country,
                    onValueChange = { country = it },
                    isEdited = country != initialCountry,
                    modifier = Modifier.weight(1f)
                )
                EditableTextField(
                    label = "Ciudad",
                    value = city,
                    onValueChange = { city = it },
                    isEdited = city != initialCity,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Género
            GenderDropdown(
                selectedGender = gender,
                onGenderSelected = { gender = it },
                isEdited = gender != initialGender
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Teléfono
            EditableTextField(
                label = "Número",
                value = phone,
                onValueChange = { phone = it },
                isEdited = phone != initialPhone
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Intereses
            ChipSelectionField(
                label = "Intereses",
                options = listOf(
                    "Películas", "Música", "Deportes", "Lectura", "Viajes",
                    "Cocina", "Arte", "Fotografía", "Gaming", "Naturaleza",
                    "Tecnología", "Mascotas"
                ),
                selectedOptions = selectedInterests,
                onSelectionChange = { selectedInterests = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Objetivos
            ChipSelectionField(
                label = "Objetivos",
                options = listOf(
                    "Reducir ansiedad", "Manejar estrés", "Mejorar sueño",
                    "Aumentar autoestima", "Controlar emociones", "Superar depresión",
                    "Mejorar relaciones", "Aumentar confianza", "Encontrar paz mental",
                    "Desarrollo personal"
                ),
                selectedOptions = selectedGoals,
                onSelectionChange = { selectedGoals = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Gray828
                    )
                ) {
                    Text("Cancelar", modifier = Modifier.padding(vertical = 8.dp))
                }

                Button(
                    onClick = {
                        scope.launch {
                            viewModel.updateProfile(
                                firstName = firstName.takeIf { it.isNotBlank() },
                                lastName = lastName.takeIf { it.isNotBlank() },
                                dateOfBirth = dateOfBirth.takeIf { it.isNotBlank() },
                                gender = gender.takeIf { it.isNotBlank() },
                                phone = phone.takeIf { it.isNotBlank() },
                                bio = bio.takeIf { it.isNotBlank() },
                                country = country.takeIf { it.isNotBlank() },
                                city = city.takeIf { it.isNotBlank() },
                                interests = selectedInterests.toList().takeIf { it.isNotEmpty() },
                                mentalHealthGoals = selectedGoals.toList().takeIf { it.isNotEmpty() },
                                emailNotifications = user?.emailNotifications,
                                pushNotifications = user?.pushNotifications,
                                isProfilePublic = user?.isProfilePublic,
                                profileImageUri = profileImageUri
                            )
                        }
                    },
                    enabled = hasChanges,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Green29,
                        disabledContainerColor = GrayB2
                    )
                ) {
                    Text("Guardar", modifier = Modifier.padding(vertical = 8.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun EditableTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isEdited: Boolean,
    modifier: Modifier = Modifier,
    minLines: Int = 1
) {
    val textColor = if (isEdited) Green37 else GrayB2
    val borderColor = if (isEdited) Green37 else GrayB2

    Column(modifier = modifier) {
        Text(
            text = label,
            style = SourceSansRegular,
            fontSize = 14.sp,
            color = Gray828,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            minLines = minLines,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFEBEFE5),
                focusedContainerColor = Color(0xFFEBEFE5),
                unfocusedBorderColor = borderColor,
                focusedBorderColor = Green29,
                unfocusedTextColor = textColor,
                focusedTextColor = Green29
            ),
            shape = RoundedCornerShape(8.dp),
            textStyle = SourceSansRegular.copy(fontSize = 16.sp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderDropdown(
    selectedGender: String,
    onGenderSelected: (String) -> Unit,
    isEdited: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    val genderOptions = listOf("Femenino", "Masculino", "Otro", "Prefiero no decir")
    val displayValue = when (selectedGender) {
        "Female" -> "Femenino"
        "Male" -> "Masculino"
        "Other" -> "Otro"
        "PreferNotToSay" -> "Prefiero no decir"
        else -> selectedGender.ifBlank { "Seleccionar" }
    }

    val textColor = if (isEdited) Green37 else GrayB2
    val borderColor = if (isEdited) Green37 else GrayB2

    Column {
        Text(
            text = "Género",
            style = SourceSansRegular,
            fontSize = 14.sp,
            color = Gray828,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = displayValue,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = if (isEdited) Green37 else GrayB2
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFEBEFE5),
                    focusedContainerColor = Color(0xFFEBEFE5),
                    disabledContainerColor = Color(0xFFEBEFE5),
                    unfocusedBorderColor = borderColor,
                    focusedBorderColor = Green29,
                    unfocusedTextColor = textColor,
                    focusedTextColor = Green29
                ),
                shape = RoundedCornerShape(8.dp),
                textStyle = SourceSansRegular.copy(fontSize = 16.sp)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                genderOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            val backendValue = when (option) {
                                "Femenino" -> "Female"
                                "Masculino" -> "Male"
                                "Otro" -> "Other"
                                "Prefiero no decir" -> "PreferNotToSay"
                                else -> option
                            }
                            onGenderSelected(backendValue)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateOfBirthPicker(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    isEdited: Boolean
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

    val textColor = if (isEdited) Green37 else GrayB2
    val borderColor = if (isEdited) Green37 else GrayB2

    Column {
        Text(
            text = "Fecha de Cumpleaños",
            style = SourceSansRegular,
            fontSize = 14.sp,
            color = Gray828,
            modifier = Modifier.padding(bottom = 4.dp)
        )
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
                        tint = if (isEdited) Green37 else GrayB2
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFEBEFE5),
                focusedContainerColor = Color(0xFFEBEFE5),
                disabledContainerColor = Color(0xFFEBEFE5),
                unfocusedBorderColor = borderColor,
                focusedBorderColor = Green29,
                unfocusedTextColor = textColor,
                focusedTextColor = Green29
            ),
            shape = RoundedCornerShape(8.dp),
            textStyle = SourceSansRegular.copy(fontSize = 16.sp)
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

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChipSelectionField(
    label: String,
    options: List<String>,
    selectedOptions: Set<String>,
    onSelectionChange: (Set<String>) -> Unit
) {
    Column {
        Text(
            text = label,
            style = SourceSansRegular,
            fontSize = 14.sp,
            color = Gray828,
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
                        containerColor = Color(0xFFEBEFE5),
                        selectedContainerColor = Green29,
                        labelColor = Gray828,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun ProfileImageFromUrl(
    imageUrl: String,
    onClick: () -> Unit,
    size: androidx.compose.ui.unit.Dp = 100.dp
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
                .size(size)
                .clip(CircleShape)
                .clickable(onClick = onClick),
            contentScale = ContentScale.Crop
        )
    } ?: Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFFC5D9A4))
            .clickable(onClick = onClick)
    )
}

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Profile Image with Camera Button
        Box(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFC5D9A4))
            )

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

        Spacer(modifier = Modifier.height(24.dp))

        // Nombre y Apellido
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EditableTextField(
                label = "Nombre",
                value = "Laura",
                onValueChange = {},
                isEdited = false,
                modifier = Modifier.weight(1f)
            )
            EditableTextField(
                label = "Apellido",
                value = "Gomez",
                onValueChange = {},
                isEdited = true,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bio
        EditableTextField(
            label = "Descripción",
            value = "Me gusta la psicología y ayudar a las personas.",
            onValueChange = {},
            isEdited = false,
            minLines = 3
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Fecha de Cumpleaños
        DateOfBirthPicker(
            selectedDate = "2004-05-15T00:00:00Z",
            onDateSelected = {},
            isEdited = false
        )

        Spacer(modifier = Modifier.height(16.dp))

        // País y Ciudad
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EditableTextField(
                label = "País",
                value = "Peru",
                onValueChange = {},
                isEdited = false,
                modifier = Modifier.weight(1f)
            )
            EditableTextField(
                label = "Ciudad",
                value = "Lima",
                onValueChange = {},
                isEdited = false,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Género
        GenderDropdown(
            selectedGender = "Female",
            onGenderSelected = {},
            isEdited = false
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Teléfono
        EditableTextField(
            label = "Número",
            value = "+51987654321",
            onValueChange = {},
            isEdited = false
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Intereses
        ChipSelectionField(
            label = "Intereses",
            options = listOf(
                "Películas", "Música", "Deportes", "Lectura", "Viajes",
                "Cocina", "Arte", "Fotografía", "Gaming", "Naturaleza",
                "Tecnología", "Mascotas"
            ),
            selectedOptions = setOf("Películas", "Música"),
            onSelectionChange = {}
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Objetivos
        ChipSelectionField(
            label = "Objetivos",
            options = listOf(
                "Reducir ansiedad", "Manejar estrés", "Mejorar sueño",
                "Aumentar autoestima", "Controlar emociones", "Superar depresión",
                "Mejorar relaciones", "Aumentar confianza", "Encontrar paz mental",
                "Desarrollo personal"
            ),
            selectedOptions = setOf("Reducir ansiedad"),
            onSelectionChange = {}
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = {},
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cancelar")
            }

            Button(
                onClick = {},
                enabled = false,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green29,
                    disabledContainerColor = GrayB2
                )
            ) {
                Text("Guardar")
            }
        }
    }
}
