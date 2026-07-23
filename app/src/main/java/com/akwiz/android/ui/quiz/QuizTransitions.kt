package com.akwiz.android.ui.quiz

import com.akwiz.android.domain.AnswerRecord
import com.akwiz.android.ui.quiz.QuizUiState.Active

/**
 * The scoring logic, as pure functions. No ViewModel, no coroutines — so the whole
 * quiz tests as a truth table.
 */

fun Active.recordAnswer(selected: Int): Active {
    val correct = selected == currentQuestion.correctIndex
    val streak = if (correct) currentStreak + 1 else 0
    return copy(
        phase = AnswerPhase.Revealed(selected),
        answers = answers + AnswerRecord.answered(currentQuestion.id, selected, correct),
        currentStreak = streak,
        longestStreak = maxOf(longestStreak, streak),
    )
}

fun Active.recordSkip(): Active = copy(
    answers = answers + AnswerRecord.skipped(currentQuestion.id),
    currentStreak = 0,
)

/** Next question, or null when the quiz is over. */
fun Active.advancedOrNull(): Active? =
    if (index < set.questions.lastIndex) {
        copy(index = index + 1, phase = AnswerPhase.Awaiting)
    } else {
        null
    }
