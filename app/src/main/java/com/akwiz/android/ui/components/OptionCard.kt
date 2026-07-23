package com.akwiz.android.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.unit.dp
import com.akwiz.android.ui.theme.Motion
import com.akwiz.android.ui.theme.Spacing
import com.akwiz.android.ui.theme.quizColors

/**
 * One option's display role. The screen decides which role each option is in; the
 * card only renders it, so it never needs the question data.
 */
enum class OptionCardState { Awaiting, Correct, CorrectChosen, WrongChosen, Muted }

@Composable
fun OptionCard(
    label: String,
    indexLetter: String,
    state: OptionCardState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val quiz = MaterialTheme.quizColors
    val awaiting = state == OptionCardState.Awaiting

    val container by animateColorAsState(
        targetValue = when (state) {
            OptionCardState.Awaiting, OptionCardState.Muted -> scheme.surface
            OptionCardState.Correct, OptionCardState.CorrectChosen -> quiz.correctContainer
            OptionCardState.WrongChosen -> quiz.incorrectContainer
        },
        animationSpec = tween(Motion.STANDARD),
        label = "optionContainer",
    )
    val alpha by animateFloatAsState(
        targetValue = if (state == OptionCardState.Muted) 0.44f else 1f,
        animationSpec = tween(Motion.STANDARD),
        label = "optionAlpha",
    )

    val accent = when (state) {
        OptionCardState.Correct, OptionCardState.CorrectChosen -> quiz.correct
        OptionCardState.WrongChosen -> quiz.incorrect
        else -> scheme.onSurfaceVariant
    }
    val icon: ImageVector? = when (state) {
        OptionCardState.Correct, OptionCardState.CorrectChosen -> Icons.Filled.Check
        OptionCardState.WrongChosen -> Icons.Filled.Close
        else -> null
    }
    val stateLabel = when (state) {
        OptionCardState.Correct -> "Correct answer"
        OptionCardState.CorrectChosen -> "Correct — your answer"
        OptionCardState.WrongChosen -> "Your answer"
        else -> null
    }

    val description = buildString {
        append("Option ").append(indexLetter).append(", ").append(label)
        stateLabel?.let { append(", ").append(it) }
    }

    Surface(
        onClick = onClick,
        enabled = awaiting,
        shape = MaterialTheme.shapes.medium,
        color = container,
        border = if (awaiting) BorderStroke(1.dp, scheme.outline) else null,
        modifier = modifier
            .fillMaxWidth()
            .alpha(alpha)
            .clearAndSetSemantics {
                contentDescription = description
                if (awaiting) role = Role.Button else disabled()
            },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            modifier = Modifier
                .defaultMinSize(minHeight = Spacing.optionMinHeight)
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(if (icon != null) accent else scheme.surfaceContainerHighest),
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp),
                    )
                } else {
                    Text(
                        text = indexLetter,
                        style = MaterialTheme.typography.labelMedium,
                        color = scheme.onSurfaceVariant,
                    )
                }
            }
            Column(Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    color = scheme.onSurface,
                )
                if (stateLabel != null) {
                    Text(
                        text = stateLabel.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = accent,
                    )
                }
            }
        }
    }
}
