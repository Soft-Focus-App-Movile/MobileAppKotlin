package com.softfocus.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.ui.theme.RedE8
import com.softfocus.ui.theme.SourceSansBold
import com.softfocus.ui.theme.White

/**
 * Botón rojo de "Cerrar Sesión", compartido por los perfiles (general, paciente y psicólogo)
 * para que se vea igual en todos.
 */
@Composable
fun LogoutButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = RedE8),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Outlined.Logout,
            contentDescription = null,
            tint = White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Cerrar Sesión",
            style = SourceSansBold,
            fontSize = 16.sp,
            color = White,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}
