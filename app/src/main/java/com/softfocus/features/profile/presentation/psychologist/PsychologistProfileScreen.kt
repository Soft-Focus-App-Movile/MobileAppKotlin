package com.softfocus.features.profile.presentation.psychologist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.softfocus.R
import com.softfocus.ui.theme.*
import java.net.URL
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PsychologistProfileScreen(
    onNavigateToEditProfile: () -> Unit,
    onNavigateToInvitationCode: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToPlan: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToProfessionalData: () -> Unit,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit = {},
    viewModel: PsychologistProfileViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        when (uiState) {
            is PsychologistProfileUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is PsychologistProfileUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (uiState as PsychologistProfileUiState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            is PsychologistProfileUiState.Success -> {
                profile?.let { psychProfile ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))

                        // Profile Section - Image and Info side by side
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 0.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            // Profile Image
                            psychProfile.profileImageUrl?.let { imageUrl ->
                                AsyncImageLoader(
                                    imageUrl = imageUrl,
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape)
                                )
                            } ?: Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(GreenA3)
                            )

                            // User Info
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Name - Ajusta automáticamente el tamaño
                                Text(
                                    text = psychProfile.fullName,
                                    style = CrimsonSemiBold.copy(
                                        fontSize = if (psychProfile.fullName.length > 20) 20.sp else 28.sp
                                    ),
                                    color = Black,
                                    maxLines = 2,
                                    lineHeight = 28.sp
                                )

                                // Age
                                psychProfile.dateOfBirth?.let { dob ->
                                    val age = calculateAge(dob)
                                    if (age != null) {
                                        Text(
                                            text = "$age años",
                                            style = CrimsonSemiBold,
                                            fontSize = 18.sp,
                                            color = Black
                                        )
                                    }
                                }

                                // Email
                                Text(
                                    text = psychProfile.email,
                                    style = CrimsonSemiBold,
                                    fontSize = 18.sp,
                                    color = Black,
                                    maxLines = 1
                                )

                                // Badges - Solo mostrar las 2 primeras especialidades del backend
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // Show only first two specialties from backend
                                    psychProfile.specialties.take(2).forEach { specialty ->
                                        Badge(text = specialty)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Menu Options
                        MenuOptionDrawable(
                            iconRes = R.drawable.ic_edit_information,
                            text = "Editar Información Personal",
                            onClick = onNavigateToEditProfile
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        MenuOption(
                            icon = Icons.Default.QrCode,
                            text = "Mi Código de Invitación",
                            onClick = onNavigateToInvitationCode
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        MenuOptionDrawable(
                            iconRes = R.drawable.ic_notification_bell,
                            text = "Notificaciones",
                            onClick = onNavigateToNotifications
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        MenuOptionDrawable(
                            iconRes = R.drawable.ic_subscription_plan,
                            text = "Mi plan",
                            onClick = onNavigateToPlan
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        MenuOptionDrawable(
                            iconRes = R.drawable.ic_policy_privacy,
                            text = "Mis Estadísticas",
                            onClick = onNavigateToStats
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        MenuOption(
                            icon = Icons.Default.Work,
                            text = "Datos profesionales",
                            onClick = onNavigateToProfessionalData
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        MenuOption(
                            icon = Icons.Outlined.Logout,
                            text = "Cerrar Sesión",
                            onClick = onLogout
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun Badge(text: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Green65,
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = SourceSansRegular,
            fontSize = 12.sp,
            color = White
        )
    }
}

@Composable
private fun MenuOption(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        color = GreenA3,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Black,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = text,
                    style = SourceSansRegular,
                    fontSize = 16.sp,
                    color = Black
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Black
            )
        }
    }
}

@Composable
private fun MenuOptionDrawable(
    iconRes: Int,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        color = GreenA3,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = Black,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = text,
                    style = SourceSansRegular,
                    fontSize = 16.sp,
                    color = Black
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Black
            )
        }
    }
}

@Composable
private fun AsyncImageLoader(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    LaunchedEffect(imageUrl) {
        withContext(Dispatchers.IO) {
            try {
                val url = URL(imageUrl)
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "Profile image",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } ?: Box(
        modifier = modifier.background(GreenA3)
    )
}

private fun calculateAge(dateOfBirth: String): Int? {
    return try {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val birthDate = LocalDate.parse(dateOfBirth, formatter)
        val currentDate = LocalDate.now()
        Period.between(birthDate, currentDate).years
    } catch (e: Exception) {
        null
    }
}

@Preview(showBackground = true)
@Composable
fun PsychologistProfileScreenPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Profile Section - Image and Info side by side
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Profile Image
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(GreenA3)
            )

            // User Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Dra. Patricia Sanchez",
                    style = CrimsonSemiBold.copy(fontSize = 20.sp),
                    color = Black,
                    maxLines = 2,
                    lineHeight = 28.sp
                )

                Text(
                    text = "40 años",
                    style = CrimsonSemiBold,
                    fontSize = 18.sp,
                    color = Black
                )

                Text(
                    text = "psychologist1@test.com",
                    style = CrimsonSemiBold,
                    fontSize = 18.sp,
                    color = Black,
                    maxLines = 1
                )

                // Badges - Solo 2 especialidades
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Badge(text = "Ansiedad")
                    Badge(text = "Depresión")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Menu Options
        MenuOptionDrawable(
            iconRes = R.drawable.ic_edit_information,
            text = "Editar Información Personal",
            onClick = { }
        )

        Spacer(modifier = Modifier.height(12.dp))

        MenuOption(
            icon = Icons.Default.QrCode,
            text = "Mi Código de Invitación",
            onClick = { }
        )

        Spacer(modifier = Modifier.height(12.dp))

        MenuOptionDrawable(
            iconRes = R.drawable.ic_notification_bell,
            text = "Notificaciones",
            onClick = { }
        )

        Spacer(modifier = Modifier.height(12.dp))

        MenuOptionDrawable(
            iconRes = R.drawable.ic_subscription_plan,
            text = "Mi plan",
            onClick = { }
        )

        Spacer(modifier = Modifier.height(12.dp))

        MenuOptionDrawable(
            iconRes = R.drawable.ic_policy_privacy,
            text = "Mis Estadísticas",
            onClick = { }
        )

        Spacer(modifier = Modifier.height(12.dp))

        MenuOption(
            icon = Icons.Default.Work,
            text = "Datos profesionales",
            onClick = { }
        )

        Spacer(modifier = Modifier.height(12.dp))

        MenuOption(
            icon = Icons.Outlined.Logout,
            text = "Cerrar Sesión",
            onClick = { }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

