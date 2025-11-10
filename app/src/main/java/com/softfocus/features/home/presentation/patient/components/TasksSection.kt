package com.softfocus.features.home.presentation.patient.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.R
import com.softfocus.ui.theme.Black
import com.softfocus.ui.theme.CrimsonMixed
import com.softfocus.ui.theme.Green65
import com.softfocus.ui.theme.SourceSansRegular
import com.softfocus.ui.theme.White
import com.softfocus.ui.theme.YellowCB9D

@Composable
fun TasksSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .background(color = White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(4.dp, YellowCB9D),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header con icono y título
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.tasks_icon),
                    contentDescription = null,
                    tint = Green65,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tienes 2 tareas por completar",
                    style = CrimsonMixed,
                    fontSize = 16.sp,
                    color = Green65
                )
            }

            // Lista de tareas individuales
            TaskItem(title = "Ejercicio de respiración 4-7-8")
            Spacer(modifier = Modifier.height(8.dp))
            TaskItem(title = "Video: Meditación guiada")
        }
    }
}

@Composable
fun TaskItem(title: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = YellowCB9D
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox placeholder (cuadrito sin check)
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(3.dp)
                    )
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                style = SourceSansRegular,
                fontSize = 14.sp,
                color = Black
            )
        }
    }
}
