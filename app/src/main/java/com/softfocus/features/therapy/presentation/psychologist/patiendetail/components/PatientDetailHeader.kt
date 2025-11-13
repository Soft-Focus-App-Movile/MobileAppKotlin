package com.softfocus.features.therapy.presentation.psychologist.patiendetail.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.softfocus.R
import com.softfocus.features.therapy.presentation.psychologist.patiendetail.PatientSummaryState
import com.softfocus.features.therapy.presentation.psychologist.patiendetail.lightGrayText
import com.softfocus.features.therapy.presentation.psychologist.patiendetail.primaryGreen
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.SourceSansSemiBold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientDetailHeader(summaryState: PatientSummaryState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Placeholder para la imagen
        Image(
            painter = rememberAsyncImagePainter(
                model = summaryState.profilePhotoUrl,
                placeholder = painterResource(id = R.drawable.ic_profile_user),
                error = painterResource(id = R.drawable.ic_profile_user)
            ),
            contentDescription = "Foto de ${summaryState.patientName}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp) // Puedes ajustar este tamaño
                .clip(CircleShape)
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = summaryState.patientName,
            style = CrimsonSemiBold.copy(fontSize = 30.sp),
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${summaryState.age} años",
            style = SourceSansSemiBold.copy(fontSize = 13.sp),
            color = lightGrayText
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = summaryState.formattedStartDate,
            style = SourceSansSemiBold.copy(fontSize = 13.sp),
            color = primaryGreen
        )
    }
}