package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SensoryAudioEngine
import com.example.ui.theme.*
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun WaveformOscillatorCanvas(
    modifier: Modifier = Modifier,
    baseFrequency: Float = 440f,
    baseColor: Color = MuseCyan,
    speedMultiplier: Float = 1.0f,
    interactive: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveformAnimation")
    val timePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.283185f,
        animationSpec = infiniteRepeatable(
            animation = tween((2000 / speedMultiplier).toInt().coerceAtLeast(400), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "timePhase"
    )

    var touchModX by remember { mutableFloatStateOf(1.0f) }
    var touchModY by remember { mutableFloatStateOf(1.0f) }
    var lastAudioPlayTime by remember { mutableLongStateOf(0L) }

    val path1 = remember { Path() }
    val path2 = remember { Path() }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MuseDarkSurface)
            .border(1.dp, MuseBorder, RoundedCornerShape(12.dp))
            .pointerInput(interactive) {
                if (interactive) {
                    detectDragGestures { change, _ ->
                        touchModX = (change.position.x / size.width).coerceIn(0.2f, 3.0f)
                        touchModY = (change.position.y / size.height).coerceIn(0.2f, 2.5f)
                        val now = System.currentTimeMillis()
                        if (now - lastAudioPlayTime > 80) {
                            lastAudioPlayTime = now
                            SensoryAudioEngine.playTone((baseFrequency * touchModX).coerceIn(120f, 1200f), 60, 0.3f)
                        }
                    }
                }
            }
            .pointerInput(interactive) {
                if (interactive) {
                    detectTapGestures { offset ->
                        touchModX = (offset.x / size.width).coerceIn(0.2f, 3.0f)
                        touchModY = (offset.y / size.height).coerceIn(0.2f, 2.5f)
                        SensoryAudioEngine.playTone((baseFrequency * touchModX).coerceIn(120f, 1200f), 120, 0.4f)
                    }
                }
            }
            .testTag("waveform_oscillator_canvas")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val centerY = height / 2

            // Background Grid Lines
            val gridStep = 24.dp.toPx()
            var gx = 0f
            while (gx < width) {
                drawLine(
                    color = Color(0x1500E5FF),
                    start = Offset(gx, 0f),
                    end = Offset(gx, height),
                    strokeWidth = 1f
                )
                gx += gridStep
            }
            var gy = 0f
            while (gy < height) {
                drawLine(
                    color = Color(0x1500FF9D),
                    start = Offset(0f, gy),
                    end = Offset(width, gy),
                    strokeWidth = 1f
                )
                gy += gridStep
            }

            // Primary Waveform - Reset cached paths
            path1.reset()
            path2.reset()
            val pointsCount = 100
            val dx = width / pointsCount

            for (i in 0..pointsCount) {
                val x = i * dx
                val normalizedX = (x / width) * 4 * Math.PI.toFloat() * touchModX
                
                // Composite Harmonic Formula: sin(x + t) + 0.35 * sin(2x - t)
                val y1 = centerY + (sin(normalizedX + timePhase) * (height * 0.28f * touchModY) +
                        sin(normalizedX * 2.2f - timePhase) * (height * 0.12f)).toFloat()

                val y2 = centerY + (sin(normalizedX * 1.5f + timePhase * 1.3f) * (height * 0.22f) -
                        sin(normalizedX * 0.5f - timePhase) * (height * 0.08f)).toFloat()

                if (i == 0) {
                    path1.moveTo(x, y1)
                    path2.moveTo(x, y2)
                } else {
                    path1.lineTo(x, y1)
                    path2.lineTo(x, y2)
                }
            }

            // Draw harmonic glow wave (secondary)
            drawPath(
                path = path2,
                color = MusePurple.copy(alpha = 0.6f),
                style = Stroke(width = 2.dp.toPx())
            )

            // Draw primary sharp waveform
            drawPath(
                path = path1,
                brush = Brush.horizontalGradient(
                    listOf(MuseRed, MuseGreen, baseColor, MuseCyan)
                ),
                style = Stroke(width = 3.dp.toPx())
            )
        }

        // Live telemetry overlay
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
                .background(Color(0x88000000), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "4REAL STEM: ${(baseFrequency * touchModX).toInt()}Hz | MOD: ${"%.2f".format(touchModY)}x",
                color = MuseCyan,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp
            )
        }
    }
}

private data class MeshParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: Color
)

@Composable
fun ParticleMeshCanvas(
    modifier: Modifier = Modifier,
    particleCount: Int = 30,
    accentColor: Color = MuseGreen
) {
    val infiniteTransition = rememberInfiniteTransition(label = "particleClock")
    val clockTick by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "clockTick"
    )

    val particles = remember {
        val colors = listOf(MuseRed, MuseGreen, MuseCyan, MusePurple)
        List(particleCount) {
            MeshParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                vx = (Random.nextFloat() - 0.5f) * 0.006f,
                vy = (Random.nextFloat() - 0.5f) * 0.006f,
                color = colors.random()
            )
        }
    }

    var rippleCenter by remember { mutableStateOf<Offset?>(null) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MuseDarkBg)
            .border(1.dp, MuseBorder, RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                detectTapGestures { tapPos ->
                    rippleCenter = tapPos
                    SensoryAudioEngine.playTone(520f, 90, 0.4f)
                }
            }
            .testTag("particle_mesh_canvas")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val maxDistance = width * 0.32f

            // Update particle positions
            particles.forEach { p ->
                p.x = (p.x + p.vx).let { if (it < 0f) 1f else if (it > 1f) 0f else it }
                p.y = (p.y + p.vy).let { if (it < 0f) 1f else if (it > 1f) 0f else it }
            }

            // Draw connection lines
            for (i in particles.indices) {
                for (j in i + 1 until particles.size) {
                    val p1 = particles[i]
                    val p2 = particles[j]
                    val x1 = p1.x * width
                    val y1 = p1.y * height
                    val x2 = p2.x * width
                    val y2 = p2.y * height

                    val dist = kotlin.math.hypot((x2 - x1), (y2 - y1))
                    if (dist < maxDistance) {
                        val alpha = ((1f - (dist / maxDistance)) * 0.6f).coerceIn(0f, 1f)
                        drawLine(
                            color = accentColor.copy(alpha = alpha),
                            start = Offset(x1, y1),
                            end = Offset(x2, y2),
                            strokeWidth = 1.2.dp.toPx()
                        )
                    }
                }
            }

            // Draw particle dots
            particles.forEach { p ->
                val px = p.x * width
                val py = p.y * height
                drawCircle(
                    color = p.color,
                    radius = 3.dp.toPx(),
                    center = Offset(px, py)
                )
            }
        }
    }
}
