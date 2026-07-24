package com.akwiz.android.ui.quiz

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.akwiz.android.ui.components.ConfettiBurst
import com.akwiz.android.ui.components.IgnitionRing
import com.akwiz.android.ui.theme.Motion
import com.akwiz.android.ui.theme.quizColors
import com.akwiz.android.ui.theme.rememberAnimationsEnabled
import com.akwiz.android.ui.theme.rememberTouchExplorationEnabled
import kotlinx.coroutines.delay

/**
 * The composition root: the only place that holds the ViewModel. Everything below
 * takes state and lambdas, so every screen stays previewable.
 */
@Composable
fun QuizRoute(
    modifier: Modifier = Modifier,
    viewModel: QuizViewModel = viewModel(factory = QuizViewModel.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val haptics = rememberHaptics()
    val animationsOn = rememberAnimationsEnabled()
    val talkBackOn = rememberTouchExplorationEnabled()

    // A screen reader can't hear a 2s reveal, so it advances manually instead.
    LaunchedEffect(talkBackOn) { viewModel.setAutoAdvance(!talkBackOn) }

    var igniting by remember { mutableStateOf(false) }
    var celebrating by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                QuizEffect.AnswerCorrect -> haptics.correct()
                QuizEffect.AnswerWrong -> haptics.wrong()
                QuizEffect.StreakIgnited -> { haptics.streak(); igniting = true }
                QuizEffect.PersonalBest -> celebrating = true
            }
        }
    }

    LaunchedEffect(igniting) { if (igniting) { delay(500); igniting = false } }
    LaunchedEffect(celebrating) { if (celebrating) { delay(1_100); celebrating = false } }

    Box(modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = state,
            contentKey = { it::class },   // fade between screen kinds, not within Active
            transitionSpec = {
                if (animationsOn) {
                    fadeIn(tween(Motion.STANDARD)) togetherWith fadeOut(tween(Motion.STANDARD))
                } else {
                    EnterTransition.None togetherWith ExitTransition.None
                }
            },
            label = "screen",
        ) { s ->
            when (s) {
                QuizUiState.Loading -> LoadingScreen()
                is QuizUiState.Error -> ErrorScreen(s.message, s.canRetry, onRetry = viewModel::retry)
                is QuizUiState.ResumePrompt -> ResumePromptScreen(
                    state = s,
                    onResume = viewModel::resumeSaved,
                    onStartOver = viewModel::startOver,
                )
                is QuizUiState.Active -> QuestionScreen(
                    state = s,
                    onOption = viewModel::selectOption,
                    onSkip = viewModel::skip,
                    onAdvance = viewModel::advanceNow,
                    manualAdvance = talkBackOn,
                    animate = animationsOn,
                )
                is QuizUiState.Finished -> ResultScreen(s, onRestart = viewModel::restart)
            }
        }

        IgnitionRing(
            playing = igniting,
            color = MaterialTheme.quizColors.streakActive,
            modifier = Modifier.fillMaxSize(),
            animate = animationsOn,
        )
        ConfettiBurst(
            playing = celebrating,
            colors = MaterialTheme.quizColors.celebration,
            modifier = Modifier.fillMaxSize(),
            animate = animationsOn,
        )
    }
}
