package com.softfocus.features.profile.presentation.shared

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.ui.theme.AppColors
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.GreenA3
import com.softfocus.ui.theme.SourceSansBold
import com.softfocus.ui.theme.SourceSansRegular
import com.softfocus.ui.theme.Black
import com.softfocus.ui.theme.Blue77
import com.softfocus.ui.theme.Green37
import com.softfocus.ui.theme.Gray828

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ayuda y Soporte",
                        style = CrimsonSemiBold,
                        fontSize = 24.sp,
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
                    containerColor = AppColors.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(AppColors.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Encabezado
            Text(
                text = "¿Necesitas ayuda? Estamos aquí para ti",
                style = CrimsonSemiBold,
                fontSize = 20.sp,
                color = AppColors.textPrimary,
                lineHeight = 28.sp
            )

            Text(
                text = "Contacta con nuestro equipo de soporte según tu necesidad. " +
                       "Estamos disponibles 24/7 para asistirte.",
                style = SourceSansRegular,
                fontSize = 16.sp,
                color = AppColors.textSecondary,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Contacto general
            ContactCard(
                icon = Icons.Outlined.Email,
                title = "Contacto General",
                email = "softfocusorg@gmail.com",
                description = "Para consultas generales, sugerencias o información sobre SoftFocus"
            )

            // Landing Page
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Language,
                        contentDescription = null,
                        tint = Green37,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Sitio Web",
                            style = SourceSansBold,
                            fontSize = 16.sp,
                            color = AppColors.textPrimary,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "soft-focus-61053.web.app",
                            style = SourceSansBold,
                            fontSize = 16.sp,
                            color = Blue77,
                            lineHeight = 22.sp,
                            modifier = Modifier.clickable {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse("https://soft-focus-61053.web.app")
                                }
                                context.startActivity(intent)
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Visita nuestra página web oficial para más información sobre SoftFocus",
                            style = SourceSansRegular,
                            fontSize = 14.sp,
                            color = Gray828,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // Números de soporte específicos
            SectionTitle("Soporte Especializado")

            SupportNumberCard(
                icon = Icons.Outlined.Notifications,
                category = "Notificaciones",
                phone = "952 280 745",
                description = "Problemas con notificaciones, alertas o avisos de la app"
            )

            SupportNumberCard(
                icon = Icons.Outlined.Folder,
                category = "Datos y Contenido",
                phone = "974 341 019",
                description = "Gestión de datos, biblioteca de contenido y recursos"
            )

            SupportNumberCard(
                icon = Icons.Outlined.Security,
                category = "Servidor y Seguridad",
                phone = "982 757 892",
                description = "Problemas de conexión, seguridad y privacidad de datos"
            )

            SupportNumberCard(
                icon = Icons.Outlined.Psychology,
                category = "Chat y Psicólogo",
                phone = "981 936 328",
                description = "Comunicación con psicólogos, mensajes y sesiones"
            )

            SupportNumberCard(
                icon = Icons.Outlined.CalendarMonth,
                category = "Calendario y Seguimiento",
                phone = "933 372 489",
                description = "Diario emocional, check-ins y progreso personal"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Atención prioritaria
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Green37.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.SupportAgent,
                            contentDescription = null,
                            tint = Green37,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "Atención Prioritaria 24/7",
                            style = SourceSansBold,
                            fontSize = 18.sp,
                            color = Green37
                        )
                    }

                    Text(
                        text = "Para dudas específicas o atención urgente:",
                        style = SourceSansRegular,
                        fontSize = 14.sp,
                        color = AppColors.textPrimary
                    )

                    Text(
                        text = "952 280 745",
                        style = SourceSansBold,
                        fontSize = 20.sp,
                        color = Blue77,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:952280745")
                                }
                                context.startActivity(intent)
                            }
                            .padding(vertical = 8.dp),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Toca el número para llamar directamente",
                        style = SourceSansRegular,
                        fontSize = 12.sp,
                        color = Gray828,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ContactCard(
    icon: ImageVector,
    title: String,
    email: String,
    description: String
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Green37,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = SourceSansBold,
                    fontSize = 16.sp,
                    color = AppColors.textPrimary,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = email,
                    style = SourceSansBold,
                    fontSize = 16.sp,
                    color = Blue77,
                    lineHeight = 22.sp,
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:$email")
                        }
                        context.startActivity(intent)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    style = SourceSansRegular,
                    fontSize = 14.sp,
                    color = Gray828,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = SourceSansBold,
        fontSize = 18.sp,
        color = AppColors.textPrimary,
        lineHeight = 24.sp,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun SupportNumberCard(
    icon: ImageVector,
    category: String,
    phone: String,
    description: String
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Green37,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = category,
                    style = SourceSansBold,
                    fontSize = 16.sp,
                    color = AppColors.textPrimary,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = phone,
                    style = SourceSansBold,
                    fontSize = 16.sp,
                    color = Blue77,
                    lineHeight = 22.sp,
                    modifier = Modifier.clickable {
                        val cleanPhone = phone.replace(" ", "")
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:$cleanPhone")
                        }
                        context.startActivity(intent)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    style = SourceSansRegular,
                    fontSize = 14.sp,
                    color = Gray828,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
