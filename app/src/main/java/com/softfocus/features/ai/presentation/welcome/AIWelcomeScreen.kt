package com.softfocus.features.ai.presentation.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.R
import com.softfocus.features.ai.domain.models.ChatSession
import com.softfocus.features.ai.presentation.chat.components.SuggestedQuestionChip
import com.softfocus.features.ai.presentation.di.AIPresentationModule
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Green29
import com.softfocus.ui.theme.Green65
import com.softfocus.ui.theme.GreenC0
import com.softfocus.ui.theme.SourceSansRegular
import com.softfocus.ui.theme.White
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Custom shape that creates an oval bottom edge going from border to border
 */
class OvalBottomShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            // Start at top-left corner
            moveTo(0f, 0f)

            // Draw top edge
            lineTo(size.width, 0f)

            // Draw right edge
            lineTo(size.width, size.height - 100f)

            // Draw oval bottom curve from right to left
            quadraticBezierTo(
                size.width / 2f, size.height + 100f,
                0f, size.height - 100f
            )

            // Close path back to start
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun AIWelcomeScreen(
    onSendMessage: (String) -> Unit,
    onClose: () -> Unit,
    onSessionClick: (String) -> Unit = {},
    onNavigateToEmotionDetection: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel = remember { AIPresentationModule.getAIWelcomeViewModel(context) }
    val state by viewModel.state.collectAsState()
    var message by remember { mutableStateOf("") }

    val suggestedQuestions = listOf(
        "Me siento ansioso",
        "Necesito hablar",
        "Consejos para dormir",
        "Técnicas de relajación"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        // Gradient background with oval bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(0.85f)
                .clip(OvalBottomShape())
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Green65,  // 0%
                            GreenC0   // 100%
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.1f))
            Text(
                text = "Bienvenido al chat de Focus",
                style = CrimsonSemiBold,
                fontSize = 28.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            Icon(
                painter = painterResource(id = R.drawable.focus_ia),
                contentDescription = "Focus Panda",
                tint = Color.Unspecified,
                modifier = Modifier.size(190.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            TextField(
                value = message,
                onValueChange = { message = it },
                placeholder = {
                    Text(
                        text = "Cuéntame algo ....",
                        style = SourceSansRegular,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                },
                trailingIcon = {
                    Row {
                        IconButton(
                            onClick = onNavigateToEmotionDetection
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Detectar emoción",
                                tint = Green29
                            )
                        }
                        IconButton(
                            onClick = {
                                if (message.isNotBlank()) {
                                    onSendMessage(message)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Enviar",
                                tint = Green29
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                suggestedQuestions.take(2).forEach { question ->
                    SuggestedQuestionChip(
                        question = question,
                        onClick = { onSendMessage(question) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                suggestedQuestions.drop(2).forEach { question ->
                    SuggestedQuestionChip(
                        question = question,
                        onClick = { onSendMessage(question) }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.3f))

            if (state.sessions.isNotEmpty()) {
                Text(
                    text = "Conversaciones recientes",
                    style = SourceSansRegular,
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, bottom = 8.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    )
                ) {
                    if (state.isLoadingSessions) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Green29)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp)
                        ) {
                            items(state.sessions) { session ->
                                SessionItem(
                                    session = session,
                                    onClick = { onSessionClick(session.sessionId) }
                                )
                                if (session != state.sessions.last()) {
                                    Divider(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        color = Color.Gray.copy(alpha = 0.3f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Close button on top of everything
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(35.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cerrar",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun SessionItem(
    session: ChatSession,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ia_button),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Título: lastMessagePreview o fecha como fallback
            Text(
                text = session.lastMessagePreview?.takeIf { it.isNotBlank() }
                    ?: session.lastMessageAt.format(
                        DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
                    ),
                style = SourceSansRegular,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))


            Text(
                text = "${session.lastMessageAt.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))} • ${session.messageCount} mensajes",
                style = SourceSansRegular,
                fontSize = 12.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AIWelcomeScreenPreview() {
    AIWelcomeScreen(onSendMessage = {}, onClose = {}, onNavigateToEmotionDetection = {})
}
