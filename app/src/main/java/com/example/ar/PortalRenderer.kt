package com.example.ar

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object PortalRenderer {

    fun renderCosmicPortal(
        drawScope: DrawScope,
        center: Offset,
        radius: Float,
        animTimeSec: Float
    ) {
        if (radius < 10f) return

        val innerRadius = radius * 0.82f

        // Clip interior to display the Multiverse Dimension
        val clipPath = Path().apply {
            addOval(
                Rect(
                    center = center,
                    radius = innerRadius
                )
            )
        }

        drawScope.clipPath(clipPath, clipOp = ClipOp.Intersect) {
            // Cosmic Void Backdrop
            drawCircle(
                color = Color(0xFF03010A),
                radius = innerRadius,
                center = center
            )

            // Multiverse Swirling Nebula Gradient
            val nebulaBrush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFE0AAFF),
                    Color(0xFF7B2CBF),
                    Color(0xFF00B4D8),
                    Color(0xFF03010A)
                ),
                center = center,
                radius = innerRadius
            )
            drawCircle(
                brush = nebulaBrush,
                radius = innerRadius,
                center = center
            )

            // Dynamic Cosmic Multiverse Stars & Galaxies
            for (i in 0 until 36) {
                val starAngle = (i * 137.5f * (PI.toFloat() / 180f)) + (animTimeSec * (0.2f + (i % 5) * 0.05f))
                val starDist = ((i * 17f + animTimeSec * 25f) % (innerRadius * 0.85f))
                val sx = center.x + cos(starAngle) * starDist
                val sy = center.y + sin(starAngle) * starDist

                val starColor = when (i % 3) {
                    0 -> Color(0xFFFFFFFF)
                    1 -> Color(0xFFFFD700)
                    else -> Color(0xFF80FFDB)
                }

                drawCircle(
                    color = starColor.copy(alpha = (0.5f + (i % 4) * 0.15f)),
                    radius = ((i % 3) + 1.2f),
                    center = Offset(sx, sy)
                )
            }

            // Distant Spiral Galaxy
            withTransform({
                translate(center.x, center.y)
                rotate(animTimeSec * 15f)
            }) {
                for (arm in 0 until 2) {
                    val armOffset = arm * PI.toFloat()
                    for (step in 0 until 20) {
                        val theta = (step * 0.25f) + armOffset
                        val r = step * (innerRadius / 25f)
                        val gx = cos(theta) * r
                        val gy = sin(theta) * r
                        drawCircle(
                            color = Color(0x99B5179E),
                            radius = 2.5f,
                            center = Offset(gx, gy)
                        )
                    }
                }
            }
        }

        // Blazing Event Horizon Glowing Rings
        drawScope.drawCircle(
            color = Color(0xFFFF9900).copy(alpha = 0.4f),
            radius = radius * 0.88f,
            center = center,
            style = Stroke(width = 8f)
        )

        drawScope.drawCircle(
            color = Color(0xFFFFDD44),
            radius = innerRadius,
            center = center,
            style = Stroke(width = 4f)
        )

        drawScope.drawCircle(
            color = Color.White,
            radius = innerRadius - 1f,
            center = center,
            style = Stroke(width = 1.8f)
        )

        // Event Horizon Sparks / Fire Rim Arc segments
        drawScope.withTransform({
            translate(center.x, center.y)
            rotate((animTimeSec * 120f) % 360f)
        }) {
            for (i in 0 until 8) {
                val segAngle = i * 45f
                rotate(segAngle) {
                    drawLine(
                        color = Color(0xFFFF6600),
                        start = Offset(innerRadius - 4f, 0f),
                        end = Offset(innerRadius + 14f, 0f),
                        strokeWidth = 2.5f,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}
