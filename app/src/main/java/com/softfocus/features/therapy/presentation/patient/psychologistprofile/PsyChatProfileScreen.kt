package com.softfocus.features.therapy.presentation.patient.psychologistprofile

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.softfocus.R
import com.softfocus.ui.theme.Black
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.RedE8
import com.softfocus.ui.theme.SourceSansBold
import com.softfocus.ui.theme.SourceSansRegular
import com.softfocus.ui.theme.SourceSansSemiBold
import com.softfocus.ui.theme.White

@Composable
fun PsyChatProfileScreen(
    onBackClicked: () -> Unit,
    onUnlinkClick: () -> Unit,
    viewModel: PsyChatProfileViewModel,
    context: Context
) {
    val summaryState by viewModel.summaryState.collectAsState()
    var showDisconnectDialog by remember { mutableStateOf(false) }

    // Diálogo de confirmación de desvinculación
    if (showDisconnectDialog) {
        AlertDialog(
            onDismissRequest = { showDisconnectDialog = false },
            title = {
                Text(
                    text = "Desvincular Terapeuta",
                    style = CrimsonSemiBold,
                    fontSize = 20.sp,
                    color = Black,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "¿Estás seguro de que deseas desvincularte de tu terapeuta?",
                        style = SourceSansRegular,
                        fontSize = 16.sp,
                        color = Black,
                        lineHeight = 22.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Esta acción no se puede deshacer.",
                        style = SourceSansRegular,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 20.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDisconnectDialog = false
                        viewModel.disconnectPsychologist(
                            onSuccess = {
                                Toast.makeText(
                                    context,
                                    "Terapeuta desvinculado exitosamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // Navegar a la vista de conexión (General Home)
                                onUnlinkClick()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedE8
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = "Confirmar",
                        color = White,
                        style = SourceSansBold,
                        fontSize = 14.sp
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDisconnectDialog = false },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Cancelar",
                        color = Black,
                        style = SourceSansBold,
                        fontSize = 14.sp
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = White,
            modifier = Modifier.padding(16.dp)
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFCFEFC))
    ) {
        // Sección de imagen superior y botón de regreso
        item {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)) {
                // Placeholder para la imagen de cabecera
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!summaryState.profilePhotoUrl.isNullOrBlank()) {
                        // Si hay imagen, mostrarla
                        AsyncImage(
                            model = summaryState.profilePhotoUrl,
                            contentDescription = summaryState.psychologistName,
                            modifier = Modifier
                                .fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Si no hay imagen, mostrar inicial
                        Text(
                            text = summaryState.psychologistName.firstOrNull()?.uppercase() ?: "?",
                            style = SourceSansSemiBold,
                            fontSize = 30.sp,
                            color = Green49
                        )
                    }
                }
                // Botón de regreso
                IconButton(
                    onClick = onBackClicked,
                    modifier = Modifier
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar",
                        tint = Color.White
                    )
                }
            }
        }

        // Contenido principal del perfil
        item {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = summaryState.psychologistName,
                    style = CrimsonSemiBold.copy(fontSize = 30.sp),
                    color = Color(0xFF657142)
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (summaryState.specialties.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)){
                        summaryState.specialties.forEach { specialty ->
                            ProfileTag(text = specialty)
                        }
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 24.dp), color = Color.LightGray)

                // Título y Universidad
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = summaryState.degree ?: "M.A Psicología Clínica",
                        style = CrimsonSemiBold.copy(fontSize = 18.sp),
                    )
                    Text(
                        text = summaryState.university ?: "Universidad PUCP",
                        style = SourceSansSemiBold.copy(fontSize = 14.sp),
                        color = Color.Gray
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 24.dp), color = Color.LightGray)

                // Biografía
                Text(
                    text = summaryState.bio ?: "No especificado",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
                )
                Divider(modifier = Modifier.padding(top = 24.dp, bottom = 10.dp), color = Color.LightGray)

                // Contacto externo


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Contacto externo",
                            style = CrimsonSemiBold.copy(fontSize = 24.sp),
                            color = Color(0xFF37593F)
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        ContactRow(
                            icon = Icons.Default.Email,
                            text = summaryState.email ?: "No especificado"
                        )
                        ContactRow(
                            icon = Icons.Default.Phone,
                            text = summaryState.phoneNumber ?: "No especificado"
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.bunny_softfocus),
                        contentDescription = "Conejo",
                        modifier = Modifier
                            .height(150.dp)
                            .align(Alignment.CenterVertically)
                    )
                }

                Divider(modifier = Modifier.padding(top = 10.dp, bottom = 24.dp), color = Color.LightGray)

                Button(
                    onClick = onUnlinkClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedE8
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
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
}

@Composable
fun ProfileTag(text: String) {
    Surface(
        color = Color(0xFF657142),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun ContactRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF657142),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = SourceSansRegular.copy(fontSize = 14.sp, textDecoration = TextDecoration.Underline),
            color = Color.DarkGray
        )
    }
}
