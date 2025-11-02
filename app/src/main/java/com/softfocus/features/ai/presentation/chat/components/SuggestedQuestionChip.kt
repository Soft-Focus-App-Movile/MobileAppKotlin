package com.softfocus.features.ai.presentation.chat.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.ui.theme.Green29
import com.softfocus.ui.theme.GreenA3
import com.softfocus.ui.theme.GreenBF2
import com.softfocus.ui.theme.InterRegular
import com.softfocus.ui.theme.SourceSansRegular

@Composable
fun SuggestedQuestionChip(
    question: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AssistChip(
        onClick = onClick,
        label = {
            Text(
                text = question,
                style = InterRegular,
                fontSize = 13.sp
            )
        },
        modifier = modifier.padding(end = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = AssistChipDefaults.assistChipColors(
            containerColor = GreenBF2,
            labelColor = Green29
        )
    )
}

@Preview(showBackground = true)
@Composable
fun SuggestedQuestionChipPreview() {
    SuggestedQuestionChip(
        question = "Me siento ansioso",
        onClick = {}
    )
}
