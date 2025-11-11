package com.softfocus.features.profile.presentation.general

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
import androidx.compose.ui.res.painterResource
import com.softfocus.R
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.softfocus.features.profile.presentation.ProfileViewModel
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Gray828
import com.softfocus.ui.theme.SourceSansRegular
import java.net.URL
import android.graphics.BitmapFactory
import com.softfocus.ui.components.ProfileAvatar
import com.softfocus.ui.theme.Black
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.GreenA3
import com.softfocus.ui.theme.White
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralProfileScreen(
    onNavigateToConnect: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

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
                    containerColor = White
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

            Spacer(modifier = Modifier.height(32.dp))

            ProfileOptionDrawable(
                iconRes = R.drawable.ic_edit_information,
                title = "Editar información Personal",
                onClick = onNavigateToEditProfile
            )

            ProfileOptionDrawable(
                iconRes = R.drawable.ic_connect_psychology,
                title = "Conectar con Psicólogo",
                onClick = onNavigateToConnect
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
fun GeneralProfileScreenPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
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
                    text = "Laura Gomez",
                    style = CrimsonSemiBold,
                    fontSize = 28.sp,
                    color = Black
                )

                Text(
                    text = "21 años",
                    style = CrimsonSemiBold,
                    fontSize = 18.sp,
                    color = Black
                )

                Text(
                    text = "patient1@test.com",
                    style = CrimsonSemiBold,
                    fontSize = 18.sp,
                    color = Black
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        ProfileOptionDrawable(
            iconRes = R.drawable.ic_edit_information,
            title = "Editar información Personal",
            onClick = { }
        )

        ProfileOptionDrawable(
            iconRes = R.drawable.ic_connect_psychology,
            title = "Conectar con Psicólogo",
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
