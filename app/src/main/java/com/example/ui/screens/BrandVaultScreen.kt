package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.components.BadgeVariant
import com.example.ui.components.CodeSnippetViewer
import com.example.ui.components.DV1BrandBadge
import com.example.ui.components.LegalFooter
import com.example.ui.theme.*

@Composable
fun BrandVaultScreen(
    modifier: Modifier = Modifier
) {
    var selectedBadgeVariant by remember { mutableStateOf(BadgeVariant.TRANSPARENT) }
    var selectedStageIndex by remember { mutableIntStateOf(0) }

    val evolutionStages = listOf(
        Triple("DRACIS // DRAGON", "Compute / Fire — Raw ignition. Turns void into vector.", """// Stage 01 — DRACIS protocol
func ignition(prompt: Myth) -> System {
  let fire = Compute.surge(from: .void)
  let idea = fire.bind(to: intent)
  return idea.compile(dialect: .rgb)
  // observe: what wants to exist?
}"""),
        Triple("PHOENIX // PHOENIX", "Resilience / Deploy — Burns clean. Ships from ash.", """// Stage 02 — PHOENIX loop
loop resilience {
  self.burn(legacy: true)
  let next = self.from(ash: learning)
  deploy(next, mode: .disturb)
  if next.isAlive { continue }
  // disturb: what must be remade?
}"""),
        Triple("KRAKEN // KRAKEN", "Depth / Data — Pressure is a feature. Holds the abyss.", """// Stage 03 — KRAKEN depth
struct Depth {
  func pressure(sys: System) -> Scale {
    return DataOcean
      .pressurize(sys, depth: .abyssal)
      .cache().shard().observe()
  }
  // converge: hold complexity without drowning
}"""),
        Triple("UNICORN // UNICORN", "Signal / Purity — Filters noise. Keeps the signal holy.", """// Stage 04 — UNICORN filter
let pure = Signal.filter { input in
  guard input.isTrue && !input.isNoisy else { return .drop }
  return .keep(security: .verified, clarity: .high)
}
// uplift: what remains when noise is gone?"""),
        Triple("MERUS // MERMAID", "Adaptation / Interface — Amphibious logic. Breathes human & machine.", """// Stage 05 — MERUS bridge
protocol Amphibious {
  func bridge(human: Intent, ai: Model) -> LivingInterface
}

let interface = Merus.bridge(
  human: .embodied, ai: .evolved
)
// converge: dextervann1-rgb = human x AI""")
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("brand_vault_list"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Brand Header
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "DEXTERVANN1-RGB BRAND KIT",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Observe. Disturb. Evolve. | No ceilings.",
                    color = MuseCyan,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                )
            }
        }

        // Live Brand Badge Showcase Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("brand_badge_showcase_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MuseDarkSurfaceElevated),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(MuseRed, MuseGreen, MuseCyan))
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "RGB PULSE MASTER BADGE (TAP TO PULSE)",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // The Glowing Dynamic Composable Badge
                    DV1BrandBadge(
                        size = 110.dp,
                        variant = selectedBadgeVariant,
                        showLabel = true,
                        interactive = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Variant Switcher
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        BadgeVariant.entries.forEach { variant ->
                            val isSelected = selectedBadgeVariant == variant
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    SensoryAudioEngine.playTone(600f, 50, 0.3f)
                                    selectedBadgeVariant = variant
                                },
                                label = { Text(variant.name.lowercase().replace("_", " "), fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MuseCyan.copy(alpha = 0.2f),
                                    selectedLabelColor = MuseCyan,
                                    containerColor = MuseDarkSurface,
                                    labelColor = TextSecondary
                                ),
                                modifier = Modifier.padding(horizontal = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // 5-Stage Myth-Convergence Evolution Matrix
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MuseDarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(MuseBorder, MuseCyan))
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "⚡ 5-STAGE EVOLUTION SCRIPT (MYTH → METHOD)",
                        color = MuseGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Totem.entries.forEachIndexed { idx, totem ->
                            val isSelected = selectedStageIndex == idx
                            val tColor = Color(android.graphics.Color.parseColor(totem.colorHex))
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) tColor.copy(alpha = 0.2f) else Color.Transparent)
                                    .border(1.dp, if (isSelected) tColor else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable {
                                        selectedStageIndex = idx
                                        SensoryAudioEngine.playTone(330f + (idx * 110f), 80, 0.4f)
                                    }
                                    .padding(horizontal = 6.dp, vertical = 6.dp)
                            ) {
                                Text(totem.emoji, fontSize = 18.sp)
                                Text(
                                    text = "0${idx + 1}",
                                    color = if (isSelected) tColor else TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val activeStage = evolutionStages[selectedStageIndex]
                    Text(
                        text = activeStage.first,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = activeStage.second,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    CodeSnippetViewer(
                        code = activeStage.third,
                        title = "Stage 0${selectedStageIndex + 1} Code Implementation",
                        language = "swift"
                    )
                }
            }
        }

        // Founder Bio & Vann Family Ventures LLC Dossier
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("founder_dossier_card"),
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
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("👑", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "FOUNDER & PROTOCOL ARCHITECT",
                                    color = GoldAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = "Dexter Lamar Vann",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "@dextervann1-rgb • dextervann1@gmail.com",
                                color = MuseCyan,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
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
                                text = "L6+ STAFF",
                                color = GoldAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Legal IP Notice
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MuseDarkBg)
                            .border(1.dp, MuseBorder, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = MuseGreen, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "COMMERCIAL IP OWNERSHIP REGISTRY",
                                    color = MuseGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "All intellectual property, algorithms, audio waveforms, totem convergence pipelines, and commercial rights are exclusively owned and held by Vann Family Ventures LLC.",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Bio Text
                    Text(
                        text = "EXECUTIVE BIO",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Dexter Lamar Vann (dextervann1-rgb) is a Web3 Systems Architect, Creative Technologist, and Founder of Vann Family Ventures LLC. Dexter pioneers decentralized intent infrastructure, bridging ERC-7715 delegated permission mechanics, real-time sensory DSP audio synthesis, and multi-model agentic swarms. With a 9.4/10 system architecture rating, his work synthesizes high-velocity smart accounts with tactile human-machine interfaces under the guiding mandate: 'Observe. Disturb. Evolve. No ceilings.'",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Key Stats Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple("VALUATION", "$1.25M - $2.4M", GoldAccent),
                            Triple("SYSTEM RATING", "9.4 / 10", MuseGreen),
                            Triple("YR 1 PROJ SALES", "$573,450", MuseCyan)
                        ).forEach { (label, value, color) ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MuseDarkBg)
                                    .border(1.dp, MuseBorder, RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(label, color = TextMuted, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                                    Text(value, color = color, fontSize = 12.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tech Stack Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple("DEV LEVEL", "L6+ Principal", MusePurple),
                            Triple("RE-CREATE COST", "$70K - $125K", MuseRed),
                            Triple("BUILD TIME", "420 - 580 Hrs", TextSecondary)
                        ).forEach { (label, value, color) ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MuseDarkBg)
                                    .border(1.dp, MuseBorder, RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(label, color = TextMuted, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                                    Text(value, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Integration Code Snippets
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "INTEGRATION SUITE & SECURE SETUP",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 1. Python .env Setup
                CodeSnippetViewer(
                    title = "1. Python .env Secure Setup (manus_secure_init.py)",
                    language = "python",
                    code = """# manus_secure_init.py - Run this in your new private repo
from pathlib import Path
import os

# Create structure
Path("assets/branding").mkdir(parents=True, exist_ok=True)
Path(".env.example").write_text("API_KEY=your_key_here\nMETA_TOKEN=your_meta_token\nDB_URL=your_db_url\n")

# Create .gitignore
gitignore = \"\"\"
.env
.env.*
__pycache__/
*.pyc
.claude/projects/
sessions/
*.log
\"\"\"
Path(".gitignore").write_text(gitignore)
print("✅ Secure structure created. Add .env file locally, never commit it.")"""
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 2. React Badge Component
                CodeSnippetViewer(
                    title = "2. React RGB Pulse Component (DV1Badge.jsx)",
                    language = "jsx",
                    code = """// components/DV1Badge.jsx
export default function DV1Badge({ variant="transparent", size=120 }) {
  const srcs = {
    transparent: "/assets/branding/dextervann1_rgb_muse_private_badge.png",
    black: "/assets/branding/dv1_black.png",
    white: "/assets/branding/dv1_white.png",
    icon: "/assets/branding/dv1_app_icon_transparent.png"
  }
  return (
    <div style={{ 
      display:'inline-flex', 
      filter: 'drop-shadow(0 0 12px #00E5FF)',
      animation: 'rgbPulse 3s infinite'
    }}>
      <img src={srcs[variant]} width={size} alt="dextervann1-rgb MUSE PRIVATE" />
      <style>{`@keyframes rgbPulse { 0%{filter:drop-shadow(0 0 6px #FF1E3C)} 33%{filter:drop-shadow(0 0 12px #00FF9D)} 66%{filter:drop-shadow(0 0 12px #00E5FF)} 100%{filter:drop-shadow(0 0 6px #FF1E3C)} }`}</style>
    </div>
  )
}"""
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 3. FastAPI Static Serving
                CodeSnippetViewer(
                    title = "3. FastAPI Router (routers/branding.py)",
                    language = "python",
                    code = """from fastapi import APIRouter
from fastapi.staticfiles import StaticFiles

router = APIRouter()

@router.get("/brand")
def get_brand():
    return {
        "name": "dextervann1-rgb-muse-private",
        "colors": {"R": "#FF1E3C", "G": "#00FF9D", "B": "#00E5FF"},
        "philosophy": "Observe. Disturb. Evolve. No ceilings."
    }"""
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 4. README.md Badge Header
                CodeSnippetViewer(
                    title = "4. README.md Markdown Header",
                    language = "markdown",
                    code = """<p align="center">
  <img src="./assets/branding/dextervann1_rgb_muse_private_badge.png" width="300" alt="dextervann1-rgb MUSE PRIVATE" />
</p>

<h1 align="center">dextervann1-rgb-muse-private</h1>
<p align="center"><i>Observe. Disturb. Evolve. | No ceilings.</i></p>

- 🔥 Dragon = Ignition
- ♻️ Phoenix = Regeneration  
- 🦑 Kraken = Depth / Scale
- 🦄 Unicorn = Signal / Purity
- 🧜 Mermaid = Amphibious Interface"""
                )
                Spacer(modifier = Modifier.height(12.dp))
                LegalFooter(modifier = Modifier.padding(top = 8.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
