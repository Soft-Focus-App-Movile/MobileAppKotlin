package com.softfocus.features.therapy.presentation.psychologist.patiendetail.tabs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.softfocus.features.therapy.presentation.psychologist.patiendetail.lightGrayText
import com.softfocus.ui.components.navigation.PsychologistBottomNav
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.SoftFocusMobileTheme

val primaryGreen = Color(0xFF4B634B)
val cardBackground = Color(0xFFF7F7F3)
val chatBubbleGreen = Color(0xFFE4F1E3)
val chatBubbleWhite = Color(0xFFFFFFFF)

// --- Datos de ejemplo para el chat ---
data class Message(val text: String, val timestamp: String, val isFromMe: Boolean)

val dummyMessages = listOf(
    Message("Buenas tardes, gracias por el recordatorio, las haré", "", true),
    Message("Buenas tardes Laura, no te olvides de realizar las tareas que te asigné", "2:00pm", false)
)

// --- Pantalla Principal de Chat ---

@Composable
fun PatientChatScreen(
    navController: NavHostController,
    patientName: String
) {
    Scaffold(
        topBar = {
            PatientChatTopBar(
                patientName = patientName,
                onNavigateBack = { navController.popBackStack() }
            )
        },
        bottomBar = {
            // Creamos una Columna para apilar las dos barras
            Column(modifier = Modifier.background(Color.White)) {
                // 1. La barra de chat (el campo de texto)
                ChatInputField()
                // 2. La barra de navegación principal
                PsychologistBottomNav(navController = navController)
            }
        }
    ) { paddingValues ->
        ChatContent(
            messages = dummyMessages,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

// --- Componentes de PatientChatScreen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientChatTopBar(
    patientName: String,
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Placeholder de imagen
                Box(contentAlignment = Alignment.BottomEnd) {
                    Image(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Foto de Ana García",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)

                    )
                    // Punto verde de "Activo"
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color.Green, CircleShape)
                            .border(2.dp, primaryGreen, CircleShape)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = patientName,
                        style = CrimsonSemiBold.copy(fontSize = 28.sp)
                    )
                    Text(
                        text = "Activo(a) ahora",
                        fontSize = 10.sp,
                        color = Color.Black
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarColors(
            containerColor = Color(0xFF8B9471),
            scrolledContainerColor = Color.White,
            navigationIconContentColor = Color.White,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White,
            subtitleContentColor = Color.White
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInputField() {
    var text by remember { mutableStateOf("") }
    var isAttachmentMenuExpanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- Box para el Botón "+" y su Menú ---
            Box {
                IconButton(onClick = { isAttachmentMenuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.AddCircle, // Icono de "+" en círculo
                        contentDescription = "Adjuntar",
                        tint = Color.Gray,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // --- Menú Emergente ---
                DropdownMenu(
                    expanded = isAttachmentMenuExpanded,
                    onDismissRequest = { isAttachmentMenuExpanded = false },
                    // Offset para que aparezca ARRIBA del botón
                    offset = DpOffset(x = 0.dp, y = (-160).dp)
                ) {
                    DropdownMenuItem(
                        onClick = {
                            isAttachmentMenuExpanded = false /* TODO: Abrir Cámara */
                        },
                        leadingIcon = {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.Gray)
                        },
                        text = { Text("Cámara") }
                    )
                    DropdownMenuItem(
                        onClick = { isAttachmentMenuExpanded = false /* TODO: Abrir Galería */ },
                        leadingIcon = {
                            Icon(Icons.Default.Image, contentDescription = null, tint = Color.Gray)
                        },
                        text = { Text("Galería") }
                    )
                    DropdownMenuItem(
                        onClick = { isAttachmentMenuExpanded = false /* TODO: Abrir Documento */ },
                        leadingIcon = {
                            Icon(Icons.Default.Description, contentDescription = null, tint = Color.Gray)
                        },
                        text = { Text("Documento") }
                    )
                }
            }

            // --- Campo de Texto ---
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("Escribe tu mensaje...") },
                modifier = Modifier.weight(1f), // Añadido para consistencia

                maxLines = 5
            )

            // --- Botón de Enviar ---
            IconButton(onClick = { /* TODO: Enviar mensaje */ }) {
                Icon(Icons.Default.Send, contentDescription = "Enviar", tint = Color.Gray)
            }
        }
    }
}

// --- Contenido del Chat (Burbujas) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatContent(messages: List<Message>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        reverseLayout = true // Muestra los mensajes más nuevos abajo
    ) {
        item { Spacer(modifier = Modifier.height(16.dp)) }
        items(messages) { message ->
            ChatBubble(message = message)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBubble(message: Message) {
    // La lógica de alineación de la burbuja (izquierda/derecha) no cambia
    val bubbleColor = if (message.isFromMe) chatBubbleGreen else chatBubbleWhite
    val alignment = if (message.isFromMe) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = if (message.isFromMe) 16.dp else 0.dp,
        bottomEnd = if (message.isFromMe) 0.dp else 16.dp
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (message.isFromMe) 48.dp else 0.dp,
                end = if (message.isFromMe) 0.dp else 48.dp
            ),
        contentAlignment = alignment
    ) {
        Card(
            shape = bubbleShape,
            colors = CardDefaults.cardColors(containerColor = bubbleColor),
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp) // Ajustamos padding
            ) {
                Text(
                    text = message.text,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Start) // Alineado a la izquierda
                )

                // 2. El timestamp, si existe
                if (message.timestamp.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp)) // Espacio vertical
                    Text(
                        text = message.timestamp,
                        fontSize = 12.sp,
                        color = lightGrayText,
                        modifier = Modifier.align(Alignment.End) // Alineado a la derecha
                    )
                }
            }
        }
    }
}

// --- Preview ---
/*
@Preview(showBackground = true)
@Composable
fun PatientChatScreenPreview() {
    SoftFocusMobileTheme() {
        PatientChatScreen()
    }
}
*/