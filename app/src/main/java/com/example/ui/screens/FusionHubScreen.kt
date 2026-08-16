package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.SensoryAudioEngine
import com.example.data.model.FusedCapsule
import com.example.data.model.Totem
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MuseFusionViewModel

@Composable
fun FusionHubScreen(
    viewModel: MuseFusionViewModel,
    modifier: Modifier = Modifier
) {
    val capsules by viewModel.capsules.collectAsState()
    val repos by viewModel.repos.collectAsState()
    val assets by viewModel.assets.collectAsState()
    val selectedTotem by viewModel.selectedTotemFilter.collectAsState()
    val selectedRepo by viewModel.selectedRepoForFusion.collectAsState()
    val selectedAsset by viewModel.selectedAssetForFusion.collectAsState()
    val customTitle by viewModel.fusionCustomTitle.collectAsState()
    val isFusing by viewModel.isFusing.collectAsState()

    var showMixerExpanded by remember { mutableStateOf(true) }

    val filteredCapsules = remember(capsules, selectedTotem) {
        if (selectedTotem == null) capsules else capsules.filter { it.totem == selectedTotem }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("fusion_hub_list"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Hero Branding Header
        item {
            HeroBrandingHeader()
        }

        // Quick Fuse Matrix Mixer Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("fusion_matrix_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MuseDarkSurfaceElevated),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(
                        listOf(MuseRed, MuseGreen, MuseCyan)
                    )
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(MuseRed, MuseCyan))),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "MUSE FUSION ENGINE",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 14.sp,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Fuse Web3 Git Repos × 4Real.io Sensory Stems",
                                    color = MuseCyan,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        IconButton(onClick = { showMixerExpanded = !showMixerExpanded }) {
                            Icon(
                                if (showMixerExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Toggle Mixer",
                                tint = TextSecondary
                            )
                        }
                    }

                    AnimatedVisibility(visible = showMixerExpanded) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            // Selector row: Left (Web3 Repo) / Right (4Real Asset)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Left: Web3 Repo Selector
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MuseDarkBg)
                                        .border(1.dp, MuseCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "🌐 1. WEB3 REPO",
                                            color = MuseCyan,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = selectedRepo?.name ?: "Select Repo",
                                            color = TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "${selectedRepo?.chain ?: "EVM"} • ${selectedRepo?.contractType ?: "Smart Contract"}",
                                            color = TextSecondary,
                                            fontSize = 10.sp,
                                            maxLines = 1
                                        )
                                    }
                                }

                                // Right: 4Real Sensory Asset Selector
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MuseDarkBg)
                                        .border(1.dp, MuseGreen.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "🧬 2. 4REAL STEM",
                                            color = MuseGreen,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = selectedAsset?.title ?: "Select Asset",
                                            color = TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "${selectedAsset?.category ?: "Sensory"} • ${selectedAsset?.frequency ?: 440f}Hz",
                                            color = TextSecondary,
                                            fontSize = 10.sp,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Custom Capsule Name Field
                            OutlinedTextField(
                                value = customTitle,
                                onValueChange = { viewModel.fusionCustomTitle.value = it },
                                label = { Text("Capsule Title (Optional)", fontSize = 12.sp) },
                                placeholder = { Text("e.g., Dragon Account Abstraction Capsule", fontSize = 12.sp) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("capsule_title_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MuseCyan,
                                    unfocusedBorderColor = MuseBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = MuseDarkBg,
                                    unfocusedContainerColor = MuseDarkBg
                                ),
                                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Big Ignite Fusion Button
                            Button(
                                onClick = { viewModel.fuseCurrentSelection() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("ignite_fusion_button"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MuseRed),
                                enabled = !isFusing && selectedRepo != null && selectedAsset != null
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isFusing) "SYNTHESIZING FUSED CAPSULE..." else "🔥 IGNITE FUSION CAPSULE",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Totem Matrix Filter Bar
        item {
            TotemSelectorBar(
                selectedTotem = selectedTotem,
                onSelectTotem = { viewModel.setTotemFilter(it) }
            )
        }

        // Section Title
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "FUSED DAPP CAPSULES (${filteredCapsules.size})",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Live On-Chain & Sensory",
                    color = MuseCyan,
                    fontSize = 11.sp
                )
            }
        }

        // Fused Capsules List
        if (filteredCapsules.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.HourglassEmpty,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No Fused Capsules Found",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Select a Web3 Repo and 4Real Stem above to ignite a fusion.",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            items(filteredCapsules, key = { it.id }) { capsule ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    CapsuleCard(
                        capsule = capsule,
                        onToggleStar = { viewModel.toggleStar(capsule) },
                        onExecuteCapsule = { viewModel.executeCapsule(capsule) },
                        onOpenDetails = { viewModel.inspectingCapsule.value = capsule }
                    )
                }
            }
        }
    }
}

@Composable
fun HeroBrandingHeader(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(MuseDarkSurfaceElevated, MuseDarkBg)
                )
            )
            .border(
                1.dp,
                Brush.horizontalGradient(listOf(MuseRed, MuseGreen, MuseCyan)),
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Interactive DV1 Glowing Brand Badge
                DV1BrandBadge(
                    size = 72.dp,
                    showLabel = false
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "dextervann1-rgb",
                            color = TextPrimary,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MuseRed)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                "MUSE",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Web3 GitHub × 4Real.io Library Fusion",
                        color = MuseCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "\"Observe. Disturb. Evolve. No ceilings.\"",
                        color = MuseGreen,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Totem Archetype Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Totem.entries.forEach { totem ->
                    val tColor = Color(android.graphics.Color.parseColor(totem.colorHex))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(totem.emoji, fontSize = 14.sp)
                        Text(
                            text = totem.title,
                            color = tColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
