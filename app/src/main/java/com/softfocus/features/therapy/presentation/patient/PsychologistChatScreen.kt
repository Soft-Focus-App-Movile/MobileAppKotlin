package com.softfocus.features.therapy.presentation.patient

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.softfocus.core.navigation.Route
import com.softfocus.features.therapy.domain.models.ChatMessage
import com.softfocus.ui.components.ProfileAvatar
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Gray9B
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.GreenE7
import com.softfocus.ui.theme.GreenF8
import com.softfocus.ui.theme.Transparent
import com.softfocus.ui.theme.White

// --- Pantalla Principal de Chat ---

@Composable
fun PsychologistChatScreen(
    viewModel: PsychologistChatViewModel,
    navController: NavHostController
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
            .background(GreenF8)
    ) {

        ChatHeader(
            summaryState = summaryState,
            navController = navController
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
fun ChatHeader(summaryState: PsychologistSummaryState, navController: NavHostController) {
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
            // Contenedor clickeable para imagen y nombre
            Row(
                modifier = Modifier.clickable {
                    navController.navigate(
                        Route.PsychologistChatProfile.path
                    )
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileAvatar(
                    imageUrl = summaryState.profilePhotoUrl.takeIf { it.isNotEmpty() },
                    fullName = summaryState.psychologistName,
                    size = 44.dp,
                    fontSize = 21.sp,
                    backgroundColor = Color(0xFFE8F5E9),
                    textColor = Green49,
                    shape = CircleShape
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = summaryState.psychologistName,
                        style = CrimsonSemiBold.copy(fontSize = 25.sp),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f)) // Empuja el ícono a la derecha

            IconButton(onClick = {
                navController.navigate(
                    Route.Library.path
                )
            }) {
                Icon(
                    imageVector = Icons.Outlined.ContentPaste,
                    contentDescription = "Botón de portapapeles",
                    modifier = Modifier.size(28.dp),
                    tint = Color.White
                )
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
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            placeholder = { Text("Escribe un mensaje...") },
            modifier = Modifier
                .border(1.dp, Gray9B, RoundedCornerShape(24.dp))
                .weight(1f)
                .background(White, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        IconButton(
            onClick = onSend,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Transparent)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Enviar",
                tint = Gray9B
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
    viewModel: PsychologistChatViewModel
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
    val color = if (message.isFromMe) GreenE7 else White

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
            border = BorderStroke(0.5.dp, Gray9B)
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