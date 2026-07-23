package com.akwiz.android.ui.quiz

import com.akwiz.android.domain.AnswerRecord
import com.akwiz.android.domain.Outcome
import com.akwiz.android.domain.Question
import com.akwiz.android.domain.QuestionSet
import com.akwiz.android.domain.QuizProgress
import com.akwiz.android.domain.QuizResult

const val STREAK_THRESHOLD = 3

/**
 * One variant per screen mode, so illegal states can't be built — Loading has no
 * selected option to misuse, and the UI is an exhaustive `when` with no else.
 */
sealed interface QuizUiState {

    data object Loading : QuizUiState

    data class Error(val message: String, val canRetry: Boolean) : QuizUiState

    data class ResumePrompt(
        val set: QuestionSet,
        val progress: QuizProgress,
    ) : QuizUiState {
        val questionNumber: Int get() = progress.index + 1
        val total: Int get() = set.questions.size
        val currentStreak: Int get() = progress.currentStreak
    }

    data class Active(
        val set: QuestionSet,
        val index: Int,
        val phase: AnswerPhase,
        val answers: List<AnswerRecord>,
        val currentStreak: Int,
        val longestStreak: Int,
    ) : QuizUiState

    data class Finished(
        val set: QuestionSet,
        val result: QuizResult,
        val isPersonalBest: Boolean,
    ) : QuizUiState
}

sealed interface AnswerPhase {
    data object Awaiting : AnswerPhase
    data class Revealed(val selected: Int) : AnswerPhase
}

// Derived — never stored, so nothing can drift out of sync with `answers`/`index`.
val QuizUiState.Active.currentQuestion: Question get() = set.questions[index]
val QuizUiState.Active.total: Int get() = set.questions.size
val QuizUiState.Active.questionNumber: Int get() = index + 1
val QuizUiState.Active.progress: Float get() = questionNumber / total.toFloat()
val QuizUiState.Active.isStreakHot: Boolean get() = currentStreak >= STREAK_THRESHOLD
val QuizUiState.Active.correctCount: Int get() = answers.count { it.outcome == Outcome.Correct }
val QuizUiState.Active.skippedCount: Int get() = answers.count { it.outcome == Outcome.Skipped }
val QuizUiState.Active.isRevealed: Boolean get() = phase is AnswerPhase.Revealed
