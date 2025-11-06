package com.softfocus.features.profile.presentation.psychologist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.softfocus.features.home.presentation.psychologist.PsychologistHomeViewModel
import com.softfocus.ui.components.InvitationCard
import com.softfocus.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyInvitationCodeScreen(
    onNavigateBack: () -> Unit,
    viewModel: PsychologistHomeViewModel = hiltViewModel()
) {
    val invitationCode = viewModel.invitationCode.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mi código de invitación",
                        style = CrimsonSemiBold,
                        color = Green37,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Green37
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading.value) {
                CircularProgressIndicator()
            } else {
                InvitationCard(
                    code = invitationCode.value?.code ?: "ABC123XY",
                    onCopyClick = { viewModel.copyCodeToClipboard() },
                    onShareClick = { viewModel.shareCode() },
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyInvitationCodeScreenPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        InvitationCard(
            code = "ABC123XY",
            onCopyClick = { },
            onShareClick = { },
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}
