package com.softfocus.features.crisis.presentation.components

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.R
import com.softfocus.features.crisis.presentation.CrisisState
import com.softfocus.features.crisis.presentation.CrisisViewModel
import com.softfocus.features.crisis.presentation.di.crisisViewModel
import com.softfocus.ui.theme.CrimsonBold
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.GrayD9
import com.softfocus.ui.theme.RedE8
import com.softfocus.ui.theme.SourceSansSemiBold
import com.softfocus.ui.theme.White
import java.util.concurrent.TimeUnit

@Composable
fun CrisisButton(
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    viewModel: CrisisViewModel = crisisViewModel()
) {
    val context = LocalContext.current
    val crisisState by viewModel.crisisState.collectAsState()
    var lastClickTime by remember { mutableLongStateOf(0L) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    val clickCooldownMs = TimeUnit.HOURS.toMillis(1)

    LaunchedEffect(crisisState) {
        when (crisisState) {
            is CrisisState.Success -> {
                Toast.makeText(context, "Alerta de crisis enviada exitosamente", Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            is CrisisState.Error -> {
                val error = (crisisState as CrisisState.Error).message
                Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            containerColor = RedE8,
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text(
                    text = "¿Necesitas ayuda inmediata?",
                    style = CrimsonBold,
                    fontSize = 24.sp,
                    color = White,
                    lineHeight = 32.sp,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                TextButton(
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = GrayD9
                    ),
                    onClick = {
                        showConfirmDialog = false
                        lastClickTime = System.currentTimeMillis()
                        viewModel.sendCrisisAlert(context)
                    }
                ) {
                    Text("Sí, necesito ayuda", color = RedE8, style = SourceSansSemiBold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmDialog = false },
                    border = BorderStroke(2.dp, GrayD9)
                ) {
                    Text("Fue sin querer", color = GrayD9, style = SourceSansSemiBold)
                }
            }
        )
    }

    Surface(
        onClick = {
            val currentTime = System.currentTimeMillis()
            val timeSinceLastClick = currentTime - lastClickTime

            if (crisisState !is CrisisState.Loading && timeSinceLastClick >= clickCooldownMs) {
                showConfirmDialog = true
            } else if (timeSinceLastClick < clickCooldownMs) {
                val remainingMinutes = TimeUnit.MILLISECONDS.toMinutes(clickCooldownMs - timeSinceLastClick)
                Toast.makeText(context, "Ya enviaste una alerta. Espera ${remainingMinutes + 1} minutos", Toast.LENGTH_LONG).show()
            }
        },
        shape = RoundedCornerShape(12.dp),
        color = RedE8,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SOS",
                style = CrimsonSemiBold,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))

            if (crisisState is CrisisState.Loading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.warning_icon),
                    contentDescription = "Warning Icon",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}