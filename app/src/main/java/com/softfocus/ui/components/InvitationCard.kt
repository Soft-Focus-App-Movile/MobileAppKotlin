package com.softfocus.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.IntOffset
import com.softfocus.R
import com.softfocus.ui.theme.*

@Composable
fun InvitationCard(
    code: String,
    onCopyClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GreenEC),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                        text = code,
                        style = CrimsonSemiBold,
                        fontSize = 24.sp,
                        color = Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onCopyClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = YellowCB9D,
                                contentColor = Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Copiar",
                                style = SourceSansRegular,
                                fontSize = 11.sp
                            )
                        }
                        Button(
                            onClick = onShareClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = YellowCB9D,
                                contentColor = Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Compartir",
                                style = SourceSansRegular,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Koala image overlapping the card
        Image(
            painter = painterResource(id = R.drawable.koala_focus),
            contentDescription = "Koala",
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.CenterEnd)
                .offset(x =29.dp, y = (-20).dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InvitationCardPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        InvitationCard(
            code = "ABC123XY",
            onCopyClick = { },
            onShareClick = { }
        )
    }
}
