package com.softfocus.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.ui.theme.Black
import com.softfocus.ui.theme.Gray828
import com.softfocus.ui.theme.Green29
import com.softfocus.ui.theme.GreenA3
import com.softfocus.ui.theme.SourceSansRegular
import com.softfocus.ui.theme.ThemeManager
import com.softfocus.ui.theme.ThemeMode
import com.softfocus.ui.theme.White

/**
 * Fila de configuración con un Switch para activar/desactivar el modo oscuro.
 * Comparte el estilo (tarjeta verde oliva) de las demás opciones del menú de perfil.
 * Al cambiar el switch, [ThemeManager] actualiza el tema de toda la app al instante.
 */
@Composable
fun DarkModeToggleCard() {
    val context = LocalContext.current
    val isDark = when (ThemeManager.mode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GreenA3)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (isDark) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                    contentDescription = null,
                    tint = Black,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Modo oscuro",
                    style = SourceSansRegular,
                    fontSize = 16.sp,
                    color = Black
                )
            }
            Switch(
                checked = isDark,
                onCheckedChange = { checked ->
                    ThemeManager.setMode(context, if (checked) ThemeMode.DARK else ThemeMode.LIGHT)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = White,
                    checkedTrackColor = Green29,
                    uncheckedThumbColor = White,
                    uncheckedTrackColor = Gray828
                )
            )
        }
    }
}
