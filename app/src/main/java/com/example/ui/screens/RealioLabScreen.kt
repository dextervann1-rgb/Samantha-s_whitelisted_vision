package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SensoryAudioEngine
import com.example.data.model.RealioAsset
import com.example.data.model.Totem
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MuseFusionViewModel

@Composable
fun RealioLabScreen(
    viewModel: MuseFusionViewModel,
    modifier: Modifier = Modifier
) {
    val assets by viewModel.assets.collectAsState()
    val selectedTotem by viewModel.selectedTotemFilter.collectAsState()
    val selectedAssetForFusion by viewModel.selectedAssetForFusion.collectAsState()

    var showAddAssetDialog by remember { mutableStateOf(false) }
    var activeStudioAsset by remember { mutableStateOf<RealioAsset?>(null) }
    var studioFrequency by remember { mutableFloatStateOf(440f) }
    var studioSpeed by remember { mutableFloatStateOf(1.5f) }

    LaunchedEffect(assets) {
        if (assets.isNotEmpty() && activeStudioAsset == null) {
            activeStudioAsset = assets.first()
            studioFrequency = assets.first().frequency
            studioSpeed = assets.first().speed
        }
    }

    val filteredAssets = remember(assets, selectedTotem) {
        if (selectedTotem == null) assets else assets.filter { it.totem == selectedTotem }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    SensoryAudioEngine.playTone(700f, 60, 0.4f)
                    showAddAssetDialog = true
                },
                icon = { Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black) },
                text = { Text("Add 4Real Stem", fontWeight = FontWeight.Bold, color = Color.Black) },
                containerColor = MuseGreen,
                modifier = Modifier
                    .padding(bottom = 80.dp)
                    .testTag("add_asset_fab")
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("realio_lab_list"),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Screen Title & Introduction
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "4REAL.IO REAL-TIME SENSORY LAB",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Interactive audio-haptic oscillators, holographic shaders, and neural particle meshes.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )
                }
            }

            // Interactive Live Canvas Studio Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .testTag("interactive_studio_canvas_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MuseDarkSurfaceElevated),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(MuseGreen, MuseCyan))
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "LIVE STEM SYNTHESIZER",
                                    color = MuseGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = activeStudioAsset?.title ?: "Select Visual Stem",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = {
                                    SensoryAudioEngine.playTone(studioFrequency, 250, 0.7f)
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MuseGreen)
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Play Stem", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Render Selected Visualizer Canvas
                        if (activeStudioAsset?.category == "Particle Mesh") {
                            ParticleMeshCanvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp),
                                particleCount = 28,
                                accentColor = MuseGreen
                            )
                        } else if (activeStudioAsset?.category == "Brand Badge") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MuseDarkBg)
                                    .border(1.dp, MuseBorder, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                DV1BrandBadge(
                                    size = 90.dp,
                                    showLabel = true
                                )
                            }
                        } else {
                            WaveformOscillatorCanvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp),
                                baseFrequency = studioFrequency,
                                speedMultiplier = studioSpeed,
                                interactive = true
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Controls: Frequency Slider + Speed
                        Text(
                            text = "Stem Frequency: ${studioFrequency.toInt()} Hz",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Slider(
                            value = studioFrequency,
                            onValueChange = {
                                studioFrequency = it
                                SensoryAudioEngine.playTone(it, 50, 0.2f)
                            },
                            valueRange = 80f..1200f,
                            colors = SliderDefaults.colors(
                                thumbColor = MuseGreen,
                                activeTrackColor = MuseGreen,
                                inactiveTrackColor = MuseDarkBg
                            ),
                            modifier = Modifier.testTag("frequency_slider")
                        )

                        Text(
                            text = "Speed Multiplier: ${"%.2f".format(studioSpeed)}x",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Slider(
                            value = studioSpeed,
                            onValueChange = { studioSpeed = it },
                            valueRange = 0.5f..4.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = MuseCyan,
                                activeTrackColor = MuseCyan,
                                inactiveTrackColor = MuseDarkBg
                            )
                        )
                    }
                }
            }

            // Totem Filter
            item {
                TotemSelectorBar(
                    selectedTotem = selectedTotem,
                    onSelectTotem = { viewModel.setTotemFilter(it) }
                )
            }

            // Asset Cards
            items(filteredAssets, key = { it.id }) { asset ->
                val isSelectedForFusion = selectedAssetForFusion?.id == asset.id
                val isActiveStudio = activeStudioAsset?.id == asset.id

                RealioAssetCard(
                    asset = asset,
                    isSelectedForFusion = isSelectedForFusion,
                    isActiveStudio = isActiveStudio,
                    onActivateStudio = {
                        activeStudioAsset = asset
                        studioFrequency = asset.frequency
                        studioSpeed = asset.speed
                        SensoryAudioEngine.playTone(asset.frequency, 120, 0.4f)
                    },
                    onSelectForFusion = { viewModel.selectAssetForFusion(asset) }
                )
            }

            item {
                LegalFooter(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    if (showAddAssetDialog) {
        AddAssetDialog(
            onDismiss = { showAddAssetDialog = false },
            onAdd = { title, cat, totem, freq, spd, color, desc, code ->
                viewModel.addCustomAsset(title, cat, totem, freq, spd, color, desc, code)
                showAddAssetDialog = false
            }
        )
    }
}

@Composable
fun RealioAssetCard(
    asset: RealioAsset,
    isSelectedForFusion: Boolean,
    isActiveStudio: Boolean,
    onActivateStudio: () -> Unit,
    onSelectForFusion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totem = Totem.entries.find { it.name == asset.totem } ?: Totem.DRAGON
    val totemColor = Color(android.graphics.Color.parseColor(totem.colorHex))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("realio_asset_card_${asset.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MuseDarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                if (isActiveStudio) listOf(MuseGreen, MuseCyan) else listOf(MuseBorder, MuseBorder)
            )
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(totem.emoji, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = asset.title,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${asset.category} • ${asset.frequency.toInt()}Hz",
                            color = totemColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                if (isActiveStudio) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MuseGreen.copy(alpha = 0.2f))
                            .border(1.dp, MuseGreen, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "LIVE IN STUDIO",
                            color = MuseGreen,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = asset.description,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onActivateStudio,
                    modifier = Modifier.weight(1f).height(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(MuseBorder, MuseGreen)))
                ) {
                    Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Load in Lab", fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        SensoryAudioEngine.playTone(720f, 60, 0.4f)
                        onSelectForFusion()
                    },
                    modifier = Modifier.weight(1f).height(36.dp).testTag("select_fusion_asset_${asset.id}"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelectedForFusion) MuseCyan else MuseDarkSurfaceElevated
                    ),
                    border = if (!isSelectedForFusion) ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(MuseGreen, MuseCyan))) else null
                ) {
                    Icon(
                        if (isSelectedForFusion) Icons.Default.Check else Icons.Default.Bolt,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (isSelectedForFusion) Color.Black else MuseGreen
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isSelectedForFusion) "Selected" else "Select to Fuse",
                        color = if (isSelectedForFusion) Color.Black else TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AddAssetDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, category: String, totem: String, freq: Float, speed: Float, color: String, desc: String, code: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Waveform Oscillator") }
    var description by remember { mutableStateOf("") }
    var frequency by remember { mutableFloatStateOf(520f) }
    var speed by remember { mutableFloatStateOf(1.2f) }
    var selectedTotem by remember { mutableStateOf(Totem.KRAKEN.name) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add 4Real Sensory Stem", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Stem Title", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("stem_title_input")
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category (Waveform, Particle, Holographic)", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Base Frequency: ${frequency.toInt()} Hz", fontSize = 11.sp, color = TextSecondary)
                Slider(
                    value = frequency,
                    onValueChange = { frequency = it },
                    valueRange = 100f..1000f,
                    colors = SliderDefaults.colors(thumbColor = MuseGreen, activeTrackColor = MuseGreen)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onAdd(title, category, selectedTotem, frequency, speed, "#00E5FF", description, "Sensory Canvas Shader Stem")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MuseGreen),
                modifier = Modifier.testTag("confirm_add_asset_btn")
            ) {
                Text("Add Stem", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = MuseDarkSurface
    )
}
