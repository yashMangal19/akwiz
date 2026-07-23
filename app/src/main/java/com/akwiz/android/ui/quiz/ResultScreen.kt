package com.akwiz.android.ui.quiz

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.akwiz.android.R
import com.akwiz.android.ui.components.PrimaryButton
import com.akwiz.android.ui.components.ReviewRow
import com.akwiz.android.ui.components.SecondaryButton
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
    var showReview by rememberSaveable { mutableStateOf(false) }

    Column(modifier.fillMaxSize()) {
        // Scrollable content, taking the space above the pinned action bar.
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                Modifier
                    .widthIn(max = Spacing.contentMaxWidth)
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg)
                    .padding(top = Spacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Quiz complete", style = MaterialTheme.typography.headlineMedium)

                Spacer(Modifier.height(Spacing.lg))
                ScoreRing(correct = result.correct, total = result.total)

                if (state.isPersonalBest) {
                    Spacer(Modifier.height(Spacing.md))
                    PersonalBestBanner(streak = result.longestStreak)
                }

                // Deliberate breathing room before the stat rows, banner or not.
                Spacer(Modifier.height(Spacing.xl))

                StatList(
                    correct = result.correct,
                    longestStreak = result.longestStreak,
                    skipped = result.skipped,
                    wrong = result.wrong,
                )
            }
        }

        // Pinned action bar at the bottom of the screen.
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(
                Modifier
                    .widthIn(max = Spacing.contentMaxWidth)
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg)
                    .padding(top = Spacing.md, bottom = Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                PrimaryButton("Play again", onRestart, Modifier.fillMaxWidth())
                SecondaryButton("Review answers", { showReview = true }, Modifier.fillMaxWidth())
            }
        }
    }

    if (showReview) {
        ReviewSheet(state = state, onDismiss = { showReview = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReviewSheet(state: QuizUiState.Finished, onDismiss: () -> Unit) {
    // Default state opens partially expanded (not full-screen) and drags up.
    val sheetState = rememberModalBottomSheetState()
    val review = state.review()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(Modifier.padding(horizontal = Spacing.lg)) {
            Text("Review", style = MaterialTheme.typography.headlineSmall)
            Text(
                "${state.result.correct} of ${state.result.total} correct",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(Spacing.sm))
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = Spacing.contentMaxWidth),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            items(review) { item ->
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
    ) {
        Row(
            Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
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
private fun StatList(correct: Int, longestStreak: Int, skipped: Int, wrong: Int) {
    val quiz = MaterialTheme.quizColors
    val scheme = MaterialTheme.colorScheme
    Column(
        Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        StatRow(
            painter = null,
            icon = Icons.Filled.Check,
            label = "Correct",
            value = correct,
            iconTint = quiz.correct,
            circle = quiz.correctContainer,
            valueColor = quiz.correct,
        )
        StatRow(
            painter = painterResource(R.drawable.ic_flame),
            label = "Longest streak",
            value = longestStreak,
            iconTint = quiz.streakActive,
            circle = quiz.streakContainer,
            valueColor = quiz.streakActive,
        )
        StatRow(
            painter = null,
            label = "Skipped",
            value = skipped,
            iconTint = scheme.onSurfaceVariant,
            circle = scheme.surfaceContainerHighest,
            valueColor = scheme.onSurfaceVariant,
        )
        StatRow(
            painter = null,
            icon = Icons.Filled.Close,
            label = "Wrong",
            value = wrong,
            iconTint = quiz.incorrect,
            circle = quiz.incorrectContainer,
            valueColor = quiz.incorrect,
        )
    }
}

@Composable
private fun StatRow(
    painter: Painter?,
    label: String,
    value: Int,
    iconTint: Color,
    circle: Color,
    valueColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            Modifier.padding(horizontal = Spacing.md, vertical = Spacing.md),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(24.dp)
                    .background(circle, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    painter != null -> Icon(painter, null, tint = iconTint, modifier = Modifier.size(13.dp))
                    icon != null -> Icon(icon, null, tint = iconTint, modifier = Modifier.size(13.dp))
                    else -> Box(Modifier.width(9.dp).height(2.dp).background(iconTint, CircleShape))
                }
            }
            Text(
                label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            Text(
                value.toString(),
                style = MaterialTheme.typography.headlineSmall,
                color = valueColor,
            )
        }
    }
}
