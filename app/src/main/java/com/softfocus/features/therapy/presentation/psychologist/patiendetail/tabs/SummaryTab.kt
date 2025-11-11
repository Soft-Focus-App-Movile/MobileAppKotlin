package com.softfocus.features.therapy.presentation.psychologist.patiendetail.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState // --- AÑADIR IMPORT ---
import androidx.compose.runtime.getValue // --- AÑADIR IMPORT ---
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.features.therapy.presentation.psychologist.patiendetail.PatientDetailViewModel
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.SourceSansSemiBold

// Colores
private val lightGrayText = Color(0xFF8B8B8B)

@Composable
fun SummaryTab(
    viewModel: PatientDetailViewModel // Recibe el ViewModel
) {
    // --- AÑADIR ESTO ---
    // Recolecta el estado de resumen del ViewModel
    val summaryState by viewModel.summaryState.collectAsState()
    // --- FIN DE LO AÑADIDO ---

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // --- AQUÍ ESTÁ EL HEADER QUE MENCIONASTE ---
        // PatientDetailHeader Composable

        Text(
            // --- MODIFICAR ---
            text = summaryState.patientName,
            style = CrimsonSemiBold.copy(fontSize = 30.sp),
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            // --- MODIFICAR ---
            text = "${summaryState.age} años",
            style = SourceSansSemiBold.copy(fontSize = 13.sp),
            color = lightGrayText
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            // --- MODIFICAR ---
            text = summaryState.formattedStartDate,
            style = SourceSansSemiBold.copy(fontSize = 13.sp),
            color = Color(0xFF4B634B)
        )

        // ... (Aquí iría el resto del contenido de SummaryTab, como "Total Sesiones", etc.)
    }
}