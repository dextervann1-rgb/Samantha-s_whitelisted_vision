package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.model.zk.AttributeType
import com.example.data.model.zk.ZkCircuitAttribute
import com.example.ui.components.BaseLogo
import com.example.ui.components.BasescanLinkChip
import com.example.ui.components.GitHubLinkChip
import com.example.ui.components.LegalFooter
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

    // Privacy attributes & On-Chain calldata states
    val privacyAttributes by viewModel.privacyAttributes.collectAsState()
    val selectedAttrType by viewModel.selectedAttributeType.collectAsState()
    val onChainCalldata by viewModel.onChainCalldataPayload.collectAsState()
    val isOnChainVerifying by viewModel.isOnChainVerifying.collectAsState()

    // MCP & V€€³ OG8OH OS Merge states
    val mcpTools = viewModel.mcpTools
    val mcpLogs by viewModel.mcpExecutionLogs.collectAsState()
    val isExecutingMcp by viewModel.isExecutingMcp.collectAsState()
    val osMergeHistory by viewModel.osMergeHistory.collectAsState()
    val isMergingOs by viewModel.isMergingOs.collectAsState()

    var customWitnessInput by remember { mutableStateOf("VFV_INTENT_SCOPE_ALLOWANCE_100_ETH") }
    var customAttrSecret by remember { mutableStateOf("12.5 ETH Secret Balance") }
    var customAttrPredicate by remember { mutableStateOf("Balance >= 5.0 ETH") }
    var mergeSourceRepoInput by remember { mutableStateOf("dextervann1-rgb/scalenstack") }
    var expandedPolicyIndex by remember { mutableIntStateOf(0) }
    var activeSubTab by remember { mutableIntStateOf(0) } // 0: Stats, 1: Privacy Attributes & ZK, 2: MCP & OS Merge, 3: Compliance

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

        // Sub-Tab Switcher (4 Tabs)
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val tabs = listOf(
                    0 to "📊 Valuation",
                    1 to "🔐 Privacy & ZK",
                    2 to "🤖 MCP & V€€³ Merge",
                    3 to "📜 Play Compliance"
                )
                items(tabs) { (index, title) ->
                    val isSelected = activeSubTab == index
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) MuseCyan.copy(alpha = 0.2f) else MuseDarkSurface)
                            .border(1.dp, if (isSelected) MuseCyan else MuseBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                activeSubTab = index
                                SensoryAudioEngine.playTone(550f + index * 90, 40, 0.3f)
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
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
                // Section 0: Valuation & Stats
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
                // Section 1: Privacy Attributes & ZK-Proof Engine
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "PRIVACY-PRESERVING ATTRIBUTE VAULT (ZK-PEDERSEN)",
                            color = MuseCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "User attributes are blinded on-device and proven via ZK-SNARK/STARK. Only public commitments and predicate proofs touch the chain.",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                        )

                        // Privacy Attributes List
                        privacyAttributes.forEach { attr ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MuseDarkSurface),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = Brush.horizontalGradient(listOf(MuseCyan.copy(alpha = 0.5f), MusePurple.copy(alpha = 0.5f)))
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(attr.attributeType.displayName, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(MuseGreen.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("ZK VERIFIED", color = MuseGreen, fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Claim: ${attr.predicate}", color = MuseCyan, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    Text("Public Commitment: ${attr.publicCommitment.take(24)}...", color = TextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                    Text("Private Value: 🔒 [Encrypted / Secret]", color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Generate New Attribute Proof Section
                        Text(
                            text = "CREATE BLINDED ATTRIBUTE CLAIM:",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            items(AttributeType.entries.toTypedArray()) { type ->
                                val isSelected = selectedAttrType == type
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) MusePurple.copy(alpha = 0.3f) else MuseDarkSurface)
                                        .border(1.dp, if (isSelected) MusePurple else MuseBorder, RoundedCornerShape(6.dp))
                                        .clickable { viewModel.selectAttributeType(type) }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = type.displayName,
                                        color = if (isSelected) Color.White else TextSecondary,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = customAttrSecret,
                            onValueChange = { customAttrSecret = it },
                            label = { Text("Private Secret Value (Never Broadcasted)", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = LocalTextStyle.current.copy(fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = customAttrPredicate,
                            onValueChange = { customAttrPredicate = it },
                            label = { Text("Public Predicate Statement", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = LocalTextStyle.current.copy(fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                viewModel.generatePrivacyAttributeProof(selectedAttrType, customAttrSecret, customAttrPredicate)
                            },
                            modifier = Modifier.fillMaxWidth().height(42.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MuseCyan),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !isGeneratingZk
                        ) {
                            if (isGeneratingZk) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Synthesizing ZK Attribute Proof...", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Blind Secret & Generate Proof", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // On-Chain Calldata Synthesis
                        latestProof?.let { proof ->
                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = MuseBorder)
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "ON-CHAIN EVM CALLDATA (SOLIDITY VERIFIER)",
                                color = GoldAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Button(
                                onClick = { viewModel.generateOnChainCalldata(proof) },
                                modifier = Modifier.fillMaxWidth().height(38.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MusePurple),
                                shape = RoundedCornerShape(8.dp),
                                enabled = !isOnChainVerifying
                            ) {
                                Text("Synthesize verifyProof(...) Calldata", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            onChainCalldata?.let { payload ->
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MuseDarkBg)
                                        .border(1.dp, MuseGreen, RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("EVM VERIFIER READY ✅", color = MuseGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                            Text("Gas: ${payload.estimatedGas}", color = MuseCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Contract: ${payload.verifierContractAddress}", color = TextSecondary, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                        Text("ABI: ${payload.functionSignature}", color = GoldAccent, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                        Text("Calldata: ${payload.fullCalldataHex.take(48)}...", color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        BasescanLinkChip(
                                            url = "https://basescan.org/address/${payload.verifierContractAddress}",
                                            label = "Verify on basescan.org"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Section 2: Model Context Protocol (MCP) & V€€³ OG8OH OS Merge
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "MODEL CONTEXT PROTOCOL (MCP) & V€€³ OG8OH OS",
                            color = MusePurple,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Standardized agentic MCP tools for Web3 commit tree synchronization, ZK attestation, and V€€³ OG8OH OS kernel merging.",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                        )

                        // MCP Tool Runner Chips
                        Text(
                            text = "AVAILABLE MCP AGENT TOOLS:",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        mcpTools.forEach { tool ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MuseDarkSurface),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = Brush.horizontalGradient(listOf(MuseBorder, MusePurple.copy(alpha = 0.4f)))
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(tool.name, color = MuseCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                        Text(tool.description, color = TextSecondary, fontSize = 10.sp)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            viewModel.executeMcpTool(tool.name, mapOf("repo" to mergeSourceRepoInput, "predicate" to "Scope Verified"))
                                        },
                                        modifier = Modifier.height(32.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MusePurple)
                                    ) {
                                        Text("Run MCP", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // V€€³ OG8OH OS Merge Console
                        Text(
                            text = "V€€³ OG8OH OS KERNEL REPOSITORY MERGE:",
                            color = GoldAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = mergeSourceRepoInput,
                            onValueChange = { mergeSourceRepoInput = it },
                            label = { Text("GitHub Source Repository", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = LocalTextStyle.current.copy(fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Button(
                            onClick = { viewModel.mergeV3Og8ohOs(mergeSourceRepoInput) },
                            modifier = Modifier.fillMaxWidth().height(40.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !isMergingOs
                        ) {
                            if (isMergingOs) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Merging Kernel Tree with ZK Attestation...", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Default.MergeType, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Execute V€€³ OG8OH OS Merge", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Merge History Stream
                        if (osMergeHistory.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "V€€³ OS MERGE ATTESTATIONS (${osMergeHistory.size})",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            osMergeHistory.take(3).forEach { record ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MuseDarkBg)
                                        .border(1.dp, MuseGreen, RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(record.kernelBuildVersion, color = MuseGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                            Text("Score: ${record.securityAuditScore}/10", color = GoldAccent, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                        }
                                        Text("Source: ${record.sourceRepository} -> ${record.targetBranch}", color = TextPrimary, fontSize = 10.sp)
                                        Text("ZK Attestation: ${record.zkAttestationHash.take(28)}...", color = TextSecondary, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }
                        }

                        // MCP Execution Logs
                        if (mcpLogs.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "MCP TOOL EXECUTION TELEMETRY (${mcpLogs.size})",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            mcpLogs.take(4).forEach { log ->
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
                                        Text(log.toolName, color = MuseCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                        Text("${log.latencyMs}ms", color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                    }
                                    Text(log.outputSummary, color = TextSecondary, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }

            3 -> {
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

        item {
            LegalFooter(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
