package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

object SensoryAudioEngine {
    private val scope = CoroutineScope(Dispatchers.Default)
    private const val SAMPLE_RATE = 44100

    fun playTone(frequency: Float = 440f, durationMs: Int = 150, volume: Float = 0.6f) {
        scope.launch {
            try {
                val numSamples = (durationMs * SAMPLE_RATE) / 1000
                val buffer = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val time = i.toDouble() / SAMPLE_RATE
                    // Apply envelope to prevent clicking
                    val envelope = when {
                        i < numSamples * 0.1 -> i / (numSamples * 0.1)
                        i > numSamples * 0.7 -> (numSamples - i) / (numSamples * 0.3)
                        else -> 1.0
                    }
                    val sample = (sin(2.0 * Math.PI * frequency * time) * Short.MAX_VALUE * volume * envelope).toInt()
                    buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()

                val audioFormat = AudioFormat.Builder()
                    .setSampleRate(SAMPLE_RATE)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setAudioFormat(audioFormat)
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
                // Let track play out asynchronously without blocking thread
                kotlinx.coroutines.delay(durationMs.toLong() + 30)
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {}
            } catch (_: Exception) {
                // Audio synthesis fallback silently ignored on headless environments
            }
        }
    }

    fun playFusionIgnition() {
        scope.launch {
            // Triple arpeggio chord (Red -> Green -> Cyan: 330Hz -> 554Hz -> 659Hz -> 880Hz)
            playTone(330f, 90, 0.4f)
            kotlinx.coroutines.delay(80)
            playTone(554f, 90, 0.5f)
            kotlinx.coroutines.delay(80)
            playTone(659f, 100, 0.6f)
            kotlinx.coroutines.delay(90)
            playTone(880f, 220, 0.7f)
        }
    }
}
