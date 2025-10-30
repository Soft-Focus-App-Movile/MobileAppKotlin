package com.softfocus.core.ui.components.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.softfocus.core.navigation.Route

@Composable
fun GeneralBottomNav(navController: NavController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar(
        containerColor = Color.White
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = if (currentRoute == Route.Home.path) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Inicio",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Inicio", fontSize = 12.sp) },
            selected = currentRoute == Route.Home.path,
            onClick = {
                if (currentRoute != Route.Home.path) {
                    navController.navigate(Route.Home.path) {
                        popUpTo(Route.Home.path) { inclusive = true }
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF6B8E6F),
                selectedTextColor = Color(0xFF6B8E6F),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_edit),
                    contentDescription = "Diario",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Diario", fontSize = 12.sp) },
            selected = false,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF6B8E6F),
                selectedTextColor = Color(0xFF6B8E6F),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_dialog_info),
                    contentDescription = "IA",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("IA", fontSize = 12.sp) },
            selected = false,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF6B8E6F),
                selectedTextColor = Color(0xFF6B8E6F),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                    contentDescription = "Biblioteca",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Biblioteca", fontSize = 12.sp) },
            selected = false,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF6B8E6F),
                selectedTextColor = Color(0xFF6B8E6F),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = if (currentRoute == Route.Profile.path) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Perfil",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Perfil", fontSize = 12.sp) },
            selected = currentRoute == Route.Profile.path,
            onClick = {
                if (currentRoute != Route.Profile.path) {
                    navController.navigate(Route.Profile.path)
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF6B8E6F),
                selectedTextColor = Color(0xFF6B8E6F),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GeneralBottomNavPreview() {
    val navController = rememberNavController()
    GeneralBottomNav(navController)
}
