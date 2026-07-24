package com.akwiz.android.ui.quiz

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import com.akwiz.android.ui.components.OptionCard
import com.akwiz.android.ui.components.ProgressHeader
import com.akwiz.android.ui.theme.Motion
import com.akwiz.android.ui.theme.Spacing

private const val SWIPE_THRESHOLD = -120f   // leftward drag distance to advance

@Composable
fun QuestionScreen(
    state: QuizUiState.Active,
    onOption: (Int) -> Unit,
    onSkip: () -> Unit,
    onAdvance: () -> Unit,
    modifier: Modifier = Modifier,
    manualAdvance: Boolean = false,
    animate: Boolean = true,
) {
    val onSwipeForward = { if (state.isRevealed) onAdvance() else onSkip() }

    Box(
        modifier
            .fillMaxSize()
            .swipeForward(onSwipeForward),
        contentAlignment = Alignment.TopCenter,
    ) {
        AnimatedContent(
            targetState = state,
            contentKey = { it.index },   // slide only when the question changes, not on reveal
            transitionSpec = {
                if (animate) {
                    (slideInHorizontally(tween(Motion.STANDARD)) { it / 2 } + fadeIn(tween(Motion.STANDARD)))
                        .togetherWith(
                            slideOutHorizontally(tween(Motion.STANDARD)) { -it / 2 } + fadeOut(tween(Motion.STANDARD)),
                        )
                } else {
                    EnterTransition.None togetherWith ExitTransition.None
                }
            },
            label = "question",
        ) { current ->
            QuestionContent(current, onOption, onSkip, onAdvance, manualAdvance)
        }
    }
}

@Composable
private fun QuestionContent(
    state: QuizUiState.Active,
    onOption: (Int) -> Unit,
    onSkip: () -> Unit,
    onAdvance: () -> Unit,
    manualAdvance: Boolean,
) {
    val question = state.currentQuestion

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
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
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

        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            when {
                !state.isRevealed -> TextButton(onClick = onSkip) {
                    Text("Skip", style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center)
                }
                // During a reveal we only show a control when auto-advance is off
                // (a screen reader is on); otherwise the 2s timer carries it.
                manualAdvance -> TextButton(onClick = onAdvance) {
                    Text("Next question", style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

private fun Modifier.swipeForward(onSwipeForward: () -> Unit): Modifier = this.pointerInput(Unit) {
    var total = 0f
    detectHorizontalDragGestures(
        onDragStart = { total = 0f },
        onDragEnd = { if (total <= SWIPE_THRESHOLD) onSwipeForward() },
    ) { _, drag -> total += drag }
}
