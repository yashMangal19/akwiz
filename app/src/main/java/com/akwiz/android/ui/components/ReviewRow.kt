package com.akwiz.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.akwiz.android.domain.Outcome
import com.akwiz.android.ui.theme.Spacing
import com.akwiz.android.ui.theme.quizColors

/**
 * One reviewed answer. Shows the correct answer whenever you didn't get it right, so
 * a wrong or skipped row teaches; a correct row just confirms. Three signals per
 * outcome — icon, label, colour — same accessibility rule as [OptionCard].
 */
@Composable
fun ReviewRow(
    questionNumber: Int,
    questionText: String,
    yourAnswer: String?,
    correctAnswer: String,
    outcome: Outcome,
    modifier: Modifier = Modifier,
) {
    val quiz = MaterialTheme.quizColors
    val scheme = MaterialTheme.colorScheme

    val accent: Color = when (outcome) {
        Outcome.Correct -> quiz.correct
        Outcome.Wrong -> quiz.incorrect
        Outcome.Skipped -> scheme.onSurfaceVariant
    }
    val yourLine = if (outcome == Outcome.Skipped) "Skipped" else "You: ${yourAnswer.orEmpty()}"

    val description = buildString {
        append("Question ").append(questionNumber).append(". ").append(questionText).append(". ")
        append(if (outcome == Outcome.Skipped) "Skipped." else "Your answer, $yourAnswer, ")
        append(
            when (outcome) {
                Outcome.Correct -> "correct."
                Outcome.Wrong -> "incorrect. Correct answer, $correctAnswer."
                Outcome.Skipped -> "Correct answer, $correctAnswer."
            },
        )
    }

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = scheme.surface,
        modifier = modifier
            .fillMaxWidth()
            .clearAndSetSemantics { contentDescription = description },
    ) {
        Row(
            Modifier.padding(Spacing.md),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Box(Modifier.size(20.dp).padding(top = 2.dp), contentAlignment = Alignment.Center) {
                when (outcome) {
                    Outcome.Correct -> Icon(Icons.Filled.Check, null, tint = accent, modifier = Modifier.size(18.dp))
                    Outcome.Wrong -> Icon(Icons.Filled.Close, null, tint = accent, modifier = Modifier.size(18.dp))
                    Outcome.Skipped -> Box(
                        Modifier.width(10.dp).height(2.dp).clip(CircleShape).background(accent),
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(
                    text = "$questionNumber. $questionText",
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurface,
                )
                Text(
                    text = yourLine,
                    style = MaterialTheme.typography.labelMedium,
                    color = accent,
                    fontWeight = FontWeight.Medium,
                )
                if (outcome != Outcome.Correct) {
                    Text(
                        text = "Correct: $correctAnswer",
                        style = MaterialTheme.typography.labelMedium,
                        color = quiz.correct,
                    )
                }
            }
        }
    }
}
