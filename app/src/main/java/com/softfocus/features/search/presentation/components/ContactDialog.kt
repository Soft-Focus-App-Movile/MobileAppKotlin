package com.softfocus.features.search.presentation.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.softfocus.R
import com.softfocus.features.search.domain.models.Psychologist
import com.softfocus.ui.theme.*

@Composable
fun ContactDialog(
    psychologist: Psychologist,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Contactar",
                        style = CrimsonSemiBold,
                        fontSize = 20.sp,
                        color = Green65
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Gray787
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = psychologist.fullName,
                    style = SourceSansSemiBold,
                    fontSize = 16.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Opciones de contacto
                if (psychologist.email != null) {
                    ContactOption(
                        icon = Icons.Default.Email,
                        label = "Email",
                        value = psychologist.email,
                        onClick = {
                            sendEmail(context, psychologist.email, psychologist.fullName)
                            onDismiss()
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (psychologist.corporateEmail != null) {
                    ContactOption(
                        icon = Icons.Default.Email,
                        label = "Email Corporativo",
                        value = psychologist.corporateEmail,
                        onClick = {
                            sendEmail(context, psychologist.corporateEmail, psychologist.fullName)
                            onDismiss()
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (psychologist.phone != null) {
                    ContactOption(
                        icon = Icons.Default.Phone,
                        label = "Teléfono",
                        value = psychologist.phone,
                        onClick = {
                            makePhoneCall(context, psychologist.phone)
                            onDismiss()
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (psychologist.whatsApp != null) {
                    ContactOption(
                        iconRes = R.drawable.ic_notification_bell, // TODO: Usar icono de WhatsApp real
                        label = "WhatsApp",
                        value = psychologist.whatsApp,
                        onClick = {
                            openWhatsApp(context, psychologist.whatsApp, psychologist.fullName)
                            onDismiss()
                        }
                    )
                }

                // Verificar si no hay información de contacto
                if (psychologist.email == null &&
                    psychologist.corporateEmail == null &&
                    psychologist.phone == null &&
                    psychologist.whatsApp == null) {
                    Text(
                        text = "No hay información de contacto disponible",
                        style = SourceSansRegular,
                        fontSize = 14.sp,
                        color = Gray787,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Card informativa con elefante
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Card(
                        modifier = Modifier.width(200.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.elephant_focus),
                                contentDescription = "Elephant Focus",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(60.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Comunícate con la psicóloga para coordinar una sesión. Solicítale su código de invitación para conectarte en la app.",
                                style = SourceSansRegular,
                                fontSize = 11.sp,
                                color = Gray787,
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Justify,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = onDismiss,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Green49),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Entendido",
                                    style = SourceSansSemiBold,
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactOption(
    icon: ImageVector? = null,
    iconRes: Int? = null,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Green49,
                    modifier = Modifier.size(24.dp)
                )
            } else if (iconRes != null) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = Green49,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = SourceSansSemiBold,
                    fontSize = 14.sp,
                    color = Green65
                )
                Text(
                    text = value,
                    style = SourceSansRegular,
                    fontSize = 12.sp,
                    color = Gray787
                )
            }
        }
    }
}

private fun sendEmail(context: Context, email: String, psychologistName: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        putExtra(Intent.EXTRA_SUBJECT, "Consulta desde SoftFocus - $psychologistName")
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // Manejar error
    }
}

private fun makePhoneCall(context: Context, phone: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phone")
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // Manejar error
    }
}

private fun openWhatsApp(context: Context, phone: String, psychologistName: String) {
    val message = "Hola $psychologistName, me gustaría agendar una consulta."
    val url = "https://wa.me/$phone?text=${Uri.encode(message)}"
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(url)
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // Si WhatsApp no está instalado, intentar abrir en navegador
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(browserIntent)
    }
}

@Preview(showBackground = true)
@Composable
fun ContactDialogPreview() {
    ContactDialog(
        psychologist = Psychologist(
            id = "1",
            fullName = "Dra. María García López",
            profileImageUrl = null,
            professionalBio = "Psicóloga clínica especializada en terapia cognitivo-conductual",
            specialties = listOf("Ansiedad", "Depresión", "Estrés"),
            yearsOfExperience = 8,
            city = "Lima",
            languages = listOf("Español", "Inglés"),
            isAcceptingNewPatients = true,
            averageRating = 4.8,
            totalReviews = 127,
            allowsDirectMessages = true,
            targetAudience = listOf("Adultos", "Adolescentes"),
            email = "maria.garcia@example.com",
            phone = "+51 999 888 777",
            whatsApp = "+51999888777",
            corporateEmail = "maria@clinica.com",
            university = "Universidad Nacional Mayor de San Marcos",
            graduationYear = 2015,
            degree = "Licenciatura en Psicología",
            licenseNumber = "CPsP 12345",
            professionalCollege = "Colegio de Psicólogos del Perú",
            collegeRegion = "Lima"
        ),
        onDismiss = {}
    )
}
