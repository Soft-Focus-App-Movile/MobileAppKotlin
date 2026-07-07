package com.softfocus.features.subscription.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.softfocus.features.subscription.domain.models.SubscriptionPlan
import com.softfocus.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPlanScreen(
    onNavigateBack: () -> Unit,
    viewModel: SubscriptionViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val checkoutUrl by viewModel.checkoutUrl.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showWebView by remember { mutableStateOf(false) }

    if (showWebView && checkoutUrl != null) {
        StripeCheckoutWebView(
            checkoutUrl = checkoutUrl!!,
            onSuccess = { sessionId ->
                android.util.Log.d("MyPlanScreen", "Payment success! SessionId: $sessionId")
                showWebView = false
                viewModel.handleCheckoutSuccess(sessionId)
            },
            onCancel = {
                android.util.Log.d("MyPlanScreen", "Payment cancelled by user")
                showWebView = false
                viewModel.clearCheckoutUrl()
            },
            onDismiss = {
                android.util.Log.d("MyPlanScreen", "WebView dismissed")
                showWebView = false
                viewModel.clearCheckoutUrl()
            }
        )
        return
    }

    LaunchedEffect(checkoutUrl) {
        if (checkoutUrl != null) {
            showWebView = true
        } else if (showWebView) {
            // WebView was closed, ensure we have the latest subscription data
            android.util.Log.d("MyPlanScreen", "WebView closed, refreshing subscription data")
            showWebView = false
        }
    }

    errorMessage?.let { message ->
        LaunchedEffect(message) {
            android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mi plan",
                        style = CrimsonSemiBold,
                        fontSize = 25.sp,
                        color = GreenA3
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Atrás",
                            tint = GreenA3
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.background
                )
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is SubscriptionUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Green49)
                }
            }
            is SubscriptionUiState.Success -> {
                PlanContent(
                    modifier = Modifier.padding(paddingValues),
                    plan = state.subscription.plan,
                    isActive = state.subscription.isActive,
                    usageLimits = state.subscription.usageLimits,
                    isLoading = isLoading,
                    onUpgradeToPro = {
                        viewModel.upgradeToPro(
                            successUrl = "softfocus://subscription/success",
                            cancelUrl = "softfocus://subscription/cancel"
                        )
                    }
                )
            }
            is SubscriptionUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.message,
                        style = SourceSansRegular,
                        fontSize = 16.sp,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                }
            }
            is SubscriptionUiState.IsPatient -> {
            }
        }
    }
}

@Composable
fun PlanContent(
    modifier: Modifier = Modifier,
    plan: SubscriptionPlan,
    isActive: Boolean,
    usageLimits: com.softfocus.features.subscription.domain.models.UsageLimits,
    isLoading: Boolean,
    onUpgradeToPro: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Green29
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "USUARIO",
                    style = CrimsonSemiBold,
                    fontSize = 16.sp,
                    color = White,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (plan == SubscriptionPlan.PRO) "Plan Pro" else "Plan Gratuito",
                    style = CrimsonSemiBold,
                    fontSize = 32.sp,
                    color = White
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (plan == SubscriptionPlan.BASIC) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        PlanFeatureItem("Check-ins básicos diarios")
                        PlanFeatureItem("Acceso limitado a recursos emocionales")
                        PlanFeatureItem("Recordatorios simples")
                        PlanFeatureItem("Comunidad de apoyo")
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = onUpgradeToPro,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = White,
                            contentColor = Green29
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Green29
                            )
                        } else {
                            Text(
                                text = "Cambiar Plan Pro",
                                style = CrimsonSemiBold,
                                fontSize = 18.sp
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        PlanFeatureItem("Check-ins ilimitados")
                        PlanFeatureItem("Acceso completo a recursos emocionales")
                        PlanFeatureItem("IA personalizada avanzada")
                        PlanFeatureItem("Análisis de emociones ilimitado")
                        PlanFeatureItem("Soporte prioritario")
                        PlanFeatureItem("Sin anuncios")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.outlinedCardColors(
                containerColor = Color.Transparent
            ),
            border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
        ) {
            Text(
                text = if (plan == SubscriptionPlan.BASIC) "Gratuito" else "Pro",
                modifier = Modifier.padding(16.dp),
                style = CrimsonSemiBold,
                fontSize = 20.sp,
                color = Green29
            )
        }
    }
}

@Composable
private fun PlanFeatureItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "• ",
            style = SourceSansRegular,
            fontSize = 18.sp,
            color = White,
            modifier = Modifier.padding(end = 4.dp)
        )
        Text(
            text = text,
            style = SourceSansRegular,
            fontSize = 16.sp,
            color = White,
            lineHeight = 20.sp,
            softWrap = true,
            overflow = androidx.compose.ui.text.style.TextOverflow.Visible,
            modifier = Modifier.weight(1f)
        )
    }
}
