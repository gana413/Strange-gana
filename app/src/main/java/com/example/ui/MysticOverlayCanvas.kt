package com.example.ui

import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import com.example.ar.ActiveShield
import com.example.ar.DualHandWhipState
import com.example.ar.EnergyWhipRenderer
import com.example.ar.MysticAudioSynthesizer
import com.example.ar.ParticleEngine
import com.example.ar.PortalRenderer
import com.example.ar.PortalState
import com.example.ar.ShieldRenderer
import com.example.ar.ShieldType
import kotlin.math.atan2
import kotlin.math.hypot

@Composable
fun MysticOverlayCanvas(
    activeShields: MutableList<ActiveShield>,
    portalState: PortalState,
    whipState: DualHandWhipState,
    selectedShieldType: ShieldType,
    isPortalMode: Boolean,
    isWhipMode: Boolean,
    isAutoOrbitEnabled: Boolean,
    audioSynthesizer: MysticAudioSynthesizer,
    particleEngine: ParticleEngine,
    onPortalUpdate: (PortalState) -> Unit,
    modifier: Modifier = Modifier
) {
    var animTimeSec by remember { mutableFloatStateOf(0f) }
    val startTime = remember { System.currentTimeMillis() }

    // 60FPS continuous animation driver
    LaunchedEffect(Unit) {
        while (true) {
            withInfiniteAnimationFrameMillis { frameTime ->
                animTimeSec = (System.currentTimeMillis() - startTime) / 1000f
            }
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(selectedShieldType, isPortalMode, isWhipMode) {
                detectTapGestures(
                    onTap = { tapOffset ->
                        if (isPortalMode) {
                            onPortalUpdate(
                                portalState.copy(
                                    isOpen = true,
                                    center = tapOffset,
                                    radius = 180f,
                                    targetRadius = 180f
                                )
                            )
                            particleEngine.emitBurst(tapOffset, count = 25, color = selectedShieldType.accentColor)
                            audioSynthesizer.playPortalOpenSound()
                        } else if (isWhipMode) {
                            // Whip anchor tap
                            audioSynthesizer.playWhipCrackSound()
                        } else {
                            // Cast shield at tap
                            val newShield = ActiveShield(
                                id = "shield_${System.currentTimeMillis()}",
                                type = selectedShieldType,
                                center = tapOffset,
                                radius = 220f
                            )
                            activeShields.add(newShield)
                            particleEngine.emitBurst(tapOffset, count = 30, color = selectedShieldType.primaryColor)
                            audioSynthesizer.playSpellCastSound(selectedShieldType)
                        }
                    }
                )
            }
            .pointerInput(selectedShieldType, isPortalMode) {
                detectDragGestures(
                    onDragStart = { startOffset ->
                        if (isPortalMode) {
                            onPortalUpdate(
                                portalState.copy(
                                    isOpen = true,
                                    center = startOffset,
                                    radius = 80f,
                                    targetRadius = 220f
                                )
                            )
                            audioSynthesizer.playPortalOpenSound()
                        } else {
                            val newShield = ActiveShield(
                                id = "drag_shield_${System.currentTimeMillis()}",
                                type = selectedShieldType,
                                center = startOffset,
                                radius = 200f
                            )
                            activeShields.add(newShield)
                            audioSynthesizer.playSpellCastSound(selectedShieldType)
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val currentPos = change.position
                        if (isPortalMode && portalState.isOpen) {
                            val dist = hypot(currentPos.x - portalState.center.x, currentPos.y - portalState.center.y)
                            val newRadius = dist.coerceIn(80f, 380f)
                            onPortalUpdate(
                                portalState.copy(
                                    radius = newRadius,
                                    targetRadius = newRadius,
                                    rotation = portalState.rotation + 0.05f
                                )
                            )
                            particleEngine.emitPortalSparks(portalState.center, newRadius, count = 4)
                        } else if (activeShields.isNotEmpty()) {
                            val lastIdx = activeShields.lastIndex
                            val updated = activeShields[lastIdx].copy(center = currentPos)
                            activeShields[lastIdx] = updated
                            particleEngine.emitShieldSparks(currentPos, updated.radius, count = 2)
                        }
                    }
                )
            }
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // 1. Draw Multiverse Cosmic Portal if active
        if (portalState.isOpen && portalState.radius > 5f) {
            PortalRenderer.renderCosmicPortal(
                drawScope = this,
                center = if (portalState.center == Offset.Zero) Offset(canvasWidth / 2f, canvasHeight / 2f) else portalState.center,
                radius = portalState.radius,
                animTimeSec = animTimeSec
            )
        }

        // 2. Draw 5-Finger Energy Mystic Whips if active
        if (whipState.isActive) {
            EnergyWhipRenderer.renderEnergyWhips(
                drawScope = this,
                p1 = whipState.hand1Pos,
                p2 = whipState.hand2Pos,
                animTimeSec = animTimeSec,
                intensity = whipState.energyIntensity
            )
        }

        // 3. Draw Active Tao Mandala Shields
        val now = System.currentTimeMillis()
        val shieldIterator = activeShields.iterator()
        while (shieldIterator.hasNext()) {
            val shield = shieldIterator.next()
            val age = now - shield.birthTime
            if (age > shield.durationMs) {
                shieldIterator.remove()
                continue
            }

            val alpha = if (age > shield.durationMs - 1500) {
                ((shield.durationMs - age) / 1500f).coerceIn(0f, 1f)
            } else {
                (age / 300f).coerceIn(0f, 1f)
            }

            val centerPos = if (isAutoOrbitEnabled) {
                // Gentle celestial hovering motion
                Offset(
                    x = shield.center.x + kotlin.math.sin(animTimeSec * 1.5f) * 25f,
                    y = shield.center.y + kotlin.math.cos(animTimeSec * 1.2f) * 18f
                )
            } else {
                shield.center
            }

            ShieldRenderer.drawEldritchShield(
                drawScope = this,
                center = centerPos,
                radius = shield.radius,
                shieldType = shield.type,
                animTimeSec = animTimeSec,
                alpha = alpha
            )

            // Emit ambient shield sparks periodically
            if (alpha > 0.3f) {
                particleEngine.emitShieldSparks(centerPos, shield.radius, count = 1, color = shield.type.accentColor)
            }
        }

        // 4. Update and Draw Dynamic Sparks & Embers
        particleEngine.updateAndDraw(
            drawScope = this,
            portalCenter = if (portalState.isOpen) portalState.center else null,
            portalRadius = if (portalState.isOpen) portalState.radius else 0f
        )
    }
}
