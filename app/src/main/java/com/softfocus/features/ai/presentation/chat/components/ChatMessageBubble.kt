package com.softfocus.features.ai.presentation.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.features.ai.domain.models.MessageRole
import com.softfocus.ui.theme.Gray222
import com.softfocus.ui.theme.Green65
import com.softfocus.ui.theme.SourceSansRegular
import com.softfocus.ui.theme.White
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.softfocus.features.ai.domain.models.ChatMessage
import com.softfocus.ui.theme.InterRegular

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    val isUser = message.role == MessageRole.USER
    val bubbleColor = if (isUser) Green65 else Gray222
    val textColor = White

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .background(
                        color = bubbleColor,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = message.content,
                    style = InterRegular,
                    fontSize = 14.sp,
                    color = textColor,
                    lineHeight = 20.sp,
                    softWrap = true
                )
            }

            Text(
                text = message.timestamp.format(DateTimeFormatter.ofPattern("HH:mm")),
                style = InterRegular,
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatMessageBubbleUserPreview() {
    ChatMessageBubble(
        message = ChatMessage(
            role = MessageRole.USER,
            content = "Hola, me siento ansioso hoy",
            timestamp = LocalDateTime.now()
        )
    )
}

@Preview(showBackground = true)
@Composable
fun ChatMessageBubbleAssistantPreview() {
    ChatMessageBubble(
        message = ChatMessage(
            role = MessageRole.ASSISTANT,
            content = "Entiendo que te sientas ansioso. ¿Quieres hablar sobre lo que está pasando?",
            timestamp = LocalDateTime.now(),
            suggestedQuestions = listOf("Sí, cuéntame más", "Necesito técnicas de relajación")
        )
    )
}
