package com.akwiz.android.ui.components

import androidx.compose.foundation.BorderStroke
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
 * One reviewed answer. Every row shows its answer: a correct row confirms it, a wrong
 * or skipped row shows the right one too. Three signals per outcome — icon, label,
 * colour — same accessibility rule as [OptionCard].
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
    val circle: Color = when (outcome) {
        Outcome.Correct -> quiz.correctContainer
        Outcome.Wrong -> quiz.incorrectContainer
        Outcome.Skipped -> scheme.surfaceContainerHighest
    }

    val description = buildString {
        append("Question ").append(questionNumber).append(". ").append(questionText).append(". ")
        when (outcome) {
            Outcome.Correct -> append("Correct. Answer, $correctAnswer.")
            Outcome.Wrong -> append("Your answer, $yourAnswer, incorrect. Correct answer, $correctAnswer.")
            Outcome.Skipped -> append("Skipped. Correct answer, $correctAnswer.")
        }
    }

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = scheme.background,   // sits on the surface-coloured sheet, so it reads as a card
        border = BorderStroke(1.dp, scheme.outlineVariant),
        modifier = modifier
            .fillMaxWidth()
            .clearAndSetSemantics { contentDescription = description },
    ) {
        Row(
            Modifier.padding(Spacing.md),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Box(
                Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(circle),
                contentAlignment = Alignment.Center,
            ) {
                when (outcome) {
                    Outcome.Correct -> Icon(Icons.Filled.Check, null, tint = accent, modifier = Modifier.size(13.dp))
                    Outcome.Wrong -> Icon(Icons.Filled.Close, null, tint = accent, modifier = Modifier.size(13.dp))
                    Outcome.Skipped -> Box(Modifier.width(9.dp).height(2.dp).clip(CircleShape).background(accent))
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(
                    text = questionText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurface,
                )
                when (outcome) {
                    Outcome.Correct -> AnswerLine("Answer", correctAnswer, quiz.correct)
                    Outcome.Wrong -> {
                        AnswerLine("You", yourAnswer.orEmpty(), quiz.incorrect)
                        AnswerLine("Correct", correctAnswer, quiz.correct)
                    }
                    Outcome.Skipped -> {
                        AnswerLine("You", "Skipped", scheme.onSurfaceVariant)
                        AnswerLine("Correct", correctAnswer, quiz.correct)
                    }
                }
            }
        }
    }
}

@Composable
private fun AnswerLine(label: String, value: String, valueColor: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(52.dp),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            color = valueColor,
            fontWeight = FontWeight.Medium,
        )
    }
}
