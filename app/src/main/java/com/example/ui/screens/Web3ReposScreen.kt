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
import com.example.data.model.Totem
import com.example.data.model.Web3Repo
import com.example.ui.components.CodeSnippetViewer
import com.example.ui.components.TotemSelectorBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.MuseFusionViewModel

@Composable
fun Web3ReposScreen(
    viewModel: MuseFusionViewModel,
    modifier: Modifier = Modifier
) {
    val repos by viewModel.repos.collectAsState()
    val selectedTotem by viewModel.selectedTotemFilter.collectAsState()
    val selectedRepoForFusion by viewModel.selectedRepoForFusion.collectAsState()

    var showAddRepoDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // GitHub Live API Fetcher States
    val liveGitHubStats by viewModel.liveGitHubStats.collectAsState()
    val isFetchingGitHub by viewModel.isFetchingGitHub.collectAsState()
    val gitHubError by viewModel.gitHubError.collectAsState()
    var gitOwnerInput by remember { mutableStateOf("ethereum") }
    var gitRepoInput by remember { mutableStateOf("solidity") }
    var showGitHubExplorer by remember { mutableStateOf(false) }

    val filteredRepos = remember(repos, selectedTotem, searchQuery) {
        repos.filter { repo ->
            (selectedTotem == null || repo.totem == selectedTotem) &&
                    (searchQuery.isBlank() || repo.name.contains(searchQuery, ignoreCase = true) ||
                            repo.description.contains(searchQuery, ignoreCase = true) ||
                            repo.contractType.contains(searchQuery, ignoreCase = true) ||
                            repo.chain.contains(searchQuery, ignoreCase = true))
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    SensoryAudioEngine.playTone(600f, 60, 0.4f)
                    showAddRepoDialog = true
                },
                icon = { Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black) },
                text = { Text("Add Web3 Repo", fontWeight = FontWeight.Bold, color = Color.Black) },
                containerColor = MuseCyan,
                modifier = Modifier
                    .padding(bottom = 80.dp)
                    .testTag("add_repo_fab")
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("web3_repos_list"),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Header Search & Info
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "WEB3 GITHUB REPOSITORY LIBRARY",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Decentralized contracts, ERC standards, and token-bound logic ready to fuse with 4Real sensory stems.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search Web3 repos, EVM contracts, chains...", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MuseCyan) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = TextSecondary)
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_repos_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MuseCyan,
                            unfocusedBorderColor = MuseBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = MuseDarkSurface,
                            unfocusedContainerColor = MuseDarkSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Toggle Live GitHub Explorer
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MuseDarkSurface)
                            .border(1.dp, if (showGitHubExplorer) MuseCyan else MuseBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                showGitHubExplorer = !showGitHubExplorer
                                SensoryAudioEngine.playTone(600f, 40, 0.3f)
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🐙", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Live GitHub API Integration (Stats & Commits)",
                                color = if (showGitHubExplorer) MuseCyan else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Icon(
                            if (showGitHubExplorer) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = if (showGitHubExplorer) MuseCyan else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Live GitHub Explorer Panel
                    AnimatedVisibility(visible = showGitHubExplorer) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MuseDarkBg),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.horizontalGradient(listOf(MuseCyan, MusePurple))
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "FETCH REMOTE GITHUB STATS & COMMITS",
                                    color = MuseCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "Retrofit service client querying api.github.com for repository telemetry & recent commits.",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = gitOwnerInput,
                                        onValueChange = { gitOwnerInput = it },
                                        label = { Text("Owner (e.g. ethereum)", fontSize = 10.sp) },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        textStyle = LocalTextStyle.current.copy(fontSize = 11.sp)
                                    )
                                    OutlinedTextField(
                                        value = gitRepoInput,
                                        onValueChange = { gitRepoInput = it },
                                        label = { Text("Repo (e.g. solidity)", fontSize = 10.sp) },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        textStyle = LocalTextStyle.current.copy(fontSize = 11.sp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = {
                                        if (gitOwnerInput.isNotBlank() && gitRepoInput.isNotBlank()) {
                                            viewModel.fetchLiveGitHubStats(gitOwnerInput, gitRepoInput)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(40.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MuseCyan),
                                    shape = RoundedCornerShape(8.dp),
                                    enabled = !isFetchingGitHub
                                ) {
                                    if (isFetchingGitHub) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            color = Color.Black,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Querying GitHub API...", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    } else {
                                        Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Fetch Stats & Commit History", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                gitHubError?.let { err ->
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "⚠️ $err",
                                        color = MuseRed,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                liveGitHubStats?.let { stats ->
                                    Spacer(modifier = Modifier.height(12.dp))
                                    // Repo Metrics Box
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MuseDarkSurfaceElevated)
                                            .border(1.dp, MuseBorder, RoundedCornerShape(8.dp))
                                            .padding(12.dp)
                                    ) {
                                        Column {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = stats.fullName,
                                                    color = TextPrimary,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                                Text(
                                                    text = stats.language,
                                                    color = GoldAccent,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }

                                            Text(
                                                text = stats.description,
                                                color = TextSecondary,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(vertical = 4.dp)
                                            )

                                            // Quick Stats Row
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 6.dp),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                listOf(
                                                    "⭐ ${stats.stars}" to "Stars",
                                                    "🍴 ${stats.forks}" to "Forks",
                                                    "👁️ ${stats.watchers}" to "Watchers",
                                                    "❗ ${stats.openIssues}" to "Issues"
                                                ).forEach { (value, label) ->
                                                    Box(
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .background(MuseDarkBg)
                                                            .padding(vertical = 4.dp),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                            Text(value, color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                            Text(label, color = TextMuted, fontSize = 8.sp)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Recent Commits List
                                    if (stats.recentCommits.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "RECENT COMMITS (${stats.recentCommits.size})",
                                            color = MuseGreen,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))

                                        stats.recentCommits.take(5).forEach { commit ->
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 2.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(MuseDarkBg)
                                                    .border(1.dp, MuseBorder, RoundedCornerShape(6.dp))
                                                    .padding(8.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = commit.message,
                                                            color = TextPrimary,
                                                            fontSize = 11.sp,
                                                            maxLines = 1
                                                        )
                                                        Text(
                                                            text = "by ${commit.authorName} • ${commit.date}",
                                                            color = TextMuted,
                                                            fontSize = 9.sp
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = commit.shortSha,
                                                        color = MuseCyan,
                                                        fontSize = 10.sp,
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

            // Totem Filter
            item {
                TotemSelectorBar(
                    selectedTotem = selectedTotem,
                    onSelectTotem = { viewModel.setTotemFilter(it) }
                )
            }

            // Items List
            if (filteredRepos.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No Web3 repositories matched your criteria", color = TextMuted, fontSize = 13.sp)
                    }
                }
            } else {
                items(filteredRepos, key = { it.id }) { repo ->
                    val isSelected = selectedRepoForFusion?.id == repo.id
                    Web3RepoCard(
                        repo = repo,
                        isSelected = isSelected,
                        onSelectForFusion = { viewModel.selectRepoForFusion(repo) },
                        onInspect = { viewModel.inspectingRepo.value = repo }
                    )
                }
            }
        }
    }

    if (showAddRepoDialog) {
        AddRepoDialog(
            onDismiss = { showAddRepoDialog = false },
            onAdd = { name, desc, chain, cType, code, abi, totem ->
                viewModel.addCustomRepo(name, desc, chain, cType, code, abi, totem)
                showAddRepoDialog = false
            }
        )
    }
}

@Composable
fun Web3RepoCard(
    repo: Web3Repo,
    isSelected: Boolean,
    onSelectForFusion: () -> Unit,
    onInspect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totem = Totem.entries.find { it.name == repo.totem } ?: Totem.DRAGON
    val totemColor = Color(android.graphics.Color.parseColor(totem.colorHex))
    var expandedCode by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("web3_repo_card_${repo.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MuseDarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                if (isSelected) listOf(MuseCyan, MuseGreen) else listOf(MuseBorder, MuseBorder)
            )
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Owner/Name, Chain Pill, Stars
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
                            text = repo.name,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Chain: ",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                            Text(
                                text = repo.chain,
                                color = MuseCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(" • ", color = TextMuted, fontSize = 10.sp)
                            Text(
                                text = repo.contractType,
                                color = totemColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Stars & Forks
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = "${repo.stars}", color = TextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.ForkRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = "${repo.forks}", color = TextSecondary, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = repo.description,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Commit Hash + Token Gate Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Commit: ${repo.commitHash}",
                    color = TextMuted,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                )

                if (repo.isTokenGated) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MuseRed.copy(alpha = 0.2f))
                            .border(1.dp, MuseRed.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "🔒 ${repo.requiredToken}",
                            color = MuseRed,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            AnimatedVisibility(visible = expandedCode) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    CodeSnippetViewer(
                        code = repo.smartContractCode,
                        title = "${repo.name} (Solidity)"
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { expandedCode = !expandedCode },
                    modifier = Modifier.weight(1f).height(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(MuseBorder, MuseCyan)))
                ) {
                    Icon(
                        if (expandedCode) Icons.Default.ExpandLess else Icons.Default.Code,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (expandedCode) "Hide Code" else "View Code", fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        SensoryAudioEngine.playTone(660f, 60, 0.4f)
                        onSelectForFusion()
                    },
                    modifier = Modifier.weight(1f).height(36.dp).testTag("select_fusion_repo_${repo.id}"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) MuseGreen else MuseDarkSurfaceElevated
                    ),
                    border = if (!isSelected) ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(MuseCyan, MuseGreen))) else null
                ) {
                    Icon(
                        if (isSelected) Icons.Default.Check else Icons.Default.Bolt,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (isSelected) Color.Black else MuseCyan
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isSelected) "Selected" else "Select to Fuse",
                        color = if (isSelected) Color.Black else TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AddRepoDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, description: String, chain: String, contractType: String, code: String, abi: String, totem: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var chain by remember { mutableStateOf("Ethereum") }
    var contractType by remember { mutableStateOf("ERC-20") }
    var code by remember {
        mutableStateOf(
            "// SPDX-License-Identifier: MIT\npragma solidity ^0.8.20;\n\ncontract CustomTokenBound {\n    string public name = \"RGB Muse Stem\";\n}"
        )
    }
    var abi by remember { mutableStateOf("name,mint,burn") }
    var selectedTotem by remember { mutableStateOf(Totem.DRAGON.name) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add Web3 Git Repository", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Repo Name (e.g. org/project)", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("repo_name_input")
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = chain,
                        onValueChange = { chain = it },
                        label = { Text("Chain", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = contractType,
                        onValueChange = { contractType = it },
                        label = { Text("Contract Type", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Solidity Code", fontSize = 11.sp) },
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onAdd(name, description, chain, contractType, code, abi, selectedTotem)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MuseCyan),
                modifier = Modifier.testTag("confirm_add_repo_btn")
            ) {
                Text("Add Repo", color = Color.Black, fontWeight = FontWeight.Bold)
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
