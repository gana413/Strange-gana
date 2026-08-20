package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CrimsonBand
import com.example.ui.theme.MirrorCyan
import com.example.ui.theme.MysticBorder
import com.example.ui.theme.MysticDarkBg
import com.example.ui.theme.MysticGold
import com.example.ui.theme.MysticOrange
import com.example.ui.theme.MysticSurface
import com.example.ui.theme.TimeEmerald

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpellbookDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MysticSurface,
        scrimColor = Color(0x99000000),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Book of Cagliostro",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MysticGold
                    )
                    Text(
                        text = "Master the Mystic Arts & AR Incantations",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }

            // Section 1: Front & Back Camera Features
            SpellSection(title = "Dual Camera & Real-Time AR Engine") {
                Text(
                    text = "• Front Camera: Perfectly mirrored for selfie spell casting. Summon shields on your palms as you face the mirror of Kamar-Taj.\n" +
                            "• Back Camera: Projects Eldritch shields, portals, and energy tethers into the physical world in front of you.\n" +
                            "• Torch / Flashlight: Illuminate dark realms while casting back-camera spells.",
                    fontSize = 12.sp,
                    color = Color(0xFFE2E8F0),
                    lineHeight = 18.sp
                )
            }

            // Section 2: Touch & Gesture Controls
            SpellSection(title = "Somatic Gestures & Touch Casting") {
                Text(
                    text = "• Tap Anywhere: Manifests the active Eldritch Shield or Portal at that exact coordinate.\n" +
                            "• Drag to Move: Reposition an active shield across the live camera field.\n" +
                            "• Sling Ring Portal Expansion: Tap 'Multiverse Portal', then drag outwards to open and expand the interdimensional portal!\n" +
                            "• 5-Finger Energy Whips (5 తాళ్ళు): Activates high-voltage plasma bezier tethers connecting dual points.",
                    fontSize = 12.sp,
                    color = Color(0xFFE2E8F0),
                    lineHeight = 18.sp
                )
            }

            // Section 3: Voice Incantations
            SpellSection(title = "Vocal Incantations (Speech Control)") {
                Text(
                    text = "Activate the Microphone button in the HUD and speak any of the sacred phrases:\n" +
                            "• \"Orange Shield\" / \"Tao Shield\" -> Orange Tao Mandala\n" +
                            "• \"Time Shield\" / \"Green Shield\" / \"Agamotto\" -> Time Stone Chronal Shield\n" +
                            "• \"Mirror Dimension\" / \"Crystal\" -> Mirror Dimension Shield\n" +
                            "• \"Crimson Bands\" / \"Cyttorak\" -> Ruby Cyttorak Seal\n" +
                            "• \"Open Portal\" / \"Multiverse\" -> Expands Cosmic Portal\n" +
                            "• \"Whips\" / \"Tether\" -> Ignites 5 Plasma Whips\n" +
                            "• \"Switch Camera\" / \"Flip\" -> Toggles Front/Back Camera\n" +
                            "• \"Clear\" / \"Dispel\" -> Dispels all active spells",
                    fontSize = 12.sp,
                    color = Color(0xFFE2E8F0),
                    lineHeight = 18.sp
                )
            }

            // Section 4: Shield Types Lore
            SpellSection(title = "Eldritch Shield Disciplines") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ShieldLoreItem(
                        name = "Tao Mandala Shield",
                        color = MysticOrange,
                        description = "Harnesses ambient multidimensional energy to form impenetrable rotating sacred geometric discs with trailing orange sparks."
                    )
                    ShieldLoreItem(
                        name = "Time Stone Shield (Eye of Agamotto)",
                        color = TimeEmerald,
                        description = "Channels chronal energies to manipulate temporal vectors, displaying the emerald Agamotto glyph."
                    )
                    ShieldLoreItem(
                        name = "Mirror Dimension Shield",
                        color = MirrorCyan,
                        description = "Creates a parallel crystalline barrier that reflects all matter and energy."
                    )
                    ShieldLoreItem(
                        name = "Crimson Bands of Cyttorak",
                        color = CrimsonBand,
                        description = "Summons ruby mystical bands that bind and deflect the most destructive forces in the multiverse."
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = MysticGold),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Close Spellbook & Return to AR",
                    color = MysticDarkBg,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun SpellSection(
    title: String,
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0x66180B2B),
        border = androidx.compose.foundation.BorderStroke(1.dp, MysticBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MysticGold
            )
            content()
        }
    }
}

@Composable
private fun ShieldLoreItem(
    name: String,
    color: Color,
    description: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Column {
            Text(
                text = name,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
            Text(
                text = description,
                fontSize = 11.sp,
                color = Color(0xFFCBD5E1)
            )
        }
    }
}
