package com.example.ar

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.sin

object EnergyWhipRenderer {

    fun renderEnergyWhips(
        drawScope: DrawScope,
        p1: Offset,
        p2: Offset,
        animTimeSec: Float,
        intensity: Float = 1.0f
    ) {
        val whipColors = listOf(
            Color(0xFFFF9900),
            Color(0xFFFF7700),
            Color(0xFFFF5500),
            Color(0xFFFF3300),
            Color(0xFFFFB703)
        )

        // Draw 5 distinct plasma arc energy strings
        for (i in 0 until 5) {
            val progress = i / 4f
            val curvatureOffset = (i - 2) * 35f + sin(animTimeSec * 8f + i) * 18f

            val midX = (p1.x + p2.x) / 2f + sin(animTimeSec * 10f + i * 1.5f) * 20f
            val midY = (p1.y + p2.y) / 2f + curvatureOffset

            val path = Path().apply {
                moveTo(p1.x, p1.y)
                quadraticTo(midX, midY, p2.x, p2.y)
            }

            val color = whipColors[i % whipColors.size]

            // Outer glow halo
            drawScope.drawPath(
                path = path,
                color = color.copy(alpha = 0.35f * intensity),
                style = Stroke(width = (10f - i * 1.2f) * intensity, cap = StrokeCap.Round)
            )

            // Primary Energy Core
            drawScope.drawPath(
                path = path,
                color = color.copy(alpha = 0.9f * intensity),
                style = Stroke(width = (4f - i * 0.4f) * intensity, cap = StrokeCap.Round)
            )

            // Blazing White Center Stream
            drawScope.drawPath(
                path = path,
                color = Color.White.copy(alpha = 0.95f * intensity),
                style = Stroke(width = 1.5f * intensity, cap = StrokeCap.Round)
            )

            // Lightning spark jitter along the whip
            val jitterT = (animTimeSec * 3f + i * 0.2f) % 1.0f
            val jx = (1 - jitterT) * (1 - jitterT) * p1.x + 2 * (1 - jitterT) * jitterT * midX + jitterT * jitterT * p2.x
            val jy = (1 - jitterT) * (1 - jitterT) * p1.y + 2 * (1 - jitterT) * jitterT * midY + jitterT * jitterT * p2.y

            drawScope.drawCircle(
                color = Color(0xFFFFF3B0),
                radius = (5f + sin(animTimeSec * 20f) * 2f) * intensity,
                center = Offset(jx, jy)
            )
        }

        // Terminal Mystic Nodes at anchor points
        listOf(p1, p2).forEach { anchor ->
            drawScope.drawCircle(
                color = Color(0xFFFF9900).copy(alpha = 0.4f),
                radius = 18f * intensity,
                center = anchor
            )
            drawScope.drawCircle(
                color = Color(0xFFFFDD44),
                radius = 9f * intensity,
                center = anchor
            )
            drawScope.drawCircle(
                color = Color.White,
                radius = 4f * intensity,
                center = anchor
            )
        }
    }
}
