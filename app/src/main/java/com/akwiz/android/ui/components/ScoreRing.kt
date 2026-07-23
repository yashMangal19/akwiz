package com.akwiz.android.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.akwiz.android.ui.theme.Motion
import com.akwiz.android.ui.theme.quizColors

@Composable
fun ScoreRing(
    correct: Int,
    total: Int,
    modifier: Modifier = Modifier,
    diameter: androidx.compose.ui.unit.Dp = 132.dp,
) {
    val fraction = if (total == 0) 0f else correct / total.toFloat()

    // Previews render one frame, so animating from 0 would show an empty ring.
    val inspecting = LocalInspectionMode.current
    var start by remember { mutableStateOf(inspecting) }
    LaunchedEffect(Unit) { start = true }
    val sweep by animateFloatAsState(
        targetValue = if (start) fraction else 0f,
        animationSpec = tween(Motion.RESULTS_ENTRY),
        label = "sweep",
    )

    val track = MaterialTheme.colorScheme.surfaceContainerHighest
    val fill = MaterialTheme.quizColors.correct

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(diameter)
            .clearAndSetSemantics { contentDescription = "$correct out of $total correct" },
    ) {
        Canvas(Modifier.size(diameter)) {
            val stroke = size.minDimension * 0.07f
            val inset = stroke / 2f
            val arcSize = Size(size.width - stroke, size.height - stroke)
            drawArc(
                color = track,
                startAngle = 0f, sweepAngle = 360f, useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = stroke),
            )
            drawArc(
                color = fill,
                startAngle = -90f, sweepAngle = 360f * sweep, useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
        }
        Text(
            text = buildAnnotatedString {
                withStyle(MaterialTheme.typography.displayMedium.toSpanStyle()) { append("$correct") }
                withStyle(MaterialTheme.typography.titleMedium.toSpanStyle()) { append(" / $total") }
            },
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
