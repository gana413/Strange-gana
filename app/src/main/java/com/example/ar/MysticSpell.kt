package com.example.ar

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CrimsonBand
import com.example.ui.theme.MirrorCyan
import com.example.ui.theme.MysticGold
import com.example.ui.theme.MysticOrange
import com.example.ui.theme.TimeEmerald

enum class ShieldType(
    val title: String,
    val primaryColor: Color,
    val accentColor: Color,
    val glowColor: Color,
    val description: String
) {
    ORANGE(
        title = "Tao Mandala Shield",
        primaryColor = MysticOrange,
        accentColor = MysticGold,
        glowColor = Color(0xFFFF4500),
        description = "Classic Eldritch geometric defense barrier"
    ),
    TIME(
        title = "Time Stone Shield",
        primaryColor = TimeEmerald,
        accentColor = Color(0xFFE0FFFA),
        glowColor = Color(0xFF00FF44),
        description = "Agamotto chronal shield manipulating temporal flow"
    ),
    MIRROR(
        title = "Mirror Dimension",
        primaryColor = MirrorCyan,
        accentColor = Color(0xFFD0F4DE),
        glowColor = Color(0xFF00B4D8),
        description = "Crystalline prism geometry warping space"
    ),
    CRIMSON(
        title = "Bands of Cyttorak",
        primaryColor = CrimsonBand,
        accentColor = Color(0xFFFF758F),
        glowColor = Color(0xFFFF0055),
        description = "Ruby mystical binding runes of immense power"
    )
}

data class ActiveShield(
    val id: String,
    val type: ShieldType,
    val center: Offset,
    val radius: Float,
    val birthTime: Long = System.currentTimeMillis(),
    val durationMs: Long = 8000L
)

data class PortalState(
    val isOpen: Boolean = false,
    val center: Offset = Offset.Zero,
    val radius: Float = 0f,
    val targetRadius: Float = 0f,
    val rotation: Float = 0f,
    val totalRotations: Float = 0f
)

data class DualHandWhipState(
    val isActive: Boolean = false,
    val hand1Pos: Offset = Offset.Zero,
    val hand2Pos: Offset = Offset.Zero,
    val energyIntensity: Float = 1.0f
)
