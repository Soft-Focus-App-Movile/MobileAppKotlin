package com.softfocus.features.home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.R
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Green65
import com.softfocus.ui.theme.GreenEC
import com.softfocus.ui.theme.SourceSansRegular
import com.softfocus.ui.theme.YellowCB9D

/**
 * Componente de bienvenida que muestra el saludo y la card de registro de ánimo
 */
@Composable
fun WelcomeCard(
    userName: String,
    onRegisterMoodClick: () -> Unit = {}
) {
    Column {
        Text(
            text = "Hola $userName ,",
            style = CrimsonSemiBold,
            fontSize = 24.sp,
            color = Green65,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Text(
            text = "¿Cómo te sientes hoy?",
            style = CrimsonSemiBold,
            fontSize = 24.sp,
            color = Green65,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GreenEC),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .padding(end = 80.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Registra tu estado de ánimo",
                            style = CrimsonSemiBold,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onRegisterMoodClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = YellowCB9D
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Registrar ahora",
                                style = SourceSansRegular,
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            // Koala image overlapping the card
            Image(
                painter = painterResource(id = R.drawable.koala_focus),
                contentDescription = "Koala",
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.CenterEnd)
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        // Reduce the layout height by the amount the image is moved up
                        val height = placeable.height - 30.dp.roundToPx()
                        layout(placeable.width, height) {
                            // Place the image with both offsets
                            placeable.placeRelative(35.dp.roundToPx(), -30.dp.roundToPx())
                        }
                    }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeCardPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        WelcomeCard(
            userName = "Laura",
            onRegisterMoodClick = {}
        )
    }
}
