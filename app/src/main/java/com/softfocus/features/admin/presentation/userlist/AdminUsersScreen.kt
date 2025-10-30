package com.softfocus.features.admin.presentation.userlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.features.admin.domain.models.AdminUser
import com.softfocus.ui.theme.*

@Composable
fun AdminUsersScreen(
    viewModel: AdminUsersViewModel,
    onNavigateToVerify: (String) -> Unit,
    onLogout: () -> Unit
) {
    val users by viewModel.users.collectAsState()
    val paginationInfo by viewModel.paginationInfo.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val searchTerm by viewModel.searchTerm.collectAsState()
    val filterUserType by viewModel.filterUserType.collectAsState()
    val filterIsVerified by viewModel.filterIsVerified.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Gestión de Usuarios",
                style = CrimsonSemiBold,
                fontSize = 28.sp,
                color = Green49
            )

            IconButton(onClick = onLogout) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Cerrar sesión",
                    tint = Green49
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchTerm,
            onValueChange = { viewModel.updateSearchTerm(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar por nombre o email") },
            trailingIcon = {
                IconButton(onClick = { viewModel.search() }) {
                    Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Green37)
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Green37,
                unfocusedIndicatorColor = GrayE0
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filterUserType == null,
                onClick = { viewModel.setFilterUserType(null) },
                label = { Text("Todos") }
            )
            FilterChip(
                selected = filterUserType == "Psychologist",
                onClick = { viewModel.setFilterUserType("Psychologist") },
                label = { Text("Psicólogos") }
            )
            FilterChip(
                selected = filterUserType == "General",
                onClick = { viewModel.setFilterUserType("General") },
                label = { Text("Generales") }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filterIsVerified == null,
                onClick = { viewModel.setFilterIsVerified(null) },
                label = { Text("Todos") }
            )
            FilterChip(
                selected = filterIsVerified == false,
                onClick = { viewModel.setFilterIsVerified(false) },
                label = { Text("No verificados") }
            )
            FilterChip(
                selected = filterIsVerified == true,
                onClick = { viewModel.setFilterIsVerified(true) },
                label = { Text("Verificados") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Green49)
            }
        } else if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(users) { user ->
                    UserItem(
                        user = user,
                        onClick = {
                            if (user.userType == "Psychologist" && user.isVerified == false) {
                                onNavigateToVerify(user.id)
                            }
                        }
                    )
                }
            }

            paginationInfo?.let { pagination ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { viewModel.previousPage() },
                        enabled = pagination.hasPreviousPage,
                        colors = ButtonDefaults.buttonColors(containerColor = Green49)
                    ) {
                        Text("Anterior")
                    }

                    Text(
                        text = "Página ${pagination.page} de ${pagination.totalPages}",
                        style = SourceSansRegular,
                        fontSize = 14.sp
                    )

                    Button(
                        onClick = { viewModel.nextPage() },
                        enabled = pagination.hasNextPage,
                        colors = ButtonDefaults.buttonColors(containerColor = Green49)
                    ) {
                        Text("Siguiente")
                    }
                }
            }
        }
    }
}

@Composable
fun UserItem(
    user: AdminUser,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user.fullName,
                        style = SourceSansBold,
                        fontSize = 16.sp,
                        color = Black
                    )
                    Text(
                        text = user.email,
                        style = SourceSansRegular,
                        fontSize = 14.sp,
                        color = Gray828
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (user.userType) {
                            "Psychologist" -> Green37.copy(alpha = 0.2f)
                            else -> GrayE0
                        }
                    ) {
                        Text(
                            text = when (user.userType) {
                                "Psychologist" -> "Psicólogo"
                                "General" -> "General"
                                else -> user.userType
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = SourceSansRegular,
                            fontSize = 12.sp,
                            color = when (user.userType) {
                                "Psychologist" -> Green49
                                else -> Gray828
                            }
                        )
                    }

                    if (user.userType == "Psychologist" && user.isVerified != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (user.isVerified == true) Green37.copy(alpha = 0.2f) else YellowEB.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = if (user.isVerified == true) "Verificado" else "Pendiente",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = SourceSansRegular,
                                fontSize = 12.sp,
                                color = if (user.isVerified == true) Green49 else Black
                            )
                        }
                    }
                }
            }
        }
    }
}
