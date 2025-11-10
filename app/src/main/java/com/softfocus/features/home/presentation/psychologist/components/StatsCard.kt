package com.softfocus.features.home.presentation.psychologist.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.R
import com.softfocus.ui.theme.Black
import com.softfocus.ui.theme.CrimsonSemiBold
import com.softfocus.ui.theme.GrayA2
import com.softfocus.ui.theme.Green65
import com.softfocus.ui.theme.SourceSansRegular
import com.softfocus.ui.theme.White

data class StatItem(
    val icon: Int,
    val title: String,
    val value: String,
    val subtitle: String
)

@Composable
fun StatsSection(
    stats: List<StatItem> = listOf(
        StatItem(
            icon = R.drawable.ic_profile_user,
            title = "Pacientes Activos",
            value = "24",
            subtitle = "Hay 20 pacientes activos"
        ),
        StatItem(
            icon = R.drawable.ic_alert,
            title = "Alertas Pendientes",
            value = "3",
            subtitle = "3 alertas"
        ),
        StatItem(
            icon = R.drawable.ic_calendar,
            title = "Sesiones Hoy",
            value = "5",
            subtitle = "5 pacientes te esperan su sesión"
        )
    )
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        stats.forEach { stat ->
            StatCard(
                stat = stat,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StatCard(
    stat: StatItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icono
            Icon(
                painter = painterResource(id = stat.icon),
                contentDescription = stat.title,
                tint = Green65,
                modifier = Modifier.size(24.dp)
            )

            // Título
            Text(
                text = stat.title,
                style = SourceSansRegular,
                fontSize = 12.sp,
                color = Black,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )

            // Valor grande
            Text(
                text = stat.value,
                style = CrimsonSemiBold,
                fontSize = 32.sp,
                color = Green65,
                textAlign = TextAlign.Center
            )

            // Subtítulo
            Text(
                text = stat.subtitle,
                style = SourceSansRegular,
                fontSize = 10.sp,
                color = GrayA2,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp
            )
        }
    }
}
