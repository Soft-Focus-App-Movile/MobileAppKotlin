package com.softfocus.features.search.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.softfocus.features.search.domain.models.Psychologist
import com.softfocus.ui.theme.*

/**
 * Card reutilizable para mostrar información de un psicólogo
 */
@Composable
fun PsychologistCard(
    psychologist: Psychologist,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Foto de perfil
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (psychologist.profileImageUrl != null) {
                    AsyncImage(
                        model = psychologist.profileImageUrl,
                        contentDescription = psychologist.fullName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Placeholder si no hay imagen
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Green49,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información del psicólogo
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Nombre
                Text(
                    text = psychologist.fullName,
                    style = CrimsonSemiBold,
                    fontSize = 18.sp,
                    color = Green65,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Especialidades
                if (psychologist.specialties.isNotEmpty()) {
                    Text(
                        text = psychologist.specialties.take(2).joinToString(", "),
                        style = SourceSansRegular,
                        fontSize = 13.sp,
                        color = Gray787,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Ciudad y años de experiencia
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (psychologist.city != null) {
                        Text(
                            text = psychologist.city,
                            style = SourceSansRegular,
                            fontSize = 12.sp,
                            color = Gray787
                        )
                        Text(
                            text = " • ",
                            style = SourceSansRegular,
                            fontSize = 12.sp,
                            color = Gray787
                        )
                    }
                    Text(
                        text = "${psychologist.yearsOfExperience} años exp.",
                        style = SourceSansRegular,
                        fontSize = 12.sp,
                        color = Gray787
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Rating y disponibilidad
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (psychologist.averageRating != null) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = YellowCB9D,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", psychologist.averageRating),
                            style = SourceSansSemiBold,
                            fontSize = 13.sp,
                            color = Color.Black
                        )
                        Text(
                            text = " (${psychologist.totalReviews})",
                            style = SourceSansRegular,
                            fontSize = 12.sp,
                            color = Gray787
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Indicador de disponibilidad
                    if (psychologist.isAcceptingNewPatients) {
                        Text(
                            text = "Disponible",
                            style = SourceSansSemiBold,
                            fontSize = 12.sp,
                            color = Green49
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PsychologistCardPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        PsychologistCard(
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
            onClick = {}
        )
    }
}
