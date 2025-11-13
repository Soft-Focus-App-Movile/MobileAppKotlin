package com.softfocus.features.therapy.presentation.psychologist.patiendetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.features.therapy.presentation.psychologist.patiendetail.PatientCheckInState
import com.softfocus.features.therapy.presentation.psychologist.patiendetail.cardBackground
import com.softfocus.features.therapy.presentation.psychologist.patiendetail.lightGrayText
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.SourceSansRegular

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LastCheckInCard(state: PatientCheckInState) {

    val checkIn = state.lastCheckIn

    if (checkIn == null) {
        // Muestra un mensaje si no hay registros
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay registros de check-in.",
                color = lightGrayText,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
    } else {
        // Muestra los datos del check-in
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = state.formattedDate,
                        style = CrimsonSemiBold.copy(fontSize = 20.sp),
                        color = Color.Black
                    )
                    Text(
                        text = "${checkIn.emotionalLevel}/10", // Nivel de como se siente el paciente "${emotionalLevel}/10"
                        modifier = Modifier
                            .background(
                                color = Color(0xFFCBCD9C),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        style = SourceSansRegular.copy(fontSize = 10.sp),
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                if(checkIn.symptoms.isNotEmpty()) {
                    Row {
                        checkIn.symptoms.forEach { tagText -> // Lista de síntomas (symptoms)
                            TagItem(text = tagText)
                            Spacer(modifier = Modifier.width(8.dp)) // Espacio entre tags
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                checkIn.notes?.let {
                    Text(
                        text = it, // Notas del paciente (notes)
                        style = SourceSansRegular.copy(fontSize = 12.sp),
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun TagItem(text: String) { //Para los síntomas del paciente
    Text(
        text = text,
        modifier = Modifier
            .background(
                color = Color(0xFFCBCD9C),
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        style = SourceSansRegular.copy(fontSize = 10.sp),
        color = Color.White
    )
}