package com.example.ar

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object ShieldRenderer {

    fun drawEldritchShield(
        drawScope: DrawScope,
        center: Offset,
        radius: Float,
        shieldType: ShieldType,
        animTimeSec: Float,
        alpha: Float = 1.0f
    ) {
        if (radius <= 5f || alpha <= 0f) return

        val baseColor = shieldType.primaryColor.copy(alpha = alpha)
        val brightColor = shieldType.accentColor.copy(alpha = alpha)
        val glowColor = shieldType.glowColor.copy(alpha = alpha * 0.7f)

        val scale = radius / 285f

        val rotSlowCW = (animTimeSec * 25f) % 360f
        val rotFastCW = (animTimeSec * 75f) % 360f
        val rotSlowCCW = (-animTimeSec * 18f) % 360f
        val rotFastCCW = (-animTimeSec * 55f) % 360f

        drawScope.withTransform({
            translate(left = center.x, top = center.y)
        }) {
            // Ambient outer glow halo
            drawCircle(
                color = glowColor.copy(alpha = 0.15f * alpha),
                radius = 295f * scale
            )

            // LAYER 1: Core Sacred Squares & Rings (rotSlowCW)
            rotate(rotSlowCW) {
                // Central core nodes
                drawCircle(
                    color = brightColor,
                    radius = 8f * scale,
                    style = Stroke(width = 3f * scale)
                )
                drawCircle(
                    color = baseColor,
                    radius = 35f * scale,
                    style = Stroke(width = 2.5f * scale)
                )
                // Dashed ring
                drawCircle(
                    color = brightColor,
                    radius = 60f * scale,
                    style = Stroke(
                        width = 2.5f * scale,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f * scale, 10f * scale), 0f)
                    )
                )

                // Concentric nested squares (Eldritch Geometry)
                val sqSide = 120f * scale
                val halfSq = sqSide / 2f
                drawRect(
                    color = baseColor,
                    topLeft = Offset(-halfSq, -halfSq),
                    size = Size(sqSide, sqSide),
                    style = Stroke(width = 2f * scale)
                )

                rotate(45f) {
                    drawRect(
                        color = brightColor,
                        topLeft = Offset(-halfSq, -halfSq),
                        size = Size(sqSide, sqSide),
                        style = Stroke(width = 2f * scale)
                    )
                }
            }

            // LAYER 2: Sacred Opposing Triangles (rotFastCCW)
            rotate(rotFastCCW) {
                drawCircle(
                    color = baseColor,
                    radius = 110f * scale,
                    style = Stroke(width = 3.5f * scale)
                )
                drawCircle(
                    color = baseColor,
                    radius = 140f * scale,
                    style = Stroke(
                        width = 2f * scale,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f * scale, 14f * scale), 0f)
                    )
                )

                // Equilateral Triangles forming Star of Mystic Arts
                drawEquilateralTriangle(this, 140f * scale, 0f, brightColor, 2.5f * scale)
                drawEquilateralTriangle(this, 140f * scale, 180f, brightColor, 2.5f * scale)

                // Cardinal Cross Axis
                drawLine(
                    color = baseColor,
                    start = Offset(-140f * scale, 0f),
                    end = Offset(140f * scale, 0f),
                    strokeWidth = 1.8f * scale
                )
                drawLine(
                    color = baseColor,
                    start = Offset(0f, -140f * scale),
                    end = Offset(0f, 140f * scale),
                    strokeWidth = 1.8f * scale
                )
            }

            // LAYER 3: Triple Interlocking Sacred Glyphs (rotFastCW)
            rotate(rotFastCW) {
                drawCircle(
                    color = baseColor,
                    radius = 185f * scale,
                    style = Stroke(width = 3.5f * scale)
                )
                drawCircle(
                    color = baseColor,
                    radius = 225f * scale,
                    style = Stroke(
                        width = 2.5f * scale,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f * scale, 18f * scale), 0f)
                    )
                )

                // Triquetra triangle trio
                drawEquilateralTriangle(this, 190f * scale, 15f, baseColor, 2f * scale)
                drawEquilateralTriangle(this, 190f * scale, 135f, baseColor, 2f * scale)
                drawEquilateralTriangle(this, 190f * scale, 255f, baseColor, 2f * scale)

                // Rune tick marks
                for (i in 0 until 12) {
                    val angle = (i * 30f) * (PI.toFloat() / 180f)
                    val r1 = 215f * scale
                    val r2 = 225f * scale
                    drawLine(
                        color = brightColor,
                        start = Offset(cos(angle) * r1, sin(angle) * r1),
                        end = Offset(cos(angle) * r2, sin(angle) * r2),
                        strokeWidth = 2f * scale,
                        cap = StrokeCap.Round
                    )
                }
            }

            // LAYER 4: Outer Tao Boundary & Cardinal Runes (rotSlowCCW)
            rotate(rotSlowCCW) {
                drawCircle(
                    color = brightColor,
                    radius = 260f * scale,
                    style = Stroke(width = 3.5f * scale)
                )
                drawCircle(
                    color = baseColor,
                    radius = 275f * scale,
                    style = Stroke(
                        width = 2f * scale,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f * scale, 14f * scale), 0f)
                    )
                )
                drawCircle(
                    color = baseColor,
                    radius = 285f * scale,
                    style = Stroke(width = 2.2f * scale)
                )

                // Cardinal & Intercardinal Nodes (8 Runes)
                val nodeDist = 285f * scale
                val angles = floatArrayOf(0f, 45f, 90f, 135f, 180f, 225f, 270f, 315f)
                for (a in angles) {
                    val rad = a * (PI.toFloat() / 180f)
                    val nx = cos(rad) * nodeDist
                    val ny = sin(rad) * nodeDist

                    // Glowing rune dot
                    drawCircle(
                        color = glowColor,
                        radius = 7f * scale,
                        center = Offset(nx, ny)
                    )
                    drawCircle(
                        color = brightColor,
                        radius = 4.5f * scale,
                        center = Offset(nx, ny)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 2f * scale,
                        center = Offset(nx, ny)
                    )
                }
            }

            // Time Stone specific central Eye glyph if TIME shield
            if (shieldType == ShieldType.TIME) {
                rotate(rotFastCW * 0.5f) {
                    val eyePath = Path().apply {
                        moveTo(-45f * scale, 0f)
                        quadraticTo(0f, -28f * scale, 45f * scale, 0f)
                        quadraticTo(0f, 28f * scale, -45f * scale, 0f)
                        close()
                    }
                    drawPath(
                        path = eyePath,
                        color = Color(0xFF00FF88),
                        style = Stroke(width = 3f * scale)
                    )
                    drawCircle(
                        color = Color(0xFFE0FFFA),
                        radius = 12f * scale,
                        center = Offset.Zero
                    )
                    drawCircle(
                        color = Color(0xFF00FF88),
                        radius = 6f * scale,
                        center = Offset.Zero
                    )
                }
            }
        }
    }

    private fun drawEquilateralTriangle(
        drawScope: DrawScope,
        radius: Float,
        rotDeg: Float,
        color: Color,
        strokeWidth: Float
    ) {
        val path = Path()
        val rotRad = rotDeg * (PI.toFloat() / 180f)

        for (i in 0 until 3) {
            val ang = (i * 2f * PI.toFloat() / 3f) - (PI.toFloat() / 2f) + rotRad
            val px = cos(ang) * radius
            val py = sin(ang) * radius
            if (i == 0) {
                path.moveTo(px, py)
            } else {
                path.lineTo(px, py)
            }
        }
        path.close()

        drawScope.drawPath(
            path = path,
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}
