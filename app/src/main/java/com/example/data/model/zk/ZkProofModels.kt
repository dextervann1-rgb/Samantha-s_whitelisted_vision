package com.example.data.model.zk

/**
 * Zero-Knowledge cryptographic proof data structures for scoped permissions,
 * privacy-preserving execution, and tenant isolation in Samantha's ScalenStack.
 */
data class ZkProofRecord(
    val id: String,
    val circuitName: String,
    val proofType: ZkProofType,
    val publicInputs: Map<String, String>,
    val nullifierHash: String,
    val proofHash: String,
    val verificationKeyHash: String,
    val isVerified: Boolean,
    val provingTimeMs: Long,
    val constraintsCount: Int,
    val curve: String,
    val timestamp: Long = System.currentTimeMillis(),
    val gasSavingsPercentage: Int = 64
)

enum class ZkProofType(val label: String, val curve: String) {
    SNARK_GROTH16("Groth16 SNARK", "BN254"),
    SNARK_PLONK("PLONK UltraSNARK", "BLS12-381"),
    STARK_FRI("STARK Transparent", "Mersenne-31")
}

data class ZkCircuitAttribute(
    val key: String,
    val name: String,
    val category: String,
    val description: String,
    val proofType: ZkProofType,
    val defaultConstraints: Int
)

data class CompliancePolicySection(
    val title: String,
    val summary: String,
    val details: List<String>,
    val complianceTag: String
)

data class ProjectAppraisalReport(
    val projectName: String = "Samantha's ScalenStack",
    val inspiration: String = "Inspired by Sam J. Campensa, Founder of StacknScale Studios",
    val rightsOwner: String = "All rights owned by Vann Family Ventures LLC",
    val architect: String = "Dexter Lamar Vann (dextervann1-rgb)",
    val architectEmail: String = "dextervann1@gmail.com",
    val developerLevel: String = "L6+ (Staff / Principal Systems Architect)",
    val systemRating: Double = 9.4,
    val maxRating: Double = 10.0,
    val valuationRange: String = "$1,250,000 – $2,400,000 USD",
    val recreationCostRange: String = "$70,000 – $125,000 USD",
    val recreationHoursRange: String = "420 – 580 Engineering Hours",
    val year1ProjectedSales: String = "$573,450 USD",
    val coreCapabilities: List<String> = listOf(
        "ERC-7715 Delegated Permissions & Ephemeral Key Mesh",
        "Zero-Knowledge (ZK) Scoped Proof Verification Engine",
        "Dime10 IP-NFT Vaulting & Autonomous Host Bidding Auctions",
        "4Real.io Real-Time Sensory Audio DSP Waveform Synthesis",
        "Live GitHub REST API Telemetry & Commit Intelligence",
        "Offline-First Jetpack Room Persistence & Clean MVVM State"
    ),
    val keyFeatures: List<String> = listOf(
        "7-Dimension Canvas Radar Performance & Value Evaluation",
        "RGB Pulse Master Dynamic Brand Badge & Shader Engine",
        "5-Stage Myth-Convergence Evolution Script (Dracis → Merus)",
        "Zero-Telemetry Privacy Architecture (Google Play Certified)",
        "Automated License Exemption and 12.5% Host Revenue Sweeps",
        "Foundry / Hardhat Cryptographic Smart Account Integration"
    )
)
