package com.softfocus.features.auth.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.softfocus.R
import com.softfocus.core.ui.theme.SoftFocusTheme
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.GreenC1
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit = {}
) {

    val isAuthenticated = false

    LaunchedEffect(Unit) {
        delay(2500) // 2.5 seconds
        if (isAuthenticated) {
            onNavigateToHome()
        } else {
            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GreenC1,
                        Green49
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Panda logo
        Image(
            painter = painterResource(id = R.drawable.panda_soft),
            contentDescription = "Soft Focus Logo",
            modifier = Modifier.size(550.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SoftFocusTheme {
        SplashScreen(
            onNavigateToLogin = {},
            onNavigateToHome = {}
        )
    }
}
