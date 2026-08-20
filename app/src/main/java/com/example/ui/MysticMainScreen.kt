package com.example.ui

import android.Manifest
import androidx.camera.core.CameraSelector
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ar.ActiveShield
import com.example.ar.DualHandWhipState
import com.example.ar.MysticAudioSynthesizer
import com.example.ar.ParticleEngine
import com.example.ar.PortalState
import com.example.ar.ShieldType
import com.example.camera.CameraManager
import com.example.camera.CameraPreview
import com.example.speech.VoiceIncantationManager
import com.example.speech.VoiceSpellCommand
import com.example.ui.theme.MysticBorder
import com.example.ui.theme.MysticDarkBg
import com.example.ui.theme.MysticGold
import com.example.ui.theme.MysticOrange
import com.example.ui.theme.MysticSurface
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MysticMainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    // Core Engines
    val cameraManager = remember { CameraManager(context) }
    val audioSynthesizer = remember { MysticAudioSynthesizer(context) }
    val particleEngine = remember { ParticleEngine() }

    var previewViewRef by remember { mutableStateOf<PreviewView?>(null) }

    // Permissions
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    )

    // State
    val lensFacing by cameraManager.lensFacing.collectAsState()
    val isTorchEnabled by cameraManager.isTorchEnabled.collectAsState()
    val hasFlash by cameraManager.hasFlashUnit.collectAsState()
    val motionResult by cameraManager.motionState.collectAsState()

    val activeShields = remember { mutableStateListOf<ActiveShield>() }
    var portalState by remember { mutableStateOf(PortalState()) }
    var whipState by remember { mutableStateOf(DualHandWhipState()) }

    var selectedShieldType by remember { mutableStateOf(ShieldType.ORANGE) }
    var isPortalMode by remember { mutableStateOf(false) }
    var isWhipMode by remember { mutableStateOf(false) }
    var isSoundEnabled by remember { mutableStateOf(true) }
    var isSpellbookOpen by remember { mutableStateOf(false) }

    // Voice Engine
    val voiceManager = remember {
        VoiceIncantationManager(context) { command ->
            when (command) {
                is VoiceSpellCommand.ShieldOrange -> {
                    selectedShieldType = ShieldType.ORANGE
                    isPortalMode = false
                    isWhipMode = false
                    summonShieldAt(
                        activeShields,
                        ShieldType.ORANGE,
                        Offset(screenWidthPx / 2f, screenHeightPx * 0.45f),
                        audioSynthesizer,
                        particleEngine
                    )
                }
                is VoiceSpellCommand.ShieldTime -> {
                    selectedShieldType = ShieldType.TIME
                    isPortalMode = false
                    isWhipMode = false
                    summonShieldAt(
                        activeShields,
                        ShieldType.TIME,
                        Offset(screenWidthPx / 2f, screenHeightPx * 0.45f),
                        audioSynthesizer,
                        particleEngine
                    )
                }
                is VoiceSpellCommand.ShieldMirror -> {
                    selectedShieldType = ShieldType.MIRROR
                    isPortalMode = false
                    isWhipMode = false
                    summonShieldAt(
                        activeShields,
                        ShieldType.MIRROR,
                        Offset(screenWidthPx / 2f, screenHeightPx * 0.45f),
                        audioSynthesizer,
                        particleEngine
                    )
                }
                is VoiceSpellCommand.ShieldCrimson -> {
                    selectedShieldType = ShieldType.CRIMSON
                    isPortalMode = false
                    isWhipMode = false
                    summonShieldAt(
                        activeShields,
                        ShieldType.CRIMSON,
                        Offset(screenWidthPx / 2f, screenHeightPx * 0.45f),
                        audioSynthesizer,
                        particleEngine
                    )
                }
                is VoiceSpellCommand.OpenPortal -> {
                    isPortalMode = true
                    portalState = portalState.copy(
                        isOpen = true,
                        center = Offset(screenWidthPx / 2f, screenHeightPx * 0.45f),
                        radius = 240f,
                        targetRadius = 240f
                    )
                    audioSynthesizer.playPortalOpenSound()
                    particleEngine.emitBurst(
                        Offset(screenWidthPx / 2f, screenHeightPx * 0.45f),
                        count = 30,
                        color = MysticGold
                    )
                }
                is VoiceSpellCommand.MysticWhips -> {
                    isWhipMode = !isWhipMode
                    whipState = DualHandWhipState(
                        isActive = isWhipMode,
                        hand1Pos = Offset(screenWidthPx * 0.25f, screenHeightPx * 0.5f),
                        hand2Pos = Offset(screenWidthPx * 0.75f, screenHeightPx * 0.5f),
                        energyIntensity = 1.0f
                    )
                    if (isWhipMode) {
                        audioSynthesizer.playWhipCrackSound()
                    }
                }
                is VoiceSpellCommand.SwitchCamera -> {
                    previewViewRef?.let { cameraManager.switchCamera(lifecycleOwner, it) }
                }
                is VoiceSpellCommand.ToggleTorch -> {
                    cameraManager.toggleTorch()
                }
                is VoiceSpellCommand.ClearSpells -> {
                    activeShields.clear()
                    portalState = PortalState(isOpen = false)
                    whipState = DualHandWhipState(isActive = false)
                    isPortalMode = false
                    isWhipMode = false
                }
            }
        }
    }

    val isListening by voiceManager.isListening.collectAsState()
    val isSpeechAvailable by voiceManager.isAvailable.collectAsState()
    val lastHeardVoiceText by voiceManager.lastHeardText.collectAsState()

    DisposableEffect(Unit) {
        voiceManager.initialize()
        onDispose {
            voiceManager.release()
            audioSynthesizer.release()
            cameraManager.release()
        }
    }

    // Motion triggered spell reactivity: if intense sudden movement is detected and no spells are active, emit sparks
    LaunchedEffect(motionResult.isSuddenMotionDetected) {
        if (motionResult.isSuddenMotionDetected && activeShields.isNotEmpty()) {
            activeShields.forEach { shield ->
                particleEngine.emitBurst(shield.center, count = 10, color = shield.type.accentColor)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        if (permissionsState.allPermissionsGranted) {
            // Live Camera Preview (Front or Back)
            CameraPreview(
                cameraManager = cameraManager,
                onPreviewViewReady = { pv ->
                    previewViewRef = pv
                }
            )

            // AR Overlay Canvas
            MysticOverlayCanvas(
                activeShields = activeShields,
                portalState = portalState,
                whipState = whipState,
                selectedShieldType = selectedShieldType,
                isPortalMode = isPortalMode,
                isWhipMode = isWhipMode,
                isAutoOrbitEnabled = false,
                audioSynthesizer = audioSynthesizer,
                particleEngine = particleEngine,
                onPortalUpdate = { portalState = it }
            )

            // HUD Controls
            MysticHudOverlay(
                lensFacing = lensFacing,
                isTorchEnabled = isTorchEnabled,
                hasFlash = hasFlash,
                isListening = isListening,
                isSpeechAvailable = isSpeechAvailable,
                lastHeardVoiceText = lastHeardVoiceText,
                motionResult = motionResult,
                selectedShieldType = selectedShieldType,
                isPortalActive = portalState.isOpen,
                isWhipActive = whipState.isActive,
                isSoundEnabled = isSoundEnabled,
                activeShieldCount = activeShields.size,
                onSwitchCamera = {
                    previewViewRef?.let { cameraManager.switchCamera(lifecycleOwner, it) }
                },
                onToggleTorch = { cameraManager.toggleTorch() },
                onToggleVoice = { voiceManager.toggleListening() },
                onToggleSound = {
                    isSoundEnabled = !isSoundEnabled
                    audioSynthesizer.setAudioEnabled(isSoundEnabled)
                },
                onSelectShield = { type ->
                    selectedShieldType = type
                    isPortalMode = false
                    isWhipMode = false
                },
                onTogglePortal = {
                    isPortalMode = !portalState.isOpen
                    portalState = if (!portalState.isOpen) {
                        audioSynthesizer.playPortalOpenSound()
                        particleEngine.emitBurst(
                            Offset(screenWidthPx / 2f, screenHeightPx * 0.45f),
                            count = 25,
                            color = MysticGold
                        )
                        PortalState(
                            isOpen = true,
                            center = Offset(screenWidthPx / 2f, screenHeightPx * 0.45f),
                            radius = 240f,
                            targetRadius = 240f
                        )
                    } else {
                        PortalState(isOpen = false)
                    }
                },
                onToggleWhips = {
                    isWhipMode = !whipState.isActive
                    whipState = if (!whipState.isActive) {
                        audioSynthesizer.playWhipCrackSound()
                        DualHandWhipState(
                            isActive = true,
                            hand1Pos = Offset(screenWidthPx * 0.25f, screenHeightPx * 0.48f),
                            hand2Pos = Offset(screenWidthPx * 0.75f, screenHeightPx * 0.48f),
                            energyIntensity = 1.0f
                        )
                    } else {
                        DualHandWhipState(isActive = false)
                    }
                },
                onSummonDualShields = {
                    activeShields.clear()
                    summonShieldAt(
                        activeShields,
                        selectedShieldType,
                        Offset(screenWidthPx * 0.28f, screenHeightPx * 0.48f),
                        audioSynthesizer,
                        particleEngine
                    )
                    summonShieldAt(
                        activeShields,
                        selectedShieldType,
                        Offset(screenWidthPx * 0.72f, screenHeightPx * 0.48f),
                        audioSynthesizer,
                        particleEngine
                    )
                },
                onClearSpells = {
                    activeShields.clear()
                    portalState = PortalState(isOpen = false)
                    whipState = DualHandWhipState(isActive = false)
                    isPortalMode = false
                    isWhipMode = false
                },
                onOpenSpellbook = { isSpellbookOpen = true }
            )

            // Spellbook Modal
            if (isSpellbookOpen) {
                SpellbookDialog(onDismiss = { isSpellbookOpen = false })
            }

        } else {
            // Permission Request Screen
            CameraPermissionFallback(onRequestPermissions = {
                permissionsState.launchMultiplePermissionRequest()
            })
        }
    }
}

private fun summonShieldAt(
    activeShields: MutableList<ActiveShield>,
    type: ShieldType,
    pos: Offset,
    audioSynthesizer: MysticAudioSynthesizer,
    particleEngine: ParticleEngine
) {
    activeShields.add(
        ActiveShield(
            id = "shield_${System.currentTimeMillis()}_${pos.x}",
            type = type,
            center = pos,
            radius = 210f
        )
    )
    particleEngine.emitBurst(pos, count = 25, color = type.primaryColor)
    audioSynthesizer.playSpellCastSound(type)
}

@Composable
private fun CameraPermissionFallback(
    onRequestPermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MysticDarkBg)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MysticSurface,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, MysticBorder),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = null,
                    tint = MysticGold,
                    modifier = Modifier.size(56.dp)
                )

                Text(
                    text = "Mystic Arts AR Requires Camera",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MysticGold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Grant camera and audio permissions to project Eldritch Shields, Multiverse Portals, and speak vocal incantations in live AR.",
                    fontSize = 13.sp,
                    color = Color(0xFFCBD5E1),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Button(
                    onClick = onRequestPermissions,
                    colors = ButtonDefaults.buttonColors(containerColor = MysticOrange),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Grant AR Permissions",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
