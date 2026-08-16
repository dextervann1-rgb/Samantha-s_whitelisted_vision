package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SensoryAudioEngine
import com.example.data.local.AppDatabase
import com.example.data.model.FusedCapsule
import com.example.data.model.RealioAsset
import com.example.data.model.Totem
import com.example.data.model.Web3Repo
import com.example.data.model.github.GitHubRepoStats
import com.example.data.model.zk.CompliancePolicySection
import com.example.data.model.zk.ProjectAppraisalReport
import com.example.data.model.zk.ZkCircuitAttribute
import com.example.data.model.zk.ZkProofRecord
import com.example.data.repository.GitHubRepository
import com.example.data.repository.ZkProofEngine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppTab(val title: String, val iconLabel: String) {
    SCALENSTACK("ScalenStack", "🚀"),
    FUSION_HUB("Fusion Hub", "⚡"),
    DIME10_VFV("Dime10 & VFV", "💎"),
    WEB3_REPOS("Web3 Git", "🌐"),
    REALIO_LAB("4Real Lab", "🧬"),
    BRAND_VAULT("Brand Kit", "🏷️")
}

data class DimeAuctionItem(
    val id: String,
    val name: String,
    val score: Int,
    val novelty: Float,
    val startPrice: Int,
    val currentBid: Int,
    val timeLeft: String,
    val ownerType: String, // "oracle" or "community"
    val owner: String,
    val bids: Int,
    val dna: String,
    val trait: String
)

data class OwnedDimeNft(
    val id: String,
    val name: String,
    val score: Int,
    val novelty: Float,
    val price: Int,
    val nftId: Int,
    val trait: String
)

data class MergedDime10(
    val name: String,
    val pitch: String,
    val capabilities: List<String>,
    val useCases: List<String>,
    val emergentTrait: String,
    val combinedScore: Int,
    val novelty: Float,
    val price: Int
)

data class ExecutionResult(
    val capsuleTitle: String,
    val txHash: String,
    val gasUsedGwei: Int,
    val blockNumber: Long,
    val timestamp: Long = System.currentTimeMillis()
)

class MuseFusionViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val repoDao = database.repoDao()
    private val assetDao = database.assetDao()
    private val capsuleDao = database.capsuleDao()

    val currentTab = MutableStateFlow(AppTab.FUSION_HUB)
    val selectedTotemFilter = MutableStateFlow<String?>(null)
    val currentNetwork = MutableStateFlow("Ethereum Mainnet")
    val walletBalanceEth = MutableStateFlow("4.280 ETH")

    val repos: StateFlow<List<Web3Repo>> = repoDao.getAllRepos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val assets: StateFlow<List<RealioAsset>> = assetDao.getAllAssets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val capsules: StateFlow<List<FusedCapsule>> = capsuleDao.getAllCapsules()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Fusion Lab Selection State
    val selectedRepoForFusion = MutableStateFlow<Web3Repo?>(null)
    val selectedAssetForFusion = MutableStateFlow<RealioAsset?>(null)
    val fusionCustomTitle = MutableStateFlow("")
    val isFusing = MutableStateFlow(false)

    // Dime10 & VFV State
    val auctionListings = MutableStateFlow(
        listOf(
            DimeAuctionItem(
                id = "vfv-01",
                name = "Klien-7715 Guard Mesh",
                score = 91,
                novelty = 4.8f,
                startPrice = 149,
                currentBid = 210,
                timeLeft = "14h 22m",
                ownerType = "oracle",
                owner = "Oracle Vault",
                bids = 12,
                dna = "Klien Key × ERC-7715",
                trait = "Self-Revoking Swarm Mesh: permissions expire and re-delegate on-chain"
            ),
            DimeAuctionItem(
                id = "vfv-02",
                name = "Intent-7715 Vault Engine",
                score = 88,
                novelty = 4.2f,
                startPrice = 129,
                currentBid = 165,
                timeLeft = "2d 04h",
                ownerType = "oracle",
                owner = "Oracle Vault",
                bids = 7,
                dna = "Intent × MetaMask Gator",
                trait = "Intent-Negotiation Layer: AI agents auto-bid spending caps across L2s"
            ),
            DimeAuctionItem(
                id = "vfv-03",
                name = "Swarm Authority Protocol",
                score = 94,
                novelty = 4.9f,
                startPrice = 189,
                currentBid = 310,
                timeLeft = "06h 11m",
                ownerType = "community",
                owner = "0x8a...3f21",
                bids = 23,
                dna = "Swarm × Session Keys",
                trait = "Zero-Knowledge Permission Proof: prove allowances without revealing caps"
            ),
            DimeAuctionItem(
                id = "vfv-04",
                name = "Zero-Popup DeFi Matrix",
                score = 86,
                novelty = 3.9f,
                startPrice = 99,
                currentBid = 125,
                timeLeft = "1d 18h",
                ownerType = "oracle",
                owner = "Oracle Vault",
                bids = 4,
                dna = "DeFi × 4337 Bundler",
                trait = "Cross-Session Memory: permissions learn from user behavior"
            ),
            DimeAuctionItem(
                id = "vfv-05",
                name = "Ephemeral Yield Router",
                score = 89,
                novelty = 4.5f,
                startPrice = 159,
                currentBid = 195,
                timeLeft = "11h 05m",
                ownerType = "community",
                owner = "0x42...b7e9",
                bids = 9,
                dna = "Ephemeral × Yield",
                trait = "Monetized Delegation Marketplace: rent scoped keys to agents at oracle rates"
            ),
            DimeAuctionItem(
                id = "vfv-06",
                name = "Gator-Scoped Oracle Mesh",
                score = 92,
                novelty = 4.7f,
                startPrice = 175,
                currentBid = 240,
                timeLeft = "03h 44m",
                ownerType = "oracle",
                owner = "Oracle Vault",
                bids = 18,
                dna = "Gator SDK × Scoped Caps",
                trait = "Self-Revoking Swarm Mesh: dynamic autonomous healing without seed exposure"
            )
        )
    )

    val myVaultNfts = MutableStateFlow(
        listOf(
            OwnedDimeNft(
                id = "owned-seed",
                name = "Klien-4337 Genesis Guard",
                score = 90,
                novelty = 4.6f,
                price = 189,
                nftId = 1024,
                trait = "Genesis Permission Mesh — Founder Edition with zero seed exposure."
            )
        )
    )

    val currentMergedDime10 = MutableStateFlow<MergedDime10?>(null)

    // Last execution result for modal
    val lastExecutionResult = MutableStateFlow<ExecutionResult?>(null)
    val inspectingCapsule = MutableStateFlow<FusedCapsule?>(null)
    val inspectingRepo = MutableStateFlow<Web3Repo?>(null)
    val inspectingAsset = MutableStateFlow<RealioAsset?>(null)

    // GitHub API Integration State
    private val gitHubRepository = GitHubRepository()
    val liveGitHubStats = MutableStateFlow<GitHubRepoStats?>(null)
    val isFetchingGitHub = MutableStateFlow(false)
    val gitHubError = MutableStateFlow<String?>(null)

    // Samantha's ScalenStack ZK & Compliance State
    val appraisalReport = ZkProofEngine.appraisalReport
    val availableZkCircuits = ZkProofEngine.availableCircuits
    val compliancePolicies = ZkProofEngine.compliancePolicies
    val zkProofHistory = MutableStateFlow<List<ZkProofRecord>>(emptyList())
    val selectedZkCircuit = MutableStateFlow(ZkProofEngine.availableCircuits.first())
    val isGeneratingZk = MutableStateFlow(false)
    val latestZkProof = MutableStateFlow<ZkProofRecord?>(null)

    init {
        // Auto-select first items for easy preview
        viewModelScope.launch {
            repos.collectLatest { list ->
                if (list.isNotEmpty() && selectedRepoForFusion.value == null) {
                    selectedRepoForFusion.value = list.first()
                }
            }
        }
        viewModelScope.launch {
            assets.collectLatest { list ->
                if (list.isNotEmpty() && selectedAssetForFusion.value == null) {
                    selectedAssetForFusion.value = list.first()
                }
            }
        }
    }

    fun setTab(tab: AppTab) {
        currentTab.value = tab
        SensoryAudioEngine.playTone(500f, 50, 0.2f)
    }

    fun setTotemFilter(totem: String?) {
        selectedTotemFilter.value = totem
    }

    fun setNetwork(network: String) {
        currentNetwork.value = network
    }

    fun selectRepoForFusion(repo: Web3Repo) {
        selectedRepoForFusion.value = repo
        SensoryAudioEngine.playTone(580f, 70, 0.3f)
    }

    fun selectAssetForFusion(asset: RealioAsset) {
        selectedAssetForFusion.value = asset
        SensoryAudioEngine.playTone(680f, 70, 0.3f)
    }

    fun fuseCurrentSelection() {
        val repo = selectedRepoForFusion.value ?: return
        val asset = selectedAssetForFusion.value ?: return

        viewModelScope.launch {
            isFusing.value = true
            SensoryAudioEngine.playFusionIgnition()

            val totem = Totem.entries.find { it.name == repo.totem } ?: Totem.DRAGON
            val title = if (fusionCustomTitle.value.isNotBlank()) {
                fusionCustomTitle.value.trim()
            } else {
                "${repo.name.substringAfterLast("/")} × ${asset.title.take(16)}"
            }

            val newCapsule = FusedCapsule(
                id = "cap_fused_${UUID.randomUUID().toString().take(8)}",
                title = title,
                repoId = repo.id,
                repoName = repo.name,
                assetId = asset.id,
                assetName = asset.title,
                totem = totem.name,
                fusionHash = "0x" + UUID.randomUUID().toString().replace("-", "").take(16).uppercase(),
                chain = repo.chain,
                smartContractSnippet = "${repo.contractType}: ${repo.abiFunctions.take(30)}...",
                shaderConfig = "Frequency: ${asset.frequency}Hz | Speed: ${asset.speed}x | Theme: ${asset.rgbColor}",
                gasEstimateGwei = (8..32).random(),
                isStarred = true
            )

            capsuleDao.insertCapsule(newCapsule)
            fusionCustomTitle.value = ""
            isFusing.value = false
            currentTab.value = AppTab.FUSION_HUB
        }
    }

    fun toggleStar(capsule: FusedCapsule) {
        viewModelScope.launch {
            capsuleDao.updateCapsule(capsule.copy(isStarred = !capsule.isStarred))
        }
    }

    fun deleteCapsule(capsule: FusedCapsule) {
        viewModelScope.launch {
            capsuleDao.deleteCapsule(capsule)
            if (inspectingCapsule.value?.id == capsule.id) {
                inspectingCapsule.value = null
            }
        }
    }

    fun executeCapsule(capsule: FusedCapsule) {
        viewModelScope.launch {
            val randomTx = "0x" + UUID.randomUUID().toString().replace("-", "")
            val block = 19482010L + (1..500).random()
            lastExecutionResult.value = ExecutionResult(
                capsuleTitle = capsule.title,
                txHash = randomTx,
                gasUsedGwei = capsule.gasEstimateGwei,
                blockNumber = block
            )
        }
    }

    fun clearExecutionResult() {
        lastExecutionResult.value = null
    }

    fun addCustomRepo(
        name: String,
        description: String,
        chain: String,
        contractType: String,
        smartContractCode: String,
        abiFunctions: String,
        totem: String
    ) {
        viewModelScope.launch {
            val newRepo = Web3Repo(
                id = "repo_custom_${UUID.randomUUID().toString().take(6)}",
                name = name,
                description = description,
                owner = "dextervann1",
                stars = 1,
                forks = 0,
                contractType = contractType,
                chain = chain,
                smartContractCode = smartContractCode,
                abiFunctions = abiFunctions,
                commitHash = "0x" + UUID.randomUUID().toString().replace("-", "").take(12),
                isTokenGated = false,
                requiredToken = "None",
                totem = totem
            )
            repoDao.insertRepo(newRepo)
            selectedRepoForFusion.value = newRepo
            SensoryAudioEngine.playTone(750f, 100, 0.4f)
        }
    }

    fun addCustomAsset(
        title: String,
        category: String,
        totem: String,
        frequency: Float,
        speed: Float,
        rgbColor: String,
        description: String,
        codeSnippet: String
    ) {
        viewModelScope.launch {
            val newAsset = RealioAsset(
                id = "asset_custom_${UUID.randomUUID().toString().take(6)}",
                title = title,
                category = category,
                totem = totem,
                frequency = frequency,
                speed = speed,
                rgbColor = rgbColor,
                isRealTime = true,
                description = description,
                codeSnippet = codeSnippet
            )
            assetDao.insertAsset(newAsset)
            selectedAssetForFusion.value = newAsset
            SensoryAudioEngine.playTone(850f, 100, 0.4f)
        }
    }

    fun placeBid(itemId: String) {
        auctionListings.value = auctionListings.value.map { item ->
            if (item.id == itemId) {
                val increment = (10..35).random()
                SensoryAudioEngine.playTone(720f, 80, 0.4f)
                item.copy(currentBid = item.currentBid + increment, bids = item.bids + 1)
            } else item
        }
    }

    fun buyAuctionItem(item: DimeAuctionItem): OwnedDimeNft {
        val newNft = OwnedDimeNft(
            id = "owned_${UUID.randomUUID().toString().take(6)}",
            name = item.name,
            score = item.score,
            novelty = item.novelty,
            price = item.currentBid,
            nftId = (4000..8999).random(),
            trait = item.trait
        )
        myVaultNfts.value = listOf(newNft) + myVaultNfts.value
        auctionListings.value = auctionListings.value.filter { it.id != item.id }
        SensoryAudioEngine.playFusionIgnition()
        return newNft
    }

    fun mintMergedDime10(merged: MergedDime10): OwnedDimeNft {
        val newNft = OwnedDimeNft(
            id = "owned_${UUID.randomUUID().toString().take(6)}",
            name = merged.name,
            score = merged.combinedScore,
            novelty = merged.novelty,
            price = merged.price,
            nftId = (4000..8999).random(),
            trait = merged.emergentTrait
        )
        myVaultNfts.value = listOf(newNft) + myVaultNfts.value
        SensoryAudioEngine.playFusionIgnition()
        return newNft
    }

    fun listNftOnVfv(nft: OwnedDimeNft, price: Int) {
        val listing = DimeAuctionItem(
            id = "vfv_${UUID.randomUUID().toString().take(6)}",
            name = nft.name,
            score = nft.score,
            novelty = nft.novelty,
            startPrice = price,
            currentBid = price,
            timeLeft = "7d 00h",
            ownerType = "community",
            owner = "You (0xYou)",
            bids = 0,
            dna = "User Vault → Listed",
            trait = nft.trait
        )
        auctionListings.value = listOf(listing) + auctionListings.value
        myVaultNfts.value = myVaultNfts.value.filter { it.id != nft.id }
        SensoryAudioEngine.playTone(880f, 100, 0.4f)
    }

    fun fetchLiveGitHubStats(owner: String, repo: String, token: String? = null) {
        viewModelScope.launch {
            isFetchingGitHub.value = true
            gitHubError.value = null
            SensoryAudioEngine.playTone(520f, 40, 0.3f)
            val result = gitHubRepository.fetchRepoStats(owner.trim(), repo.trim(), token?.trim())
            result.onSuccess { stats ->
                liveGitHubStats.value = stats
                isFetchingGitHub.value = false
                SensoryAudioEngine.playTone(780f, 60, 0.4f)
            }.onFailure { err ->
                gitHubError.value = err.message ?: "Failed to fetch GitHub repository data"
                isFetchingGitHub.value = false
                SensoryAudioEngine.playTone(320f, 120, 0.4f)
            }
        }
    }

    fun clearGitHubStats() {
        liveGitHubStats.value = null
        gitHubError.value = null
    }

    fun selectZkCircuit(circuit: ZkCircuitAttribute) {
        selectedZkCircuit.value = circuit
        SensoryAudioEngine.playTone(640f, 40, 0.3f)
    }

    fun generateZkProof(witnessData: String = "VFV_ZK_SCOPE_${System.currentTimeMillis()}") {
        viewModelScope.launch {
            isGeneratingZk.value = true
            SensoryAudioEngine.playTone(480f, 60, 0.3f)
            val circuit = selectedZkCircuit.value
            val proof = ZkProofEngine.generateZkProof(circuit, witnessData)
            latestZkProof.value = proof
            zkProofHistory.value = listOf(proof) + zkProofHistory.value
            isGeneratingZk.value = false
            SensoryAudioEngine.playTone(920f, 80, 0.4f)
        }
    }
}
