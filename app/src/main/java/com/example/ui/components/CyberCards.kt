package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SensoryAudioEngine
import com.example.data.model.FusedCapsule
import com.example.data.model.Totem
import com.example.ui.theme.*

@Composable
fun TotemSelectorBar(
    selectedTotem: String?,
    onSelectTotem: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // "All" filter chip
        FilterChip(
            selected = selectedTotem == null,
            onClick = {
                SensoryAudioEngine.playTone(440f, 60, 0.3f)
                onSelectTotem(null)
            },
            label = { Text("⚡ All Matrix", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MuseCyan.copy(alpha = 0.2f),
                selectedLabelColor = MuseCyan,
                containerColor = MuseDarkSurface,
                labelColor = TextSecondary
            ),
            border = FilterChipDefaults.filterChipBorder(
                borderColor = if (selectedTotem == null) MuseCyan else MuseBorder,
                selectedBorderColor = MuseCyan,
                enabled = true,
                selected = selectedTotem == null
            ),
            modifier = Modifier.testTag("totem_chip_all")
        )

        Totem.entries.forEach { totem ->
            val isSelected = selectedTotem == totem.name
            val totemColor = Color(android.graphics.Color.parseColor(totem.colorHex))
            FilterChip(
                selected = isSelected,
                onClick = {
                    SensoryAudioEngine.playTone(550f, 70, 0.3f)
                    onSelectTotem(if (isSelected) null else totem.name)
                },
                label = {
                    Text(
                        "${totem.emoji} ${totem.title}",
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = totemColor.copy(alpha = 0.2f),
                    selectedLabelColor = totemColor,
                    containerColor = MuseDarkSurface,
                    labelColor = TextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = if (isSelected) totemColor else MuseBorder,
                    selectedBorderColor = totemColor,
                    enabled = true,
                    selected = isSelected
                ),
                modifier = Modifier.testTag("totem_chip_${totem.name.lowercase()}")
            )
        }
    }
}

@Composable
fun Web3WalletBar(
    currentNetwork: String,
    onSwitchNetwork: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }
    val simulatedAddress = "0xDV1...9F4A"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MuseDarkSurfaceElevated)
            .border(1.dp, MuseBorder, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Wallet Address", "0x71C...DV1_MUSE_9F4A")
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Wallet Address Copied", Toast.LENGTH_SHORT).show()
                SensoryAudioEngine.playTone(700f, 80, 0.3f)
            }
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MuseGreen)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Web3 Signer: ",
                color = TextSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = simulatedAddress,
                color = MuseCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                Icons.Default.ContentCopy,
                contentDescription = "Copy Wallet",
                tint = TextMuted,
                modifier = Modifier.size(12.dp)
            )
        }

        Box {
            OutlinedButton(
                onClick = { isExpanded = true },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                modifier = Modifier.height(28.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MuseCyan),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(MuseCyan, MuseGreen)))
            ) {
                Text(currentNetwork, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
            }

            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
                modifier = Modifier.background(MuseDarkSurface)
            ) {
                listOf("Ethereum Mainnet", "Arbitrum One", "Base L2", "Solana Devnet", "Polygon PoS").forEach { chain ->
                    DropdownMenuItem(
                        text = { Text(chain, color = TextPrimary, fontSize = 13.sp) },
                        onClick = {
                            onSwitchNetwork(chain)
                            isExpanded = false
                            SensoryAudioEngine.playTone(600f, 60, 0.3f)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CodeSnippetViewer(
    code: String,
    title: String? = null,
    language: String = "solidity",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF04060A))
            .border(1.dp, MuseBorder, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(MuseRed))
                Spacer(modifier = Modifier.width(4.dp))
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(MuseGreen))
                Spacer(modifier = Modifier.width(4.dp))
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(MuseCyan))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title ?: language.uppercase(),
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Code Snippet", code)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Code copied to clipboard!", Toast.LENGTH_SHORT).show()
                    SensoryAudioEngine.playTone(800f, 70, 0.4f)
                },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.ContentCopy,
                    contentDescription = "Copy code",
                    tint = MuseCyan,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = code,
            color = Color(0xFFB8D0EB),
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            lineHeight = 16.sp
        )
    }
}

@Composable
fun CapsuleCard(
    capsule: FusedCapsule,
    onToggleStar: () -> Unit,
    onExecuteCapsule: () -> Unit,
    onOpenDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totem = Totem.entries.find { it.name == capsule.totem } ?: Totem.DRAGON
    val totemColor = Color(android.graphics.Color.parseColor(totem.colorHex))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("capsule_card_${capsule.id}")
            .clickable { onOpenDetails() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MuseDarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(totemColor.copy(alpha = 0.6f), MuseBorder)
            )
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Totem + Title + Star
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(totem.emoji, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = capsule.title,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Chain: ${capsule.chain} • Gas ~${capsule.gasEstimateGwei} Gwei",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(
                    onClick = {
                        SensoryAudioEngine.playTone(720f, 60, 0.3f)
                        onToggleStar()
                    },
                    modifier = Modifier.size(32.dp).testTag("star_button_${capsule.id}")
                ) {
                    Icon(
                        if (capsule.isStarred) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Star Capsule",
                        tint = if (capsule.isStarred) GoldAccent else TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Fusion Components Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MuseDarkBg)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "WEB3 REPO",
                        color = MuseCyan,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = capsule.repoName,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "⚡ FUSED ⚡",
                    color = totemColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )

                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text(
                        text = "4REAL SENSORY",
                        color = MuseGreen,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = capsule.assetName,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mini Waveform Live Preview
            WaveformOscillatorCanvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                baseColor = totemColor,
                speedMultiplier = 1.2f,
                interactive = false
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenDetails,
                    modifier = Modifier.weight(1f).height(38.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(MuseBorder, MuseCyan)))
                ) {
                    Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Inspect", fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        SensoryAudioEngine.playFusionIgnition()
                        onExecuteCapsule()
                    },
                    modifier = Modifier.weight(1f).height(38.dp).testTag("execute_button_${capsule.id}"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = totemColor)
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Execute", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}
