package com.example.data.local

import com.example.data.model.FusedCapsule
import com.example.data.model.RealioAsset
import com.example.data.model.Totem
import com.example.data.model.Web3Repo

object SampleData {
    val initialRepos = listOf(
        Web3Repo(
            id = "repo_muse_vault",
            name = "dextervann1/rgb-muse-vault",
            description = "Token-bound private sensory asset vault with ERC-6551 smart contract account.",
            owner = "dextervann1",
            stars = 428,
            forks = 89,
            contractType = "ERC-6551 TBA",
            chain = "Ethereum",
            smartContractCode = """// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

import "@openzeppelin/contracts/token/ERC721/IERC721.sol";

contract RGBMuseVault {
    string public constant PHILOSOPHY = "Observe. Disturb. Evolve. No ceilings.";
    address public immutable owner;
    mapping(bytes32 => bool) public verifiedStems;

    event StemIgnited(bytes32 indexed stemHash, string totem);

    constructor() {
        owner = msg.sender;
    }

    function verifySensoryCapsule(bytes32 stemHash, string calldata totem) external returns (bool) {
        verifiedStems[stemHash] = true;
        emit StemIgnited(stemHash, totem);
        return true;
    }
}""",
            abiFunctions = "verifySensoryCapsule,verifiedStems,PHILOSOPHY,owner",
            commitHash = "0x9f4a1c7e2b830d52a4f61e89c3",
            isTokenGated = true,
            requiredToken = "MUSE-RGB >= 1.0",
            totem = Totem.DRAGON.name
        ),
        Web3Repo(
            id = "repo_4real_dex",
            name = "4real-protocol/realtime-amm",
            description = "High-throughput constant-product AMM with real-time audio-haptic liquidity pools.",
            owner = "4real-labs",
            stars = 1204,
            forks = 312,
            contractType = "AMM Core",
            chain = "Arbitrum",
            smartContractCode = """// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

contract RealtimeAMM {
    uint256 public reserve0;
    uint256 public reserve1;
    uint256 public constant SENSORY_MULTIPLIER = 10000;

    event Swap(address indexed sender, uint256 amount0In, uint256 amount1Out, uint32 rgbFreq);

    function swap(uint256 amount0In, uint256 minAmount1Out, uint32 rgbFreq) external returns (uint256 amount1Out) {
        require(amount0In > 0, "Zero input");
        amount1Out = (amount0In * reserve1) / (reserve0 + amount0In);
        require(amount1Out >= minAmount1Out, "Slippage error");
        reserve0 += amount0In;
        reserve1 -= amount1Out;
        emit Swap(msg.sender, amount0In, amount1Out, rgbFreq);
    }
}""",
            abiFunctions = "swap,reserve0,reserve1,SENSORY_MULTIPLIER",
            commitHash = "0x3e18a20d4f19bc77a83d91c102",
            isTokenGated = false,
            requiredToken = "None",
            totem = Totem.KRAKEN.name
        ),
        Web3Repo(
            id = "repo_phoenix_zk",
            name = "phoenix-zk/regeneration-verifier",
            description = "Zero-knowledge cryptographic snark verifier for privacy-preserving code commits.",
            owner = "phoenix-core",
            stars = 890,
            forks = 145,
            contractType = "ZK Verifier",
            chain = "Base",
            smartContractCode = """// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

contract PhoenixZKVerifier {
    mapping(bytes32 => bool) public nullifiers;

    function verifyProof(
        uint256[2] memory a,
        uint256[2][2] memory b,
        uint256[2] memory c,
        uint256[2] memory input
    ) public view returns (bool) {
        // Pairing check implementation
        return input[0] != 0 && input[1] != 0;
    }
}""",
            abiFunctions = "verifyProof,nullifiers",
            commitHash = "0x77c29ba14092dd9a5f7823e01b",
            isTokenGated = true,
            requiredToken = "PHOENIX-PASS",
            totem = Totem.PHOENIX.name
        ),
        Web3Repo(
            id = "repo_unicorn_oracle",
            name = "unicorn-signals/purity-oracle",
            description = "Decentralized signal purity oracle transmitting sensory telemetry to Layer-1.",
            owner = "unicorn-dao",
            stars = 640,
            forks = 98,
            contractType = "Chainlink Oracle",
            chain = "Ethereum",
            smartContractCode = """// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

contract UnicornSignalOracle {
    uint256 public latestSignal;
    uint256 public lastUpdated;

    event SignalEmitted(uint256 signal, uint256 timestamp);

    function updateSignal(uint256 _sig) external {
        latestSignal = _sig;
        lastUpdated = block.timestamp;
        emit SignalEmitted(_sig, block.timestamp);
    }
}""",
            abiFunctions = "updateSignal,latestSignal,lastUpdated",
            commitHash = "0x55aa3190cbef982a17cd4401e",
            isTokenGated = false,
            requiredToken = "None",
            totem = Totem.UNICORN.name
        ),
        Web3Repo(
            id = "repo_mermaid_bridge",
            name = "mermaid-subsea/amphibious-bridge",
            description = "Cross-chain amphibious interface bridging EVM smart contracts with Solana VM.",
            owner = "mermaid-labs",
            stars = 975,
            forks = 210,
            contractType = "CrossChain Bridge",
            chain = "Solana / EVM",
            smartContractCode = """// Cross-chain Amphibious Bridge Anchor Program
use anchor_lang::prelude::*;

#[program]
pub mod amphibious_bridge {
    use super::*;
    pub fn relay_packet(ctx: Context<RelayPacket>, evm_tx_hash: [u8; 32], payload: Vec<u8>) -> Result<()> {
        msg!("Amphibious relay verified for EVM tx");
        Ok(())
    }
}""",
            abiFunctions = "relay_packet,verify_signature",
            commitHash = "0x11ff8899ac42109df55e810a9",
            isTokenGated = false,
            requiredToken = "None",
            totem = Totem.MERMAID.name
        )
    )

    val initialAssets = listOf(
        RealioAsset(
            id = "asset_rgb_pulse_badge",
            title = "DV1 Master Badge RGB Pulse",
            category = "Brand Badge",
            totem = Totem.DRAGON.name,
            frequency = 3.0f,
            speed = 1.2f,
            rgbColor = "#FF1E3C",
            isRealTime = true,
            description = "Master brand badge with 3-phase RGB pulse glow shader (Red 0% -> Green 33% -> Cyan 66%).",
            codeSnippet = "drop-shadow(0 0 12px #00E5FF) with 3s RGB pulse cycling"
        ),
        RealioAsset(
            id = "asset_harmonic_wave",
            title = "4Real Harmonic Oscillator",
            category = "Waveform Oscillator",
            totem = Totem.KRAKEN.name,
            frequency = 440.0f,
            speed = 2.0f,
            rgbColor = "#00E5FF",
            isRealTime = true,
            description = "Real-time sine & cosine harmonic wave oscillator with touch-reactive amplitude modulation.",
            codeSnippet = "Canvas sin(t * freq) + cos(2 * t * freq) with cyan trail"
        ),
        RealioAsset(
            id = "asset_particle_mesh",
            title = "Quantum Particle Neural Mesh",
            category = "Particle Mesh",
            totem = Totem.PHOENIX.name,
            frequency = 60.0f,
            speed = 1.5f,
            rgbColor = "#00FF9D",
            isRealTime = true,
            description = "Interactive 2D cybernetic particle grid connecting nodes with distance-based neon green glow.",
            codeSnippet = "Dynamic particle repulsion & spring physics matrix"
        ),
        RealioAsset(
            id = "asset_signal_spectrogram",
            title = "Purity Signal Spectrogram",
            category = "Audio Matrix",
            totem = Totem.UNICORN.name,
            frequency = 880.0f,
            speed = 2.5f,
            rgbColor = "#9D4EDD",
            isRealTime = true,
            description = "Multi-band audio-frequency visualizer stem reacting to simulated smart contract state transitions.",
            codeSnippet = "Fast Fourier Transform visual stem with purple laser bloom"
        ),
        RealioAsset(
            id = "asset_amphibious_fluid",
            title = "Subsea Fluid Interface",
            category = "Holographic Shader",
            totem = Totem.MERMAID.name,
            frequency = 120.0f,
            speed = 0.8f,
            rgbColor = "#06D6A0",
            isRealTime = true,
            description = "Dynamic holographic fluid surface simulator with touch ripples and aquatic light caustics.",
            codeSnippet = "Real-time Perlin noise caustic displacement shader"
        )
    )

    val initialCapsules = listOf(
        FusedCapsule(
            id = "cap_rgb_muse_genesis",
            title = "MUSE Genesis Private DApp",
            repoId = "repo_muse_vault",
            repoName = "dextervann1/rgb-muse-vault",
            assetId = "asset_rgb_pulse_badge",
            assetName = "DV1 Master Badge RGB Pulse",
            totem = Totem.DRAGON.name,
            fusionHash = "0xMUSE_FUSED_9A7B3E",
            chain = "Ethereum",
            smartContractSnippet = "RGBMuseVault.verifySensoryCapsule(0x9a7b...)",
            shaderConfig = "Mode: RGB 3-Phase Glow | Speed: 1.2x | DropShadow: 12px",
            gasEstimateGwei = 24,
            isStarred = true
        ),
        FusedCapsule(
            id = "cap_kraken_liquidity",
            title = "Kraken Depth AMM Visualizer",
            repoId = "repo_4real_dex",
            repoName = "4real-protocol/realtime-amm",
            assetId = "asset_harmonic_wave",
            assetName = "4Real Harmonic Oscillator",
            totem = Totem.KRAKEN.name,
            fusionHash = "0xKRAKEN_FUSED_3C8801",
            chain = "Arbitrum",
            smartContractSnippet = "RealtimeAMM.swap(amountIn, minOut, 440Hz)",
            shaderConfig = "Waveform: Dual Harmonic Cyan | Reactivity: High",
            gasEstimateGwei = 12,
            isStarred = true
        ),
        FusedCapsule(
            id = "cap_phoenix_mesh",
            title = "Phoenix ZK Proof Neural Field",
            repoId = "repo_phoenix_zk",
            repoName = "phoenix-zk/regeneration-verifier",
            assetId = "asset_particle_mesh",
            assetName = "Quantum Particle Neural Mesh",
            totem = Totem.PHOENIX.name,
            fusionHash = "0xPHOENIX_FUSED_0E774A",
            chain = "Base",
            smartContractSnippet = "PhoenixZKVerifier.verifyProof(a, b, c, input)",
            shaderConfig = "Particles: 48 Nodes | Green Phosphor Matrix",
            gasEstimateGwei = 8,
            isStarred = false
        )
    )
}
