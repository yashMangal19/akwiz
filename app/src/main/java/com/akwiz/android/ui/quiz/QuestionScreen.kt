package com.akwiz.android.ui.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.akwiz.android.ui.components.OptionCard
import com.akwiz.android.ui.components.ProgressHeader
import com.akwiz.android.ui.theme.Spacing

@Composable
fun QuestionScreen(
    state: QuizUiState.Active,
    onOption: (Int) -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val question = state.currentQuestion

    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            Modifier
                .widthIn(max = Spacing.contentMaxWidth)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            ProgressHeader(
                questionNumber = state.questionNumber,
                total = state.total,
                progress = state.progress,
                streak = state.currentStreak,
            )

            Text(
                text = question.text,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                question.options.forEachIndexed { index, option ->
                    OptionCard(
                        label = option,
                        indexLetter = optionLetter(index),
                        state = state.optionState(index),
                        onClick = { onOption(index) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            // Skip is only offered before answering — inert afterwards, so it hides.
            AnimatedVisibility(visible = !state.isRevealed) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TextButton(onClick = onSkip) {
                        Text("Skip", style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}
