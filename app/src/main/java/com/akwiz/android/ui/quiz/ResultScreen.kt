package com.akwiz.android.ui.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.akwiz.android.R
import com.akwiz.android.ui.components.ReviewRow
import com.akwiz.android.ui.components.ScoreRing
import com.akwiz.android.ui.theme.Spacing
import com.akwiz.android.ui.theme.quizColors

@Composable
fun ResultScreen(
    state: QuizUiState.Finished,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val result = state.result
    val review = state.review()

    Column(
        modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            Modifier
                .widthIn(max = Spacing.contentMaxWidth)
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text(
                "Quiz complete",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = Spacing.xl),
            )

            ScoreRing(correct = result.correct, total = result.total)

            if (state.isPersonalBest) {
                PersonalBestBanner(streak = result.longestStreak)
            }

            StatRow(result.longestStreak, result.skipped, result.wrong)

            Button(onClick = onRestart, modifier = Modifier.fillMaxWidth()) {
                Text("Play again", style = MaterialTheme.typography.labelLarge)
            }

            Text(
                "Review",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.md),
                textAlign = TextAlign.Start,
            )
        }

        Column(
            Modifier
                .widthIn(max = Spacing.contentMaxWidth)
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg)
                .padding(bottom = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            review.forEach { item ->
                ReviewRow(
                    questionNumber = item.questionNumber,
                    questionText = item.questionText,
                    yourAnswer = item.yourAnswer,
                    correctAnswer = item.correctAnswer,
                    outcome = item.outcome,
                )
            }
        }
    }
}

@Composable
private fun PersonalBestBanner(streak: Int) {
    Surface(
        shape = RoundedCornerShape(percent = 50),
        color = MaterialTheme.quizColors.streakContainer,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_trophy),
                contentDescription = null,
                tint = MaterialTheme.quizColors.streakActive,
                modifier = Modifier.size(18.dp),
            )
            Text(
                "New best streak — $streak in a row",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.quizColors.streakOnContainer,
            )
        }
    }
}

@Composable
private fun StatRow(longestStreak: Int, skipped: Int, wrong: Int) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Stat("Longest streak", longestStreak)
        Stat("Skipped", skipped)
        Stat("Wrong", wrong)
    }
}

@Composable
private fun Stat(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value.toString(), style = MaterialTheme.typography.headlineSmall)
        Text(
            label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
