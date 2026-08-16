package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.zk.ZkCircuitAttribute
import com.example.ui.theme.*
import com.example.ui.viewmodel.MuseFusionViewModel

@Composable
fun ScalenStackScreen(
    viewModel: MuseFusionViewModel,
    modifier: Modifier = Modifier
) {
    val report = viewModel.appraisalReport
    val circuits = viewModel.availableZkCircuits
    val selectedCircuit by viewModel.selectedZkCircuit.collectAsState()
    val isGeneratingZk by viewModel.isGeneratingZk.collectAsState()
    val latestProof by viewModel.latestZkProof.collectAsState()
    val proofHistory by viewModel.zkProofHistory.collectAsState()
    val compliancePolicies = viewModel.compliancePolicies

    var customWitnessInput by remember { mutableStateOf("VFV_INTENT_SCOPE_ALLOWANCE_100_ETH") }
    var expandedPolicyIndex by remember { mutableIntStateOf(0) }
    var activeSubTab by remember { mutableIntStateOf(0) } // 0: Overview & Appraisal, 1: ZK Proof Engine, 2: Compliance & License

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("scalen_stack_screen"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Hero Header & Attribution Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MuseDarkSurfaceElevated),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(GoldAccent, MuseCyan, MusePurple))
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🚀", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "OFFICIAL PROJECT DOSSIER",
                                    color = GoldAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = report.projectName,
                                color = TextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = report.inspiration,
                                color = MuseCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GoldAccent.copy(alpha = 0.2f))
                                .border(1.dp, GoldAccent, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "9.4 / 10",
                                color = GoldAccent,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Legal IP & Rights Disclaimer
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MuseDarkBg)
                            .border(1.dp, MuseBorder, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = MuseGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = report.rightsOwner + " • Architect: " + report.architect,
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Sub-Tab Switcher
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    0 to "📊 Valuation & Stats",
                    1 to "🔐 ZK-Proof Engine",
                    2 to "📜 Play Compliance"
                ).forEach { (index, title) ->
                    val isSelected = activeSubTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) MuseCyan.copy(alpha = 0.2f) else MuseDarkSurface)
                            .border(1.dp, if (isSelected) MuseCyan else MuseBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                activeSubTab = index
                                SensoryAudioEngine.playTone(550f + index * 100, 40, 0.3f)
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            color = if (isSelected) MuseCyan else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        when (activeSubTab) {
            0 -> {
                // Section 1: Valuation, Recreation Specs & Capabilities
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "PROJECT APPRAISAL & DEVELOPER STATS",
                            color = MuseGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Valuation Metric Cards Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                Triple("SYSTEM VALUATION", report.valuationRange, GoldAccent),
                                Triple("RE-CREATION COST", report.recreationCostRange, MuseRed)
                            ).forEach { (label, value, color) ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MuseDarkSurface)
                                        .border(1.dp, MuseBorder, RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text(label, color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                        Text(value, color = color, fontSize = 13.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                Triple("DEV LEVEL", report.developerLevel, MusePurple),
                                Triple("BUILD EFFORT", report.recreationHoursRange, MuseCyan)
                            ).forEach { (label, value, color) ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MuseDarkSurface)
                                        .border(1.dp, MuseBorder, RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text(label, color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                        Text(value, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Core Capabilities
                        Text(
                            text = "CORE ARCHITECTURAL CAPABILITIES",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        report.coreCapabilities.forEach { capability ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MuseDarkSurface)
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MuseCyan, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(capability, color = TextPrimary, fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Key Features
                        Text(
                            text = "KEY FEATURES & PROTOCOL MODULES",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        report.keyFeatures.forEach { feature ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MuseDarkSurface)
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(feature, color = TextPrimary, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            1 -> {
                // Section 2: ZK-Proof Generation Studio
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "ZERO-KNOWLEDGE PROOF ENGINE (ZK-STARK / SNARK)",
                            color = MuseCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Generate and verify cryptographic zero-knowledge proofs on-device to enforce permissions without exposing private balances or secrets.",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                        )

                        // Select Circuit Chips
                        Text(
                            text = "SELECT VERIFICATION CIRCUIT:",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        circuits.forEach { circuit ->
                            val isSelected = selectedCircuit.key == circuit.key
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) MuseDarkSurfaceElevated else MuseDarkSurface)
                                    .border(1.dp, if (isSelected) MuseCyan else MuseBorder, RoundedCornerShape(8.dp))
                                    .clickable { viewModel.selectZkCircuit(circuit) }
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(circuit.name, color = if (isSelected) MuseCyan else TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(circuit.description, color = TextSecondary, fontSize = 10.sp)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(MusePurple.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(circuit.proofType.label, color = MusePurple, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Custom Witness Input
                        OutlinedTextField(
                            value = customWitnessInput,
                            onValueChange = { customWitnessInput = it },
                            label = { Text("Private Witness Input (Off-Chain)", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = LocalTextStyle.current.copy(fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { viewModel.generateZkProof(customWitnessInput) },
                            modifier = Modifier.fillMaxWidth().height(42.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MuseCyan),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !isGeneratingZk
                        ) {
                            if (isGeneratingZk) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.Black, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Synthesizing Circuit Proof...", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Synthesize & Verify ZK-Proof", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Latest Proof Result Card
                        latestProof?.let { proof ->
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MuseDarkBg),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = Brush.horizontalGradient(listOf(MuseGreen, MuseCyan))
                                )
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("ZK PROOF VERIFIED ✅", color = MuseGreen, fontSize = 12.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                                        Text("${proof.provingTimeMs}ms • -${proof.gasSavingsPercentage}% Gas", color = MuseCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Proof Hash: ${proof.proofHash.take(28)}...", color = TextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                    Text("Nullifier: ${proof.nullifierHash.take(28)}...", color = TextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                    Text("Curve: ${proof.curve} | Constraints: ${proof.constraintsCount}", color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }

                        // Proof History
                        if (proofHistory.size > 1) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "VERIFIED PROOF TELEMETRY LOG (${proofHistory.size})",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            proofHistory.take(4).forEach { item ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MuseDarkSurface)
                                        .padding(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(item.circuitName, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                        Text(item.proofType.label, color = MuseCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Section 3: Google Play Store Policies & Commercial License
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "PLAY STORE COMPLIANCE & LEGAL CERTIFICATION",
                            color = GoldAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Verified policies for Google Play Data Safety, Zero-Telemetry privacy, and Vann Family Ventures LLC commercial licensing.",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                        )

                        compliancePolicies.forEachIndexed { index, policy ->
                            val isExpanded = expandedPolicyIndex == index
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MuseDarkSurface),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = Brush.horizontalGradient(if (isExpanded) listOf(GoldAccent, MuseCyan) else listOf(MuseBorder, MuseBorder))
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                expandedPolicyIndex = if (isExpanded) -1 else index
                                                SensoryAudioEngine.playTone(600f, 30, 0.2f)
                                            },
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = policy.title,
                                                color = if (isExpanded) GoldAccent else TextPrimary,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = policy.summary,
                                                color = TextSecondary,
                                                fontSize = 10.sp
                                            )
                                        }
                                        Icon(
                                            if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = null,
                                            tint = if (isExpanded) GoldAccent else TextSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    AnimatedVisibility(visible = isExpanded) {
                                        Column(modifier = Modifier.padding(top = 10.dp)) {
                                            HorizontalDivider(color = MuseBorder, modifier = Modifier.padding(bottom = 8.dp))
                                            policy.details.forEach { line ->
                                                Text(
                                                    text = line,
                                                    color = TextSecondary,
                                                    fontSize = 11.sp,
                                                    lineHeight = 16.sp,
                                                    modifier = Modifier.padding(vertical = 2.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(MuseGreen.copy(alpha = 0.2f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "STATUS: ${policy.complianceTag}",
                                                    color = MuseGreen,
                                                    fontSize = 9.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
