package com.softfocus.features.profile.presentation.patient

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.softfocus.features.profile.presentation.ProfileViewModel
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.SourceSansRegular
import com.softfocus.ui.theme.Black
import com.softfocus.ui.theme.GreenA3
import com.softfocus.ui.theme.ButtonPrimary
import com.softfocus.R
import java.net.URL
import android.graphics.BitmapFactory
import androidx.compose.foundation.clickable
import com.softfocus.ui.components.ProfileAvatar
import com.softfocus.ui.theme.Blue77
import com.softfocus.ui.theme.RedE8
import com.softfocus.ui.theme.SourceSansBold
import com.softfocus.ui.theme.White
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientProfileScreen(
    onNavigateToConnect: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToNotifications: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val assignedPsychologist by viewModel.assignedPsychologist.collectAsState()
    val psychologistLoadState by viewModel.psychologistLoadState.collectAsState()

    if (uiState is com.softfocus.features.profile.presentation.ProfileUiState.Loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Editar información Personal",
                        style = CrimsonSemiBold,
                        fontSize = 25.sp,
                        color = GreenA3
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Profile Section - Image and Info side by side
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Profile Image
                ProfileAvatar(
                    imageUrl = user?.profileImageUrl,
                    fullName = user?.fullName ?: "Usuario",
                    size = 120.dp,
                    fontSize = 48.sp,
                    backgroundColor = GreenA3,
                    textColor = Color.White
                )

                // User Info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = user?.fullName ?: "Usuario",
                        style = CrimsonSemiBold,
                        fontSize = 28.sp,
                        color = Black
                    )

                    // Edad calculada desde dateOfBirth
                    user?.dateOfBirth?.let { dob ->
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

                    Text(
                        text = user?.email ?: "",
                        style = CrimsonSemiBold,
                        fontSize = 18.sp,
                        color = Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (psychologistLoadState) {
                is com.softfocus.features.profile.presentation.PsychologistLoadState.Success -> {
                    assignedPsychologist?.let { psychologist ->
                        CurrentTherapistCard(
                            therapistName = psychologist.fullName,
                            therapistImageUrl = psychologist.profileImageUrl,
                            onUnlinkClick = onNavigateToConnect
                        )
                    }
                }
                is com.softfocus.features.profile.presentation.PsychologistLoadState.Loading -> {
                    CurrentTherapistCard(
                        therapistName = "Cargando...",
                        therapistImageUrl = null,
                        onUnlinkClick = onNavigateToConnect
                    )
                }
                is com.softfocus.features.profile.presentation.PsychologistLoadState.NoTherapist -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5F5F5)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No tienes un terapeuta asignado",
                                style = SourceSansRegular,
                                fontSize = 16.sp,
                                color = Black
                            )
                        }
                    }
                }
                is com.softfocus.features.profile.presentation.PsychologistLoadState.Error -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5F5F5)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Error al cargar información del terapeuta",
                                style = SourceSansRegular,
                                fontSize = 16.sp,
                                color = RedE8
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = { viewModel.loadProfile() }) {
                                Text(
                                    text = "Reintentar",
                                    style = SourceSansBold,
                                    fontSize = 14.sp,
                                    color = Blue77
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))


            // Menu Options
            ProfileOptionDrawable(
                iconRes = R.drawable.ic_edit_information,
                title = "Editar información Personal",
                onClick = onNavigateToEditProfile
            )

            ProfileOptionDrawable(
                iconRes = R.drawable.ic_notification_bell,
                title = "Notificaciones",
                onClick = onNavigateToNotifications
            )

            ProfileOptionDrawable(
                iconRes = R.drawable.ic_subscription_plan,
                title = "Mi plan",
                onClick = { }
            )

            ProfileOptionDrawable(
                iconRes = R.drawable.ic_policy_privacy,
                title = "Política de Privacidad",
                onClick = { }
            )

            ProfileOptionDrawable(
                iconRes = R.drawable.ic_help_support,
                title = "Ayuda y Soporte",
                onClick = { }
            )

            ProfileOption(
                icon = Icons.Outlined.Logout,
                title = "Cerrar Sesión",
                onClick = onLogout
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CurrentTherapistCard(
    therapistName: String,
    therapistImageUrl: String?,
    onUnlinkClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Mi Terapeuta Actual",
                style = CrimsonSemiBold,
                fontSize = 20.sp,
                color = Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Columna 1: Imagen
                ProfileAvatar(
                    imageUrl = therapistImageUrl,
                    fullName = therapistName,
                    size = 100.dp,
                    fontSize = 40.sp,
                    backgroundColor = GreenA3,
                    textColor = Color.White
                )

                Spacer(modifier = Modifier.width(24.dp))

                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = therapistName,
                        style = CrimsonSemiBold,
                        fontSize = 18.sp,
                        color = Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Ver perfil",
                        style = SourceSansRegular,
                        fontSize = 13.sp,
                        color = Blue77,
                        modifier = Modifier.clickable {
                            // TODO: Dejar vacío o implementar la lógica de clic
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onUnlinkClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RedE8
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Desvincular",
                    style = SourceSansBold,
                    fontSize = 14.sp,
                    color = White
                )
            }
        }
    }
}

@Composable
fun ProfileOption(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = GreenA3
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Black,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = SourceSansRegular,
                    fontSize = 16.sp,
                    color = Black
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Black
            )
        }
    }
}

@Composable
fun ProfileOptionDrawable(
    iconRes: Int,
    title: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = GreenA3
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = Black,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = SourceSansRegular,
                    fontSize = 16.sp,
                    color = Black
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Black
            )
        }
    }
}

@Composable
fun AsyncImageLoader(
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
fun PatientProfileScreenPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Profile Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(GreenA3)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Laura Gomez",
                    style = CrimsonSemiBold,
                    fontSize = 28.sp,
                    color = Black
                )
                Text(
                    text = "20 años",
                    style = CrimsonSemiBold,
                    fontSize = 18.sp,
                    color = Black
                )
                Text(
                    text = "laura@gmail.com",
                    style = CrimsonSemiBold,
                    fontSize = 18.sp,
                    color = Black
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Current Therapist Card
        CurrentTherapistCard(
            therapistName = "Dra. Herrera",
            therapistImageUrl = null,
            onUnlinkClick = { }
        )

        Spacer(modifier = Modifier.height(24.dp))

        ProfileOptionDrawable(
            iconRes = R.drawable.ic_edit_information,
            title = "Editar información Personal",
            onClick = { }
        )

        ProfileOptionDrawable(
            iconRes = R.drawable.ic_notification_bell,
            title = "Notificaciones",
            onClick = { }
        )

        ProfileOptionDrawable(
            iconRes = R.drawable.ic_subscription_plan,
            title = "Mi plan",
            onClick = { }
        )

        ProfileOptionDrawable(
            iconRes = R.drawable.ic_policy_privacy,
            title = "Términos y Condiciones",
            onClick = { }
        )

        ProfileOptionDrawable(
            iconRes = R.drawable.ic_policy_privacy,
            title = "Política de Privacidad",
            onClick = { }
        )

        ProfileOptionDrawable(
            iconRes = R.drawable.ic_help_support,
            title = "Ayuda y Soporte",
            onClick = { }
        )

        ProfileOption(
            icon = Icons.Outlined.Logout,
            title = "Cerrar Sesión",
            onClick = { }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}
