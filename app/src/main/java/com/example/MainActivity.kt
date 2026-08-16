package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.MuseFusionViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MuseFusionApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuseFusionApp(
    viewModel: MuseFusionViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val currentNetwork by viewModel.currentNetwork.collectAsState()
    val inspectingCapsule by viewModel.inspectingCapsule.collectAsState()
    val lastExecutionResult by viewModel.lastExecutionResult.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MuseDarkBg,
        topBar = {
            Column(
                modifier = Modifier
                    .background(MuseDarkSurface)
                    .statusBarsPadding()
            ) {
                // Top App Bar
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "SCALEN",
                                color = TextPrimary,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "STACK",
                                color = MuseCyan,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 17.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(GoldAccent)
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "VFV",
                                    color = Color.Black,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    },
                    actions = {
                        // Quick Web3 Status indicator
                        Box(
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(MuseDarkSurfaceElevated)
                                .border(1.dp, MuseBorder, RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(MuseGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "4.28 ETH",
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MuseDarkSurface)
                )

                // Network & Wallet Ribbon
                Web3WalletBar(
                    currentNetwork = currentNetwork,
                    onSwitchNetwork = { viewModel.setNetwork(it) }
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MuseDarkSurface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(listOf(MuseRed.copy(alpha = 0.3f), MuseCyan.copy(alpha = 0.3f))),
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .navigationBarsPadding()
            ) {
                AppTab.entries.forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setTab(tab) },
                        icon = {
                            Text(tab.iconLabel, fontSize = if (isSelected) 20.sp else 16.sp)
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontFamily = FontFamily.Monospace
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MuseCyan,
                            selectedTextColor = MuseCyan,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextMuted,
                            indicatorColor = MuseCyan.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Crossfade(
            targetState = currentTab,
            label = "ScreenTransition",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { tab ->
            when (tab) {
                AppTab.SCALENSTACK -> ScalenStackScreen(viewModel = viewModel)
                AppTab.FUSION_HUB -> FusionHubScreen(viewModel = viewModel)
                AppTab.DIME10_VFV -> Dime10MarketplaceScreen(viewModel = viewModel)
                AppTab.WEB3_REPOS -> Web3ReposScreen(viewModel = viewModel)
                AppTab.REALIO_LAB -> RealioLabScreen(viewModel = viewModel)
                AppTab.BRAND_VAULT -> BrandVaultScreen()
            }
        }
    }

    // Modal Sheet for Inspecting Capsule
    inspectingCapsule?.let { capsule ->
        CapsuleDetailsModal(
            capsule = capsule,
            onDismiss = { viewModel.inspectingCapsule.value = null },
            onDelete = { viewModel.deleteCapsule(capsule) },
            onExecute = { viewModel.executeCapsule(capsule) }
        )
    }

    // Modal for Execution Confirmation Receipt
    lastExecutionResult?.let { result ->
        ExecutionReceiptDialog(
            result = result,
            onDismiss = { viewModel.clearExecutionResult() }
        )
    }
}
