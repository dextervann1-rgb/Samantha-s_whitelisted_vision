package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Totem(val title: String, val meaning: String, val emoji: String, val colorHex: String) {
    DRAGON("Dragon", "Ignition / Fuel", "🔥", "#FF1E3C"),
    PHOENIX("Phoenix", "Regeneration / Proof", "♻️", "#00FF9D"),
    KRAKEN("Kraken", "Depth / Scale", "🦑", "#00E5FF"),
    UNICORN("Unicorn", "Signal / Purity", "🦄", "#9D4EDD"),
    MERMAID("Mermaid", "Amphibious Interface", "🧜", "#06D6A0")
}

@Entity(tableName = "web3_repos")
data class Web3Repo(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val owner: String,
    val stars: Int,
    val forks: Int,
    val contractType: String, // ERC-20, ERC-721, Account Abstraction, DePIN, ZeroKnowledge
    val chain: String,        // Ethereum, Arbitrum, Solana, Base, Polygon
    val smartContractCode: String,
    val abiFunctions: String, // Comma separated method names
    val commitHash: String,
    val isTokenGated: Boolean,
    val requiredToken: String,
    val totem: String,
    val isVerified: Boolean = true
)

@Entity(tableName = "realio_assets")
data class RealioAsset(
    @PrimaryKey val id: String,
    val title: String,
    val category: String, // Waveform Oscillator, Particle Mesh, Holographic Shader, Audio Matrix, Brand Badge
    val totem: String,
    val frequency: Float,
    val speed: Float,
    val rgbColor: String,
    val isRealTime: Boolean,
    val description: String,
    val codeSnippet: String
)

@Entity(tableName = "fused_capsules")
data class FusedCapsule(
    @PrimaryKey val id: String,
    val title: String,
    val repoId: String,
    val repoName: String,
    val assetId: String,
    val assetName: String,
    val totem: String,
    val fusionHash: String,
    val chain: String,
    val smartContractSnippet: String,
    val shaderConfig: String,
    val gasEstimateGwei: Int,
    val isStarred: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis()
)
