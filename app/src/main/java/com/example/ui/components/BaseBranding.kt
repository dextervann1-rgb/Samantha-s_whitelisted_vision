package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SensoryAudioEngine
import com.example.ui.theme.*

val BaseBlue = Color(0xFF0052FF)
val BaseDarkBlue = Color(0xFF0038B8)

/**
 * High-fidelity Base L2 Network Logo (Official Base Blue #0052FF)
 */
@Composable
fun BaseLogo(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp
) {
    Canvas(
        modifier = modifier
            .size(size)
            .testTag("base_logo")
    ) {
        val radius = this.size.minDimension / 2f
        val center = Offset(this.size.width / 2f, this.size.height / 2f)

        // Base Blue outer circle
        drawCircle(
            color = BaseBlue,
            radius = radius,
            center = center
        )

        // Stylized Base inner geometry (White circle arc cutout / badge)
        val innerRadius = radius * 0.48f
        drawCircle(
            color = Color.White,
            radius = innerRadius,
            center = Offset(center.x + radius * 0.18f, center.y)
        )
    }
}

/**
 * Clickable Basescan.org Explorer Link Chip
 */
@Composable
fun BasescanLinkChip(
    modifier: Modifier = Modifier,
    url: String = "https://basescan.org",
    label: String = "basescan.org"
) {
    val uriHandler = LocalUriHandler.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .testTag("basescan_link_chip")
            .clip(RoundedCornerShape(8.dp))
            .background(BaseBlue.copy(alpha = 0.15f))
            .border(1.dp, BaseBlue.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .clickable {
                SensoryAudioEngine.playTone(880f, 60, 0.4f)
                try {
                    uriHandler.openUri(url)
                } catch (e: Exception) {
                    // Fallback
                }
            }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        BaseLogo(size = 14.dp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            color = Color(0xFF388BFD),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = Icons.Default.OpenInNew,
            contentDescription = "Open Basescan",
            tint = Color(0xFF388BFD),
            modifier = Modifier.size(10.dp)
        )
    }
}

/**
 * Clickable GitHub Explorer Link Chip
 */
@Composable
fun GitHubLinkChip(
    modifier: Modifier = Modifier,
    url: String = "https://github.com/dextervann1",
    label: String = "github.com/dextervann1"
) {
    val uriHandler = LocalUriHandler.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .testTag("github_link_chip")
            .clip(RoundedCornerShape(8.dp))
            .background(MuseDarkSurfaceElevated)
            .border(1.dp, MuseBorder, RoundedCornerShape(8.dp))
            .clickable {
                SensoryAudioEngine.playTone(660f, 60, 0.4f)
                try {
                    uriHandler.openUri(url)
                } catch (e: Exception) {
                    // Fallback
                }
            }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Code,
            contentDescription = "GitHub",
            tint = Color.White,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            color = TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = Icons.Default.OpenInNew,
            contentDescription = "Open GitHub",
            tint = TextMuted,
            modifier = Modifier.size(10.dp)
        )
    }
}

/**
 * Top Quick Link Bar providing direct links to Base / Basescan.org and GitHub
 */
@Composable
fun QuickLinkHeaderBar(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasescanLinkChip(
            url = "https://basescan.org",
            label = "basescan.org (Base L2)"
        )
        GitHubLinkChip(
            url = "https://github.com/dextervann1",
            label = "github.com/dextervann1"
        )
    }
}
