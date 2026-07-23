package com.akwiz.android.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** The launcher mark, in-app: a checkmark whose upstroke carries a spark. */
@Composable
fun AppMark(modifier: Modifier = Modifier, dimension: Dp = 56.dp) {
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary

    Box(
        modifier
            .size(dimension)
            .clip(RoundedCornerShape(percent = 28))
            .background(primary)
            .clearAndSetSemantics {},
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val s = size.minDimension
            // A centred checkmark, no spark.
            val check = Path().apply {
                moveTo(s * 0.30f, s * 0.51f)
                lineTo(s * 0.44f, s * 0.66f)
                lineTo(s * 0.70f, s * 0.36f)
            }
            drawPath(
                check, onPrimary,
                style = Stroke(width = s * 0.11f, cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
        }
    }
}
