package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.ui.viewmodel.ExecutionResult

@Composable
fun ExecutionReceiptDialog(
    result: ExecutionResult,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MuseGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "ON-CHAIN CONFIRMATION",
                    color = TextPrimary,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 15.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MuseDarkBg)
                    .border(1.dp, MuseBorder, RoundedCornerShape(10.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = result.capsuleTitle,
                    color = MuseCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Divider(color = MuseBorder)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Block Height", color = TextSecondary, fontSize = 11.sp)
                    Text("#${result.blockNumber}", color = TextPrimary, fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Gas Fee", color = TextSecondary, fontSize = 11.sp)
                    Text("${result.gasUsedGwei} Gwei (~0.00042 ETH)", color = MuseGreen, fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Column(modifier = Modifier.padding(top = 4.dp)) {
                    Text("Transaction Hash", color = TextSecondary, fontSize = 10.sp)
                    Text(
                        text = result.txHash,
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Tx Hash", result.txHash)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Tx Hash copied to clipboard!", Toast.LENGTH_SHORT).show()
                    SensoryAudioEngine.playTone(880f, 60, 0.4f)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MuseGreen),
                modifier = Modifier.testTag("dismiss_receipt_btn")
            ) {
                Text("Copy Hash & Close", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = MuseDarkSurfaceElevated
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CapsuleDetailsModal(
    capsule: FusedCapsule,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onExecute: () -> Unit
) {
    val totem = Totem.entries.find { it.name == capsule.totem } ?: Totem.DRAGON
    val totemColor = Color(android.graphics.Color.parseColor(totem.colorHex))
    val scrollState = rememberScrollState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MuseDarkSurfaceElevated
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(scrollState)
                .testTag("capsule_details_modal")
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(totem.emoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = capsule.title,
                            color = TextPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Fusion Hash: ${capsule.fusionHash}",
                            color = totemColor,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Capsule", tint = MuseRed)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Real-Time Waveform
            WaveformOscillatorCanvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                baseColor = totemColor,
                speedMultiplier = 1.4f,
                interactive = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Metadata Grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MuseDarkBg)
                    .border(1.dp, MuseBorder, RoundedCornerShape(10.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Target Chain", color = TextSecondary, fontSize = 11.sp)
                    Text(capsule.chain, color = MuseCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Shader Config", color = TextSecondary, fontSize = 11.sp)
                    Text(capsule.shaderConfig, color = TextPrimary, fontSize = 11.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Gas Estimate", color = TextSecondary, fontSize = 11.sp)
                    Text("~${capsule.gasEstimateGwei} Gwei", color = MuseGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    SensoryAudioEngine.playFusionIgnition()
                    onDismiss()
                    onExecute()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("modal_execute_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = totemColor),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Execute On-Chain Contract", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
