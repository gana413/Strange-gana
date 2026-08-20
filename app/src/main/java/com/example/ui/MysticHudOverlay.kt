package com.example.ui

import androidx.camera.core.CameraSelector
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MotionPhotosOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ar.ShieldType
import com.example.camera.MotionAnalysisResult
import com.example.ui.theme.CrimsonBand
import com.example.ui.theme.MirrorCyan
import com.example.ui.theme.MysticBorder
import com.example.ui.theme.MysticDarkBg
import com.example.ui.theme.MysticGold
import com.example.ui.theme.MysticOrange
import com.example.ui.theme.MysticSurface
import com.example.ui.theme.TimeEmerald

@Composable
fun MysticHudOverlay(
    lensFacing: Int,
    isTorchEnabled: Boolean,
    hasFlash: Boolean,
    isListening: Boolean,
    isSpeechAvailable: Boolean,
    lastHeardVoiceText: String,
    motionResult: MotionAnalysisResult,
    selectedShieldType: ShieldType,
    isPortalActive: Boolean,
    isWhipActive: Boolean,
    isSoundEnabled: Boolean,
    activeShieldCount: Int,
    onSwitchCamera: () -> Unit,
    onToggleTorch: () -> Unit,
    onToggleVoice: () -> Unit,
    onToggleSound: () -> Unit,
    onSelectShield: (ShieldType) -> Unit,
    onTogglePortal: () -> Unit,
    onToggleWhips: () -> Unit,
    onSummonDualShields: () -> Unit,
    onClearSpells: () -> Unit,
    onOpenSpellbook: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hud_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(14.dp)
    ) {
        // TOP HUD BAR: Status & Info
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth(0.72f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MysticSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MysticBorder),
                shadowElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (motionResult.isSuddenMotionDetected) TimeEmerald else MysticGold)
                        )
                        Text(
                            text = "MYSTIC ARTS AR V6.1",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MysticGold,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Camera lens indicator
                        Text(
                            text = if (lensFacing == CameraSelector.LENS_FACING_FRONT) "Front (Selfie AR)" else "Back (World AR)",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1)
                        )
                        // Active spell counter
                        if (activeShieldCount > 0 || isPortalActive || isWhipActive) {
                            Text(
                                text = "• Active: $activeShieldCount Spell(s)",
                                fontSize = 11.sp,
                                color = TimeEmerald,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Voice Command Live Feedback Banner
            AnimatedVisibility(
                visible = isListening || lastHeardVoiceText.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xDD0D1B2A),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isListening) TimeEmerald else MysticGold)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isListening) TimeEmerald else MysticGold)
                                .scale(if (isListening) pulseScale else 1f)
                        )
                        Text(
                            text = if (isListening) {
                                if (lastHeardVoiceText.isNotEmpty()) "Heard: \"$lastHeardVoiceText\"" else "Listening for spells..."
                            } else {
                                "Cast: \"$lastHeardVoiceText\""
                            },
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // TOP RIGHT ACTION BUTTONS: Lens Flip, Torch, Sound, Spellbook
        Column(
            modifier = Modifier.align(Alignment.TopEnd),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Camera Lens Switch Button
            HudIconButton(
                icon = Icons.Default.Cameraswitch,
                contentDescription = "Switch Front/Back Camera",
                onClick = onSwitchCamera,
                tint = MysticGold,
                testTag = "switch_camera_button"
            )

            // Torch / Flashlight Toggle
            if (hasFlash) {
                HudIconButton(
                    icon = if (isTorchEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                    contentDescription = "Toggle Torch",
                    onClick = onToggleTorch,
                    tint = if (isTorchEnabled) MysticGold else Color.Gray,
                    active = isTorchEnabled,
                    testTag = "toggle_torch_button"
                )
            }

            // Voice Mic Toggle
            if (isSpeechAvailable) {
                HudIconButton(
                    icon = if (isListening) Icons.Default.Mic else Icons.Default.MicOff,
                    contentDescription = "Voice Incantation",
                    onClick = onToggleVoice,
                    tint = if (isListening) TimeEmerald else Color.LightGray,
                    active = isListening,
                    testTag = "voice_mic_button"
                )
            }

            // Sound Effects Toggle
            HudIconButton(
                icon = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                contentDescription = "Toggle Magic Sounds",
                onClick = onToggleSound,
                tint = if (isSoundEnabled) Color(0xFF00E5FF) else Color.Gray,
                testTag = "toggle_sound_button"
            )

            // Clear Spells
            HudIconButton(
                icon = Icons.Default.Refresh,
                contentDescription = "Clear All Spells",
                onClick = onClearSpells,
                tint = Color(0xFFFF5555),
                testTag = "clear_spells_button"
            )

            // Spellbook / Help
            HudIconButton(
                icon = Icons.Default.HelpOutline,
                contentDescription = "Mystic Arts Spellbook Guide",
                onClick = onOpenSpellbook,
                tint = Color(0xFFE2E8F0),
                testTag = "spellbook_button"
            )
        }

        // BOTTOM SPELL SELECTION CAROUSEL & QUICK ACTIONS
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Quick action pills (Dual Cast & Portal Mode)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuickActionChip(
                    text = "Summon Dual Shields",
                    icon = Icons.Default.Shield,
                    color = MysticOrange,
                    onClick = onSummonDualShields
                )
                QuickActionChip(
                    text = if (isPortalActive) "Close Portal" else "Multiverse Portal",
                    icon = Icons.Default.AutoAwesome,
                    color = Color(0xFF9D4EDD),
                    isActive = isPortalActive,
                    onClick = onTogglePortal
                )
                QuickActionChip(
                    text = if (isWhipActive) "Release Whips" else "5 Whips (తాళ్ళు)",
                    icon = Icons.Default.WifiTethering,
                    color = Color(0xFFFF5500),
                    isActive = isWhipActive,
                    onClick = onToggleWhips
                )
            }

            // Spell Selector Deck
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MysticSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MysticBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ShieldCardItem(
                        type = ShieldType.ORANGE,
                        isSelected = selectedShieldType == ShieldType.ORANGE && !isPortalActive && !isWhipActive,
                        onClick = { onSelectShield(ShieldType.ORANGE) }
                    )
                    ShieldCardItem(
                        type = ShieldType.TIME,
                        isSelected = selectedShieldType == ShieldType.TIME && !isPortalActive && !isWhipActive,
                        onClick = { onSelectShield(ShieldType.TIME) }
                    )
                    ShieldCardItem(
                        type = ShieldType.MIRROR,
                        isSelected = selectedShieldType == ShieldType.MIRROR && !isPortalActive && !isWhipActive,
                        onClick = { onSelectShield(ShieldType.MIRROR) }
                    )
                    ShieldCardItem(
                        type = ShieldType.CRIMSON,
                        isSelected = selectedShieldType == ShieldType.CRIMSON && !isPortalActive && !isWhipActive,
                        onClick = { onSelectShield(ShieldType.CRIMSON) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HudIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    tint: Color,
    active: Boolean = false,
    testTag: String
) {
    val bgColor = if (active) tint.copy(alpha = 0.25f) else MysticSurface
    val borderColor = if (active) tint else MysticBorder

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(bgColor)
            .border(1.dp, borderColor, CircleShape)
            .clickable(onClick = onClick)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun QuickActionChip(
    text: String,
    icon: ImageVector,
    color: Color,
    isActive: Boolean = false,
    onClick: () -> Unit
) {
    val bg = if (isActive) color.copy(alpha = 0.35f) else MysticSurface
    val border = if (isActive) color else MysticBorder

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bg,
        border = androidx.compose.foundation.BorderStroke(1.dp, border),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun ShieldCardItem(
    type: ShieldType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isSelected) type.primaryColor.copy(alpha = 0.25f) else Color(0x441F1138)
    val border = if (isSelected) type.primaryColor else Color(0x33FFFFFF)

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bg,
        border = androidx.compose.foundation.BorderStroke(if (isSelected) 1.5.dp else 1.dp, border),
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag("shield_card_${type.name.lowercase()}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(type.primaryColor)
            )
            Column {
                Text(
                    text = type.title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) type.accentColor else Color.White
                )
                Text(
                    text = type.description.take(24) + "...",
                    fontSize = 9.sp,
                    color = Color(0xFFA0AEC0)
                )
            }
        }
    }
}
