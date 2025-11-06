package com.softfocus.ui.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.softfocus.R
import com.softfocus.core.navigation.Route
import com.softfocus.ui.theme.Green29
import com.softfocus.ui.theme.SourceSansRegular

@Composable
private fun BottomNavIcon(
    isSelected: Boolean,
    content: @Composable () -> Unit
) {
    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        content()
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(10.dp)
                .height(5.dp)
                .background(
                    if (isSelected) Green29 else Color.Transparent,
                    RoundedCornerShape(3.dp)
                )
        )
    }
}

@Composable
fun PatientBottomNav(navController: NavController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Surface(
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        color = Color.White,
        shadowElevation = 0.dp
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            contentColor = Green29,
            modifier = Modifier.clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
        ) {
        NavigationBarItem(
            icon = {
                BottomNavIcon(isSelected = currentRoute == Route.Home.path) {
                    Icon(
                        painter = painterResource(
                            id = if (currentRoute == Route.Home.path)
                                R.drawable.ic_home_rounded_filled
                            else
                                R.drawable.ic_home_rounded_outlined
                        ),
                        contentDescription = "Inicio",
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            label = { Text("Inicio", fontSize = 12.sp, style = SourceSansRegular) },
            selected = currentRoute == Route.Home.path,
            onClick = {
                if (currentRoute != Route.Home.path) {
                    navController.navigate(Route.Home.path) {
                        popUpTo(Route.Home.path) { inclusive = true }
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Green29,
                selectedTextColor = Green29,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            icon = {
                BottomNavIcon(isSelected = false) {
                    Icon(
                        imageVector = Icons.Outlined.Book,
                        contentDescription = "Diario",
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            label = { Text("Diario", fontSize = 12.sp, style = SourceSansRegular) },
            selected = false,
            onClick = { /* No implementado aún */ },
            enabled = false,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Green29,
                selectedTextColor = Green29,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                disabledIconColor = Color.LightGray,
                disabledTextColor = Color.LightGray,
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            icon = {
                BottomNavIcon(isSelected = false) {
                    Icon(
                        imageVector = Icons.Outlined.Psychology,
                        contentDescription = "Mi terapeuta",
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            label = { Text("Mi terapeuta", fontSize = 12.sp, style = SourceSansRegular) },
            selected = false,
            onClick = { /* No implementado aún */ },
            enabled = false,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Green29,
                selectedTextColor = Green29,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                disabledIconColor = Color.LightGray,
                disabledTextColor = Color.LightGray,
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            icon = {
                BottomNavIcon(isSelected = false) {
                    Icon(
                        imageVector = Icons.Outlined.Bookmarks,
                        contentDescription = "Biblioteca",
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            label = { Text("Biblioteca", fontSize = 12.sp, style = SourceSansRegular) },
            selected = false,
            onClick = { /* TODO: Implementar para Patient */ },
            enabled = false,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Green29,
                selectedTextColor = Green29,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                disabledIconColor = Color.LightGray,
                disabledTextColor = Color.LightGray,
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            icon = {
                BottomNavIcon(isSelected = currentRoute == Route.PatientProfile.path) {
                    Icon(
                        imageVector = if (currentRoute == Route.PatientProfile.path) Icons.Filled.Person else Icons.Outlined.Person,
                        contentDescription = "Perfil",
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            label = { Text("Perfil", fontSize = 12.sp, style = SourceSansRegular) },
            selected = currentRoute == Route.PatientProfile.path,
            onClick = {
                if (currentRoute != Route.PatientProfile.path) {
                    navController.navigate(Route.PatientProfile.path)
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Green29,
                selectedTextColor = Green29,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )
        }
    }
}

@Preview()
@Composable
fun PatientBottomNavPreview() {
    val navController = rememberNavController()
    PatientBottomNav(navController)
}
