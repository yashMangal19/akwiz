package com.akwiz.android.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import com.akwiz.android.R
import com.akwiz.android.ui.theme.Motion
import com.akwiz.android.ui.theme.Spacing
import com.akwiz.android.ui.theme.quizColors
import com.akwiz.android.ui.quiz.STREAK_THRESHOLD

/**
 * Flame, count, and progress pips toward the threshold — so the mechanic is
 * visible before it fires. Reacts to [streak]; the ignition burst is separate.
 */
@Composable
fun StreakBadge(
    streak: Int,
    modifier: Modifier = Modifier,
    threshold: Int = STREAK_THRESHOLD,
) {
    val quiz = MaterialTheme.quizColors
    val hot = streak >= threshold

    val flame by animateColorAsState(
        targetValue = if (hot) quiz.streakActive else quiz.streakDormant,
        animationSpec = tween(Motion.EMPHATIC),
        label = "flame",
    )
    val scale by animateFloatAsState(
        targetValue = if (hot) 1f else 0.94f,
        animationSpec = tween(Motion.EMPHATIC),
        label = "badgeScale",
    )
    val container by animateColorAsState(
        targetValue = if (hot) quiz.streakContainer else Color.Transparent,
        animationSpec = tween(Motion.EMPHATIC),
        label = "badgeContainer",
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs + 2.dp),
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(percent = 50))
            .background(container)
            .then(
                if (hot) Modifier else Modifier.border(
                    1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(percent = 50),
                ),
            )
            .padding(horizontal = Spacing.md, vertical = Spacing.sm)
            .clearAndSetSemantics {
                contentDescription = "Streak: $streak correct in a row"
            },
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_flame),
            contentDescription = null,
            tint = flame,
            modifier = Modifier.size(17.dp),
        )
        Text(
            text = streak.toString(),
            style = MaterialTheme.typography.labelLarge,
            color = if (hot) quiz.streakOnContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        )
        // Filled pips use the active gold even while building, so progress toward
        // ignition is visible rather than grey-on-grey.
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(threshold) { i ->
                Box(
                    Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(if (i < streak) quiz.streakActive else MaterialTheme.colorScheme.outline),
                )
            }
        }
    }
}
