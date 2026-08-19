package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SensoryAudioEngine
import com.example.ui.components.CodeSnippetViewer
import com.example.ui.components.LegalFooter
import com.example.ui.theme.*
import com.example.ui.viewmodel.*
import kotlin.math.cos
import kotlin.math.sin

data class DimensionSpec(
    val id: String,
    val label: String,
    val emoji: String
)

val radarDimensions = listOf(
    DimensionSpec("innovation", "Innovation", "✨"),
    DimensionSpec("security", "Security", "🛡️"),
    DimensionSpec("ux", "UX", "👁️"),
    DimensionSpec("interop", "Interop", "🌐"),
    DimensionSpec("monetization", "Monetization", "📈"),
    DimensionSpec("feasibility", "Feasibility", "⚙️"),
    DimensionSpec("timing", "Timing", "⏱️")
)

@Composable
fun Dime10MarketplaceScreen(
    viewModel: MuseFusionViewModel,
    modifier: Modifier = Modifier
) {
    var activeSubTab by remember { mutableStateOf("compare") } // "compare", "marketplace", "builder"
    val listings by viewModel.auctionListings.collectAsState()
    val myVault by viewModel.myVaultNfts.collectAsState()
    val currentMerged by viewModel.currentMergedDime10.collectAsState()

    // Inputs for Project A & Project B
    var nameA by remember { mutableStateOf("Klien Key Concept") }
    var descA by remember { mutableStateOf("Delegated Permission Model for autonomous AI execution without root seed phrases. Ephemeral keys bounded by app-level rules.") }
    var featsA by remember { mutableStateOf("Scoped authority pre-approval, Ephemeral keys, Spending caps, Whitelists, Local signing") }

    var nameB by remember { mutableStateOf("ERC-7715 MetaMask Permissions") }
    var descB by remember { mutableStateOf("Standardized off-chain permission grants signed by EOAs or Smart Accounts with on-chain caveat enforcers.") }
    var featsB by remember { mutableStateOf("Off-chain grants, ERC-4337 Smart Accounts, Caveat enforcers, Plain-English allowances, Gator SDK") }

    var isComparing by remember { mutableStateOf(false) }
    var hasCompared by remember { mutableStateOf(true) }
    var isSynthesizingDime10 by remember { mutableStateOf(false) }
    var showPermissionModal by remember { mutableStateOf(false) }
    var modalIntentType by remember { mutableStateOf("mint") } // "mint" or "buy"
    var modalTargetItem by remember { mutableStateOf<DimeAuctionItem?>(null) }

    var marketplaceFilter by remember { mutableStateOf("all") } // "all", "oracle", "community"
    var listPriceInput by remember { mutableStateOf("") }
    var selectedNftToList by remember { mutableStateOf<OwnedDimeNft?>(null) }

    // Dynamic Scores Calculation based on content
    val scoresA = remember(nameA, descA, featsA, hasCompared) {
        calculateScores(nameA, descA, featsA, 88)
    }
    val scoresB = remember(nameB, descB, featsB, hasCompared) {
        calculateScores(nameB, descB, featsB, 84)
    }
    val overallA = scoresA.values.average().toInt()
    val overallB = scoresB.values.average().toInt()

    val filteredListings = remember(listings, marketplaceFilter) {
        listings.filter {
            when (marketplaceFilter) {
                "oracle" -> it.ownerType == "oracle"
                "community" -> it.ownerType == "community"
                else -> true
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dime10_marketplace_screen"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Hero Header & Nav Pill
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Brush.linearGradient(listOf(MuseCyan, MusePurple))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("💎", fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "DIME10 & VFV-MARKETPLACE",
                            color = TextPrimary,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 15.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Head On Compare • Unseen Combination • [×z/=$] IP Vault",
                            color = GoldAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Sub-navigation Switcher
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MuseDarkSurface)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        Triple("compare", "⚡ Compare & Combine", MuseCyan),
                        Triple("marketplace", "🏛️ VFV Vault (${listings.size})", GoldAccent),
                        Triple("builder", "🛠️ Architecture Notes", MuseGreen)
                    ).forEach { (tabId, label, accentColor) ->
                        val isSelected = activeSubTab == tabId
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) accentColor.copy(alpha = 0.25f) else Color.Transparent)
                                .border(1.dp, if (isSelected) accentColor else Color.Transparent, RoundedCornerShape(8.dp))
                                .clickable {
                                    activeSubTab = tabId
                                    SensoryAudioEngine.playTone(550f, 40, 0.3f)
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) accentColor else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // TAB 1: COMPARE & COMBINE
        if (activeSubTab == "compare") {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "HEAD ON COMPARISON PROTOCOL",
                        color = MuseCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Rate two decentralized primitives across 7 key dimensions, then synthesize an emergent Dime10 with guaranteed unseen capabilities.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    // Project A & Project B Inputs Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Project A Box
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MuseDarkSurface),
                            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(MuseCyan, MuseCyan.copy(alpha = 0.3f))))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MuseCyan))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("PROJECT A", color = MuseCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = nameA,
                                    onValueChange = { nameA = it },
                                    label = { Text("Name", fontSize = 10.sp) },
                                    singleLine = true,
                                    textStyle = LocalTextStyle.current.copy(fontSize = 11.sp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = descA,
                                    onValueChange = { descA = it },
                                    label = { Text("Description", fontSize = 10.sp) },
                                    maxLines = 2,
                                    textStyle = LocalTextStyle.current.copy(fontSize = 10.sp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // Project B Box
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MuseDarkSurface),
                            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(MuseRed, MuseRed.copy(alpha = 0.3f))))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MuseRed))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("PROJECT B", color = MuseRed, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = nameB,
                                    onValueChange = { nameB = it },
                                    label = { Text("Name", fontSize = 10.sp) },
                                    singleLine = true,
                                    textStyle = LocalTextStyle.current.copy(fontSize = 11.sp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = descB,
                                    onValueChange = { descB = it },
                                    label = { Text("Description", fontSize = 10.sp) },
                                    maxLines = 2,
                                    textStyle = LocalTextStyle.current.copy(fontSize = 10.sp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            isComparing = true
                            SensoryAudioEngine.playTone(660f, 150, 0.5f)
                            hasCompared = true
                            isComparing = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("compare_head_on_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.CompareArrows, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isComparing) "SCANNING 7 DIMENSIONS..." else "HEAD ON COMPARE (RATE & SCORE)",
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // 7-Dimension Radar & Scoreboard Card
            if (hasCompared) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .testTag("radar_scoreboard_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MuseDarkSurfaceElevated),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(listOf(MuseCyan, MuseRed))
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "7-DIMENSION RADAR SCOREBOARD",
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MuseCyan))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("A: $overallA", color = MuseCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MuseRed))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("B: $overallB", color = MuseRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Interactive 7-Dimension Radar Canvas
                            SevenDimensionRadarCanvas(
                                scoresA = scoresA,
                                scoresB = scoresB,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Dimension Progress Bars
                            radarDimensions.forEach { dim ->
                                val scoreA = scoresA[dim.id] ?: 75
                                val scoreB = scoresB[dim.id] ?: 70
                                Column(modifier = Modifier.padding(vertical = 3.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("${dim.emoji} ${dim.label}", color = TextSecondary, fontSize = 11.sp)
                                        Text("$scoreA vs $scoreB", color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        LinearProgressIndicator(
                                            progress = { scoreA / 100f },
                                            modifier = Modifier.weight(1f).height(4.dp).clip(RoundedCornerShape(2.dp)),
                                            color = MuseCyan,
                                            trackColor = MuseDarkBg
                                        )
                                        LinearProgressIndicator(
                                            progress = { scoreB / 100f },
                                            modifier = Modifier.weight(1f).height(4.dp).clip(RoundedCornerShape(2.dp)),
                                            color = MuseRed,
                                            trackColor = MuseDarkBg
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Combine & Find Dime10 Synthesizer Button & Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MuseDarkBg),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(listOf(MuseCyan, GoldAccent, MuseRed))
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "✨ UNSEEN MIX PROTOCOL (DIME10)",
                                color = GoldAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Merge to unlock emergent capabilities neither primitive possesses alone.",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                            )

                            Button(
                                onClick = {
                                    isSynthesizingDime10 = true
                                    SensoryAudioEngine.playFusionIgnition()
                                    val mergedName = "${nameA.split(" ").firstOrNull() ?: "Klien"}-${nameB.filter { it.isDigit() }.ifEmpty { "7715" }} Swarm Matrix"
                                    val merged = MergedDime10(
                                        name = mergedName,
                                        pitch = "The first self-custodied intent engine allowing AI swarms to execute with on-chain caveat enforcers.",
                                        capabilities = listOf(
                                            "Scoped authority pre-approval fused with on-chain caveat enforcement",
                                            "Ephemeral session signing for zero-popup background trades",
                                            "★ UNSEEN: Intent-Negotiation Layer auto-bidding optimal spending caps across L2s"
                                        ),
                                        useCases = listOf(
                                            "Autonomous AI arbitrage trading without seed phrase exposure",
                                            "Cross-session smart account delegation for 4Real sensory games"
                                        ),
                                        emergentTrait = "Zero-Knowledge Permission Proof: prove allowance caps without revealing underlying balances.",
                                        combinedScore = ((overallA + overallB) / 2) + 6,
                                        novelty = 4.8f,
                                        price = 189
                                    )
                                    viewModel.currentMergedDime10.value = merged
                                    isSynthesizingDime10 = false
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("combine_dime10_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isSynthesizingDime10) "SYNTHESIZING DIME10..." else "COMBINE & FIND MY DIME10",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                // Merged Dime10 Result Card
                currentMerged?.let { merged ->
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .testTag("merged_dime10_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MuseDarkSurfaceElevated),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.linearGradient(listOf(MuseCyan, GoldAccent))
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
                                            text = "MERGED • DIME10 DISCOVERED",
                                            color = MuseCyan,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = merged.name,
                                            color = TextPrimary,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Black
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
                                            text = "$${merged.price}",
                                            color = GoldAccent,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "\"${merged.pitch}\"",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "HYBRID CAPABILITIES (UNSEEN MIX)",
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(4.dp))

                                merged.capabilities.forEachIndexed { i, cap ->
                                    val isUnseen = cap.startsWith("★")
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isUnseen) GoldAccent.copy(alpha = 0.15f) else MuseDarkBg)
                                            .border(1.dp, if (isUnseen) GoldAccent.copy(alpha = 0.5f) else MuseBorder, RoundedCornerShape(6.dp))
                                            .padding(8.dp)
                                    ) {
                                        Text(
                                            text = cap,
                                            color = if (isUnseen) GoldAccent else TextPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = if (isUnseen) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        modalIntentType = "mint"
                                        showPermissionModal = true
                                    },
                                    modifier = Modifier.fillMaxWidth().height(44.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MuseCyan),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Key, contentDescription = null, tint = Color.Black)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "GRANT ERC-7715 PERMISSION & MINT ($${merged.price})",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // TAB 2: VFV-MARKETPLACE [×z/=$] & IP AUCTIONS
        if (activeSubTab == "marketplace") {
            // Filter Pills
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "all" to "All (${listings.size})",
                        "oracle" to "Oracle Vault (${listings.count { it.ownerType == "oracle" }})",
                        "community" to "Community (${listings.count { it.ownerType == "community" }})"
                    ).forEach { (filterKey, label) ->
                        val isSelected = marketplaceFilter == filterKey
                        FilterChip(
                            selected = isSelected,
                            onClick = { marketplaceFilter = filterKey },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldAccent.copy(alpha = 0.2f),
                                selectedLabelColor = GoldAccent,
                                containerColor = MuseDarkSurface,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }
            }

            // Auction Cards List
            items(filteredListings, key = { it.id }) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .testTag("auction_item_${item.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MuseDarkSurface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(
                            if (item.ownerType == "oracle") listOf(GoldAccent, MusePurple) else listOf(MuseCyan, MuseGreen)
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (item.ownerType == "oracle") GoldAccent.copy(alpha = 0.2f) else MuseCyan.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (item.ownerType == "oracle") "🏛️ ORACLE VAULT" else "👥 COMMUNITY",
                                        color = if (item.ownerType == "oracle") GoldAccent else MuseCyan,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Score: ${item.score}",
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = "⏳ ${item.timeLeft}",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = item.name,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = item.trait,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                        )

                        // Bid Info
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MuseDarkBg)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Current Bid", color = TextMuted, fontSize = 9.sp)
                                Text("$${item.currentBid} USDC", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Column {
                                Text("Total Bids", color = TextMuted, fontSize = 9.sp)
                                Text("${item.bids} bids", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Column {
                                Text("Owner", color = TextMuted, fontSize = 9.sp)
                                Text(item.owner, color = TextSecondary, fontSize = 11.sp, maxLines = 1)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Action Buttons: Bid or Buy Now
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.placeBid(item.id) },
                                modifier = Modifier.weight(1f).height(36.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(MuseBorder, GoldAccent)))
                            ) {
                                Icon(Icons.Default.Gavel, contentDescription = null, modifier = Modifier.size(14.dp), tint = GoldAccent)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Bid (+$15)", fontSize = 11.sp, color = GoldAccent)
                            }

                            Button(
                                onClick = {
                                    modalIntentType = "buy"
                                    modalTargetItem = item
                                    showPermissionModal = true
                                },
                                modifier = Modifier.weight(1f).height(36.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                            ) {
                                Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Black)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Buy $${item.currentBid}", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Section: My Vault (Owned Dime10s & Listing on VFV)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MuseDarkSurfaceElevated),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(MuseCyan, MuseGreen))
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🔒 MY VAULT (OWNED IP-NFTS)",
                                color = MuseCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "${myVault.size} Owned",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (myVault.isEmpty()) {
                            Text(
                                text = "No minted Dime10s yet. Compare & Combine to mint your first IP-NFT!",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        } else {
                            myVault.forEach { nft ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MuseDarkBg)
                                        .border(1.dp, MuseBorder, RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(nft.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text("#${nft.nftId}", color = GoldAccent, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                                        }
                                        Text(nft.trait, color = TextSecondary, fontSize = 11.sp, maxLines = 1)

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Paid: $${nft.price} • Score: ${nft.score}", color = TextMuted, fontSize = 10.sp)

                                            Button(
                                                onClick = {
                                                    selectedNftToList = nft
                                                    listPriceInput = "${nft.price + 30}"
                                                },
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                                shape = RoundedCornerShape(6.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                                            ) {
                                                Text("List on VFV", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
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

        // TAB 3: ARCHITECTURE & BUILDER NOTES
        if (activeSubTab == "builder") {
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "BUILDER IMPLEMENTATION NOTES",
                        color = MuseGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "ERC-7715 Scoped Authority + Oracle Pricing + Smart Contract Architecture",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    CodeSnippetViewer(
                        title = "1. ERC-7715 Permission Grant (wallet_requestExecutionPermissions)",
                        language = "javascript",
                        code = """// wallet_grantPermissions — Dime10 × VFV
const permission = await window.ethereum.request({
  method: 'wallet_grantPermissions',
  params: [{
    chainId: '0x89', // Polygon
    expiry: Math.floor(Date.now()/1000) + 7*24*3600, // 7 days
    signer: { type: 'account', data: { address: user } },
    permissions: [
      {
        type: 'erc20-token-allowance',
        data: {
          token: '0xUSDC... (Polygon)',
          allowance: '250000000', // $250 USDC
          justification: 'Allow Dime10 Engine to spend up to 250 USDC for mint + marketplace'
        }
      },
      {
        type: 'contract-call-allowlist',
        data: {
          contracts: ['0xVFV...MARKETPLACE', '0xD10...FACTORY']
        }
      }
    ]
  }]
});"""
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    CodeSnippetViewer(
                        title = "2. Deterministic Oracle Pricing Formula",
                        language = "javascript",
                        code = """export function priceDime10({ combinedScore, novelty, featsCount, descLen }) {
  const base = 49;
  const scoreValue = combinedScore * 1.5; // quality signal
  const noveltyValue = novelty * 20;      // unseen mix premium
  const densityBonus = Math.min(18, descLen/28) + featsCount * 0.5;
  return Math.round(base + scoreValue + noveltyValue + densityBonus);
}

export function hostSplit({ salePrice, ownerType }) {
  if (ownerType === 'oracle') return { treasury: salePrice, host: 0, seller: 0 };
  const host = salePrice * 0.125; // 12.5% host fee to you
  const seller = salePrice * 0.875;
  return { treasury: 0, host, seller };
}"""
                    )
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

    // Modal: ERC-7715 Permission Grant Simulator
    if (showPermissionModal) {
        AlertDialog(
            onDismissRequest = { showPermissionModal = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MuseCyan),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("wallet_requestExecutionPermissions", fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MuseDarkBg)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Allow Dime10 Engine to spend up to 250 USDC on your behalf for 7 days, only on VFV Marketplace (0xVFV...)?",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Divider(color = MuseBorder)

                    Text("• Spending Cap: 250 USDC (Polygon Amoy)", color = MuseCyan, fontSize = 10.sp)
                    Text("• Contract Whitelist: 0xVFV... (Marketplace) + 0xD10... (Factory)", color = MuseGreen, fontSize = 10.sp)
                    Text("• Timebound: Auto-revokes in 7 days", color = GoldAccent, fontSize = 10.sp)
                    Text("• Gas Cap: 0.05 MATIC (Bundler sponsored)", color = TextSecondary, fontSize = 10.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (modalIntentType == "mint") {
                            currentMerged?.let { viewModel.mintMergedDime10(it) }
                        } else if (modalIntentType == "buy") {
                            modalTargetItem?.let { viewModel.buyAuctionItem(it) }
                        }
                        showPermissionModal = false
                        SensoryAudioEngine.playFusionIgnition()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MuseCyan)
                ) {
                    Text("Allow & Grant", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionModal = false }) {
                    Text("Deny", color = TextSecondary, fontSize = 11.sp)
                }
            },
            containerColor = MuseDarkSurfaceElevated
        )
    }

    // Modal: List NFT on VFV Dialog
    selectedNftToList?.let { nft ->
        AlertDialog(
            onDismissRequest = { selectedNftToList = null },
            title = { Text("List ${nft.name} on VFV [×z/=$]", fontSize = 14.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Enter auction starting price (USDC):", color = TextSecondary, fontSize = 11.sp)
                    OutlinedTextField(
                        value = listPriceInput,
                        onValueChange = { listPriceInput = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    val priceNum = listPriceInput.toIntOrNull() ?: 0
                    val hostFee = (priceNum * 0.125f).toInt()
                    val payout = priceNum - hostFee
                    Text("• Host Fee (12.5%): $$hostFee USDC", color = GoldAccent, fontSize = 10.sp)
                    Text("• You Receive: $$payout USDC", color = MuseGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val p = listPriceInput.toIntOrNull() ?: 100
                        viewModel.listNftOnVfv(nft, p)
                        selectedNftToList = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                ) {
                    Text("List on VFV", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedNftToList = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = MuseDarkSurfaceElevated
        )
    }
}

@Composable
fun SevenDimensionRadarCanvas(
    scoresA: Map<String, Int>,
    scoresB: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val maxRadius = (size.minDimension / 2f) - 24.dp.toPx()
        val numPoints = radarDimensions.size

        // Draw concentric polygon grid
        val levels = 4
        for (lvl in 1..levels) {
            val r = maxRadius * (lvl.toFloat() / levels)
            val gridPath = Path()
            for (i in 0 until numPoints) {
                val angle = (i * 2 * Math.PI / numPoints) - (Math.PI / 2)
                val x = center.x + (r * cos(angle)).toFloat()
                val y = center.y + (r * sin(angle)).toFloat()
                if (i == 0) gridPath.moveTo(x, y) else gridPath.lineTo(x, y)
            }
            gridPath.close()
            drawPath(gridPath, color = Color.White.copy(alpha = 0.08f), style = Stroke(width = 1.dp.toPx()))
        }

        // Draw radial axis lines
        for (i in 0 until numPoints) {
            val angle = (i * 2 * Math.PI / numPoints) - (Math.PI / 2)
            val x = center.x + (maxRadius * cos(angle)).toFloat()
            val y = center.y + (maxRadius * sin(angle)).toFloat()
            drawLine(
                color = Color.White.copy(alpha = 0.12f),
                start = center,
                end = Offset(x, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Draw Polygon for Project A (Cyan)
        val pathA = Path()
        for (i in 0 until numPoints) {
            val dimId = radarDimensions[i].id
            val score = (scoresA[dimId] ?: 70).coerceIn(20, 100)
            val r = maxRadius * (score / 100f)
            val angle = (i * 2 * Math.PI / numPoints) - (Math.PI / 2)
            val x = center.x + (r * cos(angle)).toFloat()
            val y = center.y + (r * sin(angle)).toFloat()
            if (i == 0) pathA.moveTo(x, y) else pathA.lineTo(x, y)
        }
        pathA.close()
        drawPath(pathA, color = MuseCyan.copy(alpha = 0.25f))
        drawPath(pathA, color = MuseCyan, style = Stroke(width = 2.dp.toPx()))

        // Draw Polygon for Project B (Red)
        val pathB = Path()
        for (i in 0 until numPoints) {
            val dimId = radarDimensions[i].id
            val score = (scoresB[dimId] ?: 70).coerceIn(20, 100)
            val r = maxRadius * (score / 100f)
            val angle = (i * 2 * Math.PI / numPoints) - (Math.PI / 2)
            val x = center.x + (r * cos(angle)).toFloat()
            val y = center.y + (r * sin(angle)).toFloat()
            if (i == 0) pathB.moveTo(x, y) else pathB.lineTo(x, y)
        }
        pathB.close()
        drawPath(pathB, color = MuseRed.copy(alpha = 0.25f))
        drawPath(pathB, color = MuseRed, style = Stroke(width = 2.dp.toPx()))
    }
}

fun calculateScores(name: String, desc: String, feats: String, baseSeed: Int): Map<String, Int> {
    val text = (name + desc + feats).lowercase()
    val map = mutableMapOf<String, Int>()
    radarDimensions.forEachIndexed { idx, dim ->
        val hash = (text.hashCode() + idx * 37).let { kotlin.math.abs(it) % 20 }
        val boost = if (text.contains("ai") || text.contains("erc") || text.contains("permission")) 5 else 0
        map[dim.id] = (baseSeed + hash - 10 + boost).coerceIn(45, 98)
    }
    return map
}
