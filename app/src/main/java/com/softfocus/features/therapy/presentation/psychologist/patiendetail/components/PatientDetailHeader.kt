package com.softfocus.features.therapy.presentation.psychologist.patiendetail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.features.therapy.presentation.psychologist.patiendetail.PatientSummaryState
import com.softfocus.ui.components.ProfileAvatar
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.Gray89
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.Green65
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
        ProfileAvatar(
            imageUrl = summaryState.profilePhotoUrl.takeIf { it.isNotEmpty() },
            fullName = summaryState.patientName,
            size = 100.dp,
            fontSize = 30.sp,
            backgroundColor = Color(0xFFE8F5E9),
            textColor = Green49,
            shape = CircleShape
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
            color = Gray89
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = summaryState.formattedStartDate,
            style = SourceSansSemiBold.copy(fontSize = 13.sp),
            color = Green65
        )
    }
}