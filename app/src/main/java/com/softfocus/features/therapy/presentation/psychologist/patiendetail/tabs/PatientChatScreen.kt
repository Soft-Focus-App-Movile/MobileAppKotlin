package com.softfocus.features.therapy.presentation.psychologist.patiendetail.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.softfocus.R
import com.softfocus.features.therapy.domain.models.ChatMessage
import com.softfocus.features.therapy.presentation.psychologist.patiendetail.PatientSummaryState
import com.softfocus.ui.theme.CrimsonSemiBold
val chatBubbleUser = Color(0xFFE0F7E0)
val chatBubbleOther = Color(0xFFFFFFFF)


// --- Pantalla Principal de Chat ---

@Composable
fun PatientChatScreen(
    viewModel: PatientChatViewModel,
    onNavigateBack: () -> Unit
) {
    // --- 4. OBTENER ESTADO DEL VIEWMODEL ---
    val uiState by viewModel.uiState.collectAsState()
    val summaryState by viewModel.summaryState.collectAsState()
    var text by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Observa los mensajes para hacer scroll al último
    LaunchedEffect(uiState.messages) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        ChatHeader(
            summaryState = summaryState,
            onNavigateBack = onNavigateBack
        )

        if (uiState.isLoading && uiState.messages.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Error: ${uiState.error}", color = Color.Red)
            }
        } else {

            ChatContent(
                messages = uiState.messages,
                modifier = Modifier.weight(1f),
                listState = listState,
                viewModel = viewModel
            )
            ChatInput(
                text = text,
                onTextChange = { text = it },
                onSend = {
                    if (text.isNotBlank()) {
                        viewModel.sendMessage(text)
                        text = ""
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHeader(summaryState: PatientSummaryState, onNavigateBack: () -> Unit) {

    Surface(
        color = Color(0xFFCBCD9D),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box {
                    // Placeholder para la imagen de perfil
                    AsyncImage(
                        model = summaryState.profilePhotoUrl,
                        contentDescription = "Foto de perfil del paciente",
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.Gray, CircleShape),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.ic_profile_user),
                        error = painterResource(id = R.drawable.ic_profile_user)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = summaryState.patientName,
                        style = CrimsonSemiBold.copy(fontSize = 23.sp),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInput(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            placeholder = { Text("Escribe un mensaje...") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = onSend,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF9BA9B0))
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Enviar",
                tint = Color.White
            )
        }
    }
}

// --- Contenido del Chat (Burbujas) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatContent(
    messages: List<ChatMessage>,
    modifier: Modifier = Modifier,
    listState: androidx.compose.foundation.lazy.LazyListState,
    viewModel: PatientChatViewModel
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        reverseLayout = true, // Muestra los mensajes más nuevos abajo
        state = listState
    ) {
        // Espacio al final (que es el "fondo" al hacer scroll)
        item { Spacer(modifier = Modifier.height(16.dp)) }

        // --- 8. USAR LA LISTA DE ChatMessage ---
        items(messages, key = { it.id }) { message ->
            ChatBubble(
                message = message,
                time = viewModel.formatTimestamp(message.timestamp) // Formatear la hora
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Espacio al inicio (que es el "tope" al hacer scroll)
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBubble(
    message: ChatMessage,
    time: String // <-- Recibe la hora ya formateada
) {
    val horizontalAlignment = if (message.isFromMe) Alignment.End else Alignment.Start
    val color = if (message.isFromMe) chatBubbleUser else chatBubbleOther

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = horizontalAlignment
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isFromMe) 16.dp else 0.dp,
                bottomEnd = if (message.isFromMe) 0.dp else 16.dp
            ),
            color = color,
            modifier = Modifier.widthIn(max = 300.dp),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0))
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(
                    text = message.content,
                    fontSize = 15.sp,
                    color = Color.Black
                )
                Text(
                    text = time, // <-- Hora formateada
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}