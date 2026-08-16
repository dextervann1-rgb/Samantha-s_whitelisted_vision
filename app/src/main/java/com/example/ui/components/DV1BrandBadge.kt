package com.example.ui.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SensoryAudioEngine
import com.example.ui.theme.*

enum class BadgeVariant {
    TRANSPARENT,
    BLACK,
    WHITE,
    NEON_BURST
}

@Composable
fun DV1BrandBadge(
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    variant: BadgeVariant = BadgeVariant.TRANSPARENT,
    showLabel: Boolean = true,
    interactive: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rgbPulse")
    
    // Cycle between Red (#FF1E3C), Green (#00FF9D), and Cyan (#00E5FF)
    val glowColor by infiniteTransition.animateColor(
        initialValue = MuseRed,
        targetValue = MuseCyan,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 3000
                MuseRed at 0
                MuseGreen at 1000
                MuseCyan at 2000
                MuseRed at 3000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseColor"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "badgeRotation"
    )

    var clickTrigger by remember { mutableStateOf(false) }
    val tapScale by animateFloatAsState(
        targetValue = if (clickTrigger) 1.15f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "tapScale",
        finishedListener = { clickTrigger = false }
    )

    val containerBg = when (variant) {
        BadgeVariant.TRANSPARENT -> Color.Transparent
        BadgeVariant.BLACK -> Color(0xFF060910)
        BadgeVariant.WHITE -> Color(0xFFF8FAFC)
        BadgeVariant.NEON_BURST -> Color(0xFF0F172A)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .testTag("dv1_brand_badge")
            .clickable(enabled = interactive) {
                clickTrigger = true
                SensoryAudioEngine.playTone(660f, 100, 0.5f)
                onClick?.invoke()
            }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(size)
                .scale(pulseScale * tapScale)
        ) {
            // Ambient outer RGB glow blur
            Box(
                modifier = Modifier
                    .size(size * 0.85f)
                    .clip(CircleShape)
                    .background(glowColor.copy(alpha = 0.45f))
                    .blur(20.dp)
            )

            // Outer Rotating Cyber Ring
            Canvas(modifier = Modifier.size(size).rotate(rotationAngle)) {
                val centerOffset = Offset(this.size.width / 2, this.size.height / 2)
                val radius = this.size.minDimension / 2 - 4.dp.toPx()
                
                drawCircle(
                    brush = Brush.sweepGradient(
                        listOf(MuseRed, MuseGreen, MuseCyan, MusePurple, MuseRed)
                    ),
                    radius = radius,
                    center = centerOffset,
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // Inner Badge Body
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(size * 0.78f)
                    .clip(RoundedCornerShape(size * 0.22f))
                    .background(containerBg)
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(listOf(glowColor, MuseCyan)),
                        shape = RoundedCornerShape(size * 0.22f)
                    )
            ) {
                // Cyber geometric motif (DV1 initials & Totem icon)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "DV",
                            color = if (variant == BadgeVariant.WHITE) Color(0xFF0F172A) else Color.White,
                            fontSize = (size.value * 0.22f).sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "1",
                            color = glowColor,
                            fontSize = (size.value * 0.24f).sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .width(size * 0.4f)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(MuseRed, MuseGreen, MuseCyan)
                                )
                            )
                    )

                    Text(
                        text = "MUSE",
                        color = glowColor,
                        fontSize = (size.value * 0.11f).sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        if (showLabel) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "dextervann1-rgb",
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "MUSE PRIVATE",
                color = glowColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}
