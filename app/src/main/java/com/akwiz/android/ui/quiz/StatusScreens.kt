package com.akwiz.android.ui.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.akwiz.android.R
import com.akwiz.android.ui.theme.Spacing
import com.akwiz.android.ui.theme.quizColors

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
            Icon(
                painter = painterResource(R.drawable.ic_flame),
                contentDescription = null,
                tint = MaterialTheme.quizColors.streakActive,
                modifier = Modifier.size(32.dp),
            )
            Text("Welcome back", style = MaterialTheme.typography.headlineMedium)
            Text(
                "You were on question ${state.questionNumber} of ${state.total}" +
                    if (state.currentStreak > 0) ", on a streak of ${state.currentStreak}." else ".",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Button(onClick = onResume, modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm)) {
                Text("Resume", style = MaterialTheme.typography.labelLarge)
            }
            OutlinedButton(onClick = onStartOver, modifier = Modifier.fillMaxWidth()) {
                Text("Start over", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
