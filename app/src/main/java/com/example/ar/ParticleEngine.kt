package com.example.ar

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class SparkStreak(
    var cx: Float,
    var cy: Float,
    var baseAngle: Float,
    var distance: Float,
    var speed: Float = Random.nextFloat() * 0.08f + 0.04f,
    var radialVel: Float = Random.nextFloat() * 2.5f - 0.8f,
    val baseHue: Float = Random.nextFloat() * 20f + 25f,
    val size: Float = Random.nextFloat() * 2.5f + 1.2f,
    var alpha: Float = 1.0f,
    val decay: Float = Random.nextFloat() * 0.035f + 0.015f,
    val maxHistory: Int = Random.nextInt(4, 9),
    val customColor: Color? = null
) {
    val history = mutableListOf<Offset>()
    var active: Boolean = true

    init {
        val curX = cx + cos(baseAngle) * distance
        val curY = cy + sin(baseAngle) * distance
        history.add(Offset(curX, curY))
    }

    fun update(center: Offset, radius: Float) {
        cx = center.x
        cy = center.y
        baseAngle += speed
        distance += radialVel

        val curX = cx + cos(baseAngle) * distance
        val curY = cy + sin(baseAngle) * distance

        history.add(Offset(curX, curY))
        if (history.size > maxHistory) {
            history.removeAt(0)
        }

        alpha -= decay
        if (alpha <= 0f) {
            active = false
        }
    }

    fun draw(drawScope: DrawScope) {
        if (history.size < 2 || alpha <= 0f) return

        val strokeColor = customColor?.copy(alpha = alpha.coerceIn(0f, 1f))
            ?: Color(
                red = (1.0f).coerceIn(0f, 1f),
                green = ((baseHue / 60f)).coerceIn(0f, 1f),
                blue = 0.1f,
                alpha = alpha.coerceIn(0f, 1f)
            )

        for (i in 0 until history.size - 1) {
            val p1 = history[i]
            val p2 = history[i + 1]
            val progress = (i + 1).toFloat() / history.size.toFloat()
            drawScope.drawLine(
                color = strokeColor.copy(alpha = (alpha * progress).coerceIn(0f, 1f)),
                start = p1,
                end = p2,
                strokeWidth = size * progress
            )
        }
    }
}

class BurstSpark(
    var pos: Offset,
    var vel: Offset,
    val color: Color,
    val size: Float = Random.nextFloat() * 4f + 2f,
    var alpha: Float = 1.0f,
    val decay: Float = Random.nextFloat() * 0.04f + 0.02f,
    var active: Boolean = true
) {
    fun update() {
        pos = Offset(pos.x + vel.x, pos.y + vel.y)
        vel = Offset(vel.x * 0.96f, vel.y * 0.96f + 0.15f) // slight gravity
        alpha -= decay
        if (alpha <= 0f) {
            active = false
        }
    }

    fun draw(drawScope: DrawScope) {
        if (alpha <= 0f) return
        drawScope.drawCircle(
            color = color.copy(alpha = alpha.coerceIn(0f, 1f)),
            radius = size * alpha,
            center = pos
        )
    }
}

class ParticleEngine {
    val portalSparks = mutableListOf<SparkStreak>()
    val burstSparks = mutableListOf<BurstSpark>()
    val shieldSparks = mutableListOf<SparkStreak>()

    fun emitPortalSparks(center: Offset, radius: Float, count: Int = 8, color: Color? = null) {
        for (i in 0 until count) {
            val randomAngle = Random.nextFloat() * (Math.PI.toFloat() * 2f)
            val dist = radius * (0.85f + Random.nextFloat() * 0.3f)
            portalSparks.add(
                SparkStreak(
                    cx = center.x,
                    cy = center.y,
                    baseAngle = randomAngle,
                    distance = dist,
                    customColor = color
                )
            )
        }
    }

    fun emitShieldSparks(center: Offset, radius: Float, count: Int = 6, color: Color? = null) {
        for (i in 0 until count) {
            val randomAngle = Random.nextFloat() * (Math.PI.toFloat() * 2f)
            val dist = radius * (0.95f + Random.nextFloat() * 0.15f)
            shieldSparks.add(
                SparkStreak(
                    cx = center.x,
                    cy = center.y,
                    baseAngle = randomAngle,
                    distance = dist,
                    speed = (Random.nextFloat() * 0.06f + 0.02f) * if (Random.nextBoolean()) 1 else -1,
                    customColor = color
                )
            )
        }
    }

    fun emitBurst(center: Offset, count: Int = 20, color: Color) {
        for (i in 0 until count) {
            val angle = Random.nextFloat() * Math.PI.toFloat() * 2f
            val speed = Random.nextFloat() * 12f + 3f
            val vel = Offset(cos(angle) * speed, sin(angle) * speed)
            burstSparks.add(BurstSpark(pos = center, vel = vel, color = color))
        }
    }

    fun updateAndDraw(drawScope: DrawScope, portalCenter: Offset?, portalRadius: Float) {
        // Portal sparks
        if (portalCenter != null && portalRadius > 10f) {
            val it = portalSparks.iterator()
            while (it.hasNext()) {
                val spark = it.next()
                spark.update(portalCenter, portalRadius)
                spark.draw(drawScope)
                if (!spark.active) {
                    it.remove()
                }
            }
        } else {
            portalSparks.clear()
        }

        // Shield sparks
        val sIt = shieldSparks.iterator()
        while (sIt.hasNext()) {
            val spark = sIt.next()
            spark.update(Offset(spark.cx, spark.cy), spark.distance)
            spark.draw(drawScope)
            if (!spark.active) {
                sIt.remove()
            }
        }

        // Burst sparks
        val bIt = burstSparks.iterator()
        while (bIt.hasNext()) {
            val spark = bIt.next()
            spark.update()
            spark.draw(drawScope)
            if (!spark.active) {
                bIt.remove()
            }
        }
    }
}
