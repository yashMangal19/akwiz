package com.akwiz.android.ui.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.akwiz.android.ui.theme.Spacing

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            Text("Akwiz", style = MaterialTheme.typography.displayMedium)
            CircularProgressIndicator(
                Modifier.size(28.dp),
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
fun ErrorScreen(
    message: String,
    canRetry: Boolean,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            Modifier
                .widthIn(max = Spacing.contentMaxWidth)
                .padding(Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
            if (canRetry) {
                Button(onClick = onRetry) { Text("Try again") }
            }
        }
    }
}

/**
 * Bridges to Phase 8. The resume dialog and the results screen replace these; the
 * Finished stub already wires Restart so the flow is playable end to end.
 */
@Composable
fun ResumePromptScreen(
    state: QuizUiState.ResumePrompt,
    onResume: () -> Unit,
    onStartOver: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            Modifier
                .widthIn(max = Spacing.contentMaxWidth)
                .padding(Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text("Welcome back", style = MaterialTheme.typography.headlineMedium)
            Text(
                "You were on question ${state.questionNumber} of ${state.total}, " +
                    "streak of ${state.currentStreak}.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Button(onClick = onResume) { Text("Resume") }
            OutlinedButton(onClick = onStartOver) { Text("Start over") }
        }
    }
}

@Composable
fun FinishedScreen(
    state: QuizUiState.Finished,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val result = state.result
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            Modifier
                .widthIn(max = Spacing.contentMaxWidth)
                .padding(Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Text(
                "${result.correct} / ${result.total}",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                "Longest streak ${result.longestStreak} · skipped ${result.skipped}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(onClick = onRestart, modifier = Modifier.padding(top = Spacing.md)) {
                Text("Play again")
            }
        }
    }
}
