package com.example.ar

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

class MysticAudioSynthesizer(private val context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private val sampleRate = 22050
    private var isAudioEnabled = true
    private var activeJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    fun setAudioEnabled(enabled: Boolean) {
        isAudioEnabled = enabled
    }

    fun playSpellCastSound(type: ShieldType) {
        triggerHaptic(60)
        if (!isAudioEnabled) return

        scope.launch {
            val baseFreq = when (type) {
                ShieldType.ORANGE -> 220.0 // A3
                ShieldType.TIME -> 330.0   // E4
                ShieldType.MIRROR -> 440.0 // A4
                ShieldType.CRIMSON -> 165.0 // E3
            }
            playToneSweep(baseFreq, baseFreq * 2.2, durationMs = 380)
        }
    }

    fun playPortalOpenSound() {
        triggerHaptic(120)
        if (!isAudioEnabled) return

        scope.launch {
            playToneSweep(110.0, 480.0, durationMs = 550)
        }
    }

    fun playWhipCrackSound() {
        triggerHaptic(40)
        if (!isAudioEnabled) return

        scope.launch {
            playNoiseBurst(durationMs = 120)
        }
    }

    private fun triggerHaptic(durationMs: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {
        }
    }

    private fun playToneSweep(startFreq: Double, endFreq: Double, durationMs: Int) {
        try {
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val samples = ShortArray(numSamples)

            var phase = 0.0
            for (i in 0 until numSamples) {
                val progress = i.toDouble() / numSamples
                val currentFreq = startFreq + (endFreq - startFreq) * progress
                val envelope = (1.0 - progress) * sin(progress * Math.PI) // Fade in/out
                val value = sin(phase) * envelope * 0.4
                val sparkNoise = (Random.nextDouble() - 0.5) * 0.12 * envelope // subtle spark crackle
                samples[i] = ((value + sparkNoise).coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()

                phase += 2.0 * Math.PI * currentFreq / sampleRate
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(samples.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(samples, 0, samples.size)
            audioTrack.play()

            Thread.sleep(durationMs.toLong() + 50)
            audioTrack.release()
        } catch (_: Exception) {
        }
    }

    private fun playNoiseBurst(durationMs: Int) {
        try {
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val samples = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val progress = i.toDouble() / numSamples
                val envelope = (1.0 - progress) * (1.0 - progress)
                val noise = (Random.nextDouble() * 2.0 - 1.0) * envelope * 0.35
                samples[i] = (noise * Short.MAX_VALUE).toInt().toShort()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(samples.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(samples, 0, samples.size)
            audioTrack.play()

            Thread.sleep(durationMs.toLong() + 50)
            audioTrack.release()
        } catch (_: Exception) {
        }
    }

    fun release() {
        activeJob?.cancel()
    }
}
