package com.akwiz.android.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.semantics.clearAndSetSemantics
import com.akwiz.android.ui.theme.Motion
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

private const val GRAVITY = 1.9f
private const val DRAG = 0.72f
private const val FADE_STARTS_AT = 0.55f

@Immutable
private data class Particle(
    val angleRad: Float,
    val speed: Float,
    val spin: Float,
    val color: Color,
    val size: Float,
    val aspect: Float,
    val isCircle: Boolean,
    val drift: Float,
    val phase: Float,
)

private fun buildParticles(count: Int, colors: List<Color>, seed: Int): List<Particle> {
    val rnd = Random(seed)
    return List(count) {
        Particle(
            angleRad = (-PI / 2 + (rnd.nextFloat() - 0.5f) * PI * 0.95).toFloat(),
            speed = 0.45f + rnd.nextFloat() * 0.75f,
            spin = (rnd.nextFloat() - 0.5f) * 5f,
            color = colors[rnd.nextInt(colors.size)],
            size = 0.018f + rnd.nextFloat() * 0.022f,
            aspect = 0.35f + rnd.nextFloat() * 0.65f,
            isCircle = rnd.nextFloat() < 0.28f,
            drift = (rnd.nextFloat() - 0.5f) * 0.16f,
            phase = rnd.nextFloat() * 2f * PI.toFloat(),
        )
    }
}

/**
 * Confetti burst in the given palette colours.
 *
 * Positions are derived from [progress] rather than accumulated per frame, so the
 * animation is deterministic for a seed and survives recomposition.
 */
@Composable
fun ConfettiBurst(
    playing: Boolean,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    particleCount: Int = 64,
    durationMillis: Int = Motion.CELEBRATION,
    origin: Offset = Offset(0.5f, 0.62f),
    seed: Int = 0,
    animate: Boolean = true,
) {
    if (!animate || colors.isEmpty()) return

    val particles = remember(particleCount, colors, seed) {
        buildParticles(particleCount, colors, seed)
    }
    val progress by animateFloatAsState(
        targetValue = if (playing) 1f else 0f,
        // snap back to 0 rather than tween, so it doesn't replay in reverse
        animationSpec = if (playing) tween(durationMillis, easing = LinearEasing) else snap(),
        label = "confetti",
    )
    if (progress <= 0f || progress >= 1f) return

    Canvas(modifier = modifier.clearAndSetSemantics { }) {
        val unit = minOf(size.width, size.height)
        val start = Offset(size.width * origin.x, size.height * origin.y)
        val t = progress
        val travel = (1f - DRAG.pow(t * 6f)) / (1f - DRAG)

        particles.forEach { p ->
            val sway = sin(p.phase + t * 6f) * p.drift * t
            val x = start.x + (cos(p.angleRad) * p.speed * travel * 0.28f + sway) * unit
            val y = start.y + (sin(p.angleRad) * p.speed * travel * 0.28f + GRAVITY * t * t * 0.5f) * unit

            val alpha = if (t < FADE_STARTS_AT) 1f
            else (1f - (t - FADE_STARTS_AT) / (1f - FADE_STARTS_AT)).coerceIn(0f, 1f)
            if (alpha <= 0.01f) return@forEach

            val side = p.size * unit
            rotate(degrees = p.spin * t * 360f, pivot = Offset(x, y)) {
                if (p.isCircle) {
                    drawCircle(p.color, side / 2f, Offset(x, y), alpha)
                } else {
                    val h = side * p.aspect
                    drawRect(
                        color = p.color,
                        topLeft = Offset(x - side / 2f, y - h / 2f),
                        size = Size(side, h),
                        alpha = alpha,
                    )
                }
            }
        }
    }
}

/** Expanding ring, fired once when the streak crosses the threshold. */
@Composable
fun IgnitionRing(
    playing: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    durationMillis: Int = Motion.EMPHATIC,
    animate: Boolean = true,
) {
    if (!animate) return

    val progress by animateFloatAsState(
        targetValue = if (playing) 1f else 0f,
        animationSpec = if (playing) tween(durationMillis, easing = LinearEasing) else snap(),
        label = "ignition",
    )
    if (progress <= 0f || progress >= 1f) return

    Canvas(modifier = modifier.clearAndSetSemantics { }) {
        val maxRadius = minOf(size.width, size.height) * 0.95f
        drawCircle(
            color = color,
            radius = maxRadius * (0.35f + progress * 0.65f),
            center = center,
            alpha = ((1f - progress) * 0.85f).coerceIn(0f, 1f),
            style = Stroke(width = maxRadius * 0.09f),
        )
    }
}
