package com.softfocus.features.profile.presentation.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Política de Privacidad",
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Última actualización
            Text(
                text = "Última actualización: Noviembre 2025",
                style = SourceSansRegular,
                fontSize = 14.sp,
                color = AppColors.textSecondary
            )

            // Introducción con icono
            PrivacyCard(
                icon = Icons.Filled.VerifiedUser,
                title = "Compromiso con tu Privacidad",
                content = "En SoftFocus, tu privacidad y seguridad son nuestra máxima prioridad. " +
                        "Nos comprometemos a proteger toda tu información personal y de salud mental " +
                        "con los más altos estándares de seguridad y confidencialidad."
            )

            // Chat IA
            PrivacyCard(
                icon = Icons.Outlined.Chat,
                title = "Chat de Inteligencia Artificial",
                content = "Todas las conversaciones con nuestro asistente de IA están completamente encriptadas " +
                        "de extremo a extremo. Tu información nunca es compartida con terceros y se utiliza " +
                        "únicamente para brindarte apoyo personalizado."
            )

            // Análisis de imágenes
            PrivacyCard(
                icon = Icons.Outlined.CameraAlt,
                title = "Análisis de Emociones por Imagen",
                content = "Las imágenes son procesadas de forma segura y encriptada. No almacenamos las fotografías originales, " +
                        "solo los resultados del análisis emocional. Puedes eliminar estos datos en cualquier momento."
            )

            // Comunicación con psicólogos
            PrivacyCard(
                icon = Icons.Outlined.Psychology,
                title = "Comunicación con tu Psicólogo",
                content = "Todos los mensajes y registros compartidos con tu psicólogo están protegidos " +
                        "por encriptación de grado médico. Solo tú y tu psicólogo asignado tienen acceso. " +
                        "Cumplimos con la confidencialidad profesional establecida por ley."
            )

            // Seguimiento emocional
            PrivacyCard(
                icon = Icons.Outlined.CalendarMonth,
                title = "Diario y Seguimiento Emocional",
                content = "Tus registros diarios y estados de ánimo están almacenados de forma segura y privada. " +
                        "Esta información es visible únicamente para ti y, si lo autorizas, para tu psicólogo asignado."
            )

            // Seguridad de datos
            PrivacyCard(
                icon = Icons.Filled.Security,
                title = "Seguridad de la Información",
                content = "Utilizamos encriptación AES-256 para proteger toda tu información. Nuestros servidores " +
                        "están certificados y cumplen con estándares internacionales de seguridad (ISO 27001)."
            )

            // Tus derechos
            PrivacyCard(
                icon = Icons.Outlined.Gavel,
                title = "Tus Derechos",
                content = "Tienes derecho a acceder, corregir y eliminar tu información personal en cualquier momento. " +
                        "Puedes descargar una copia de tus datos y revocar permisos cuando lo desees."
            )

            // Contacto
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Green37.copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContactSupport,
                        contentDescription = null,
                        tint = Green37,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "¿Dudas sobre privacidad?",
                            style = SourceSansBold,
                            fontSize = 16.sp,
                            color = AppColors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "softfocusorg@gmail.com",
                            style = SourceSansRegular,
                            fontSize = 14.sp,
                            color = Blue77
                        )
                        Text(
                            text = "952 280 745 (24/7)",
                            style = SourceSansRegular,
                            fontSize = 14.sp,
                            color = Blue77
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun PrivacyCard(
    icon: ImageVector,
    title: String,
    content: String
) {
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
                    text = content,
                    style = SourceSansRegular,
                    fontSize = 14.sp,
                    color = AppColors.textSecondary,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
