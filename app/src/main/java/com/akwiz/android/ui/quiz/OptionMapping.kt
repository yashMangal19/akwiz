package com.akwiz.android.ui.quiz

import com.akwiz.android.ui.components.OptionCardState

/**
 * Which display role each option is in. Pure, because it needs the question's
 * correct index and the user's selection — the data the OptionCard deliberately
 * doesn't carry — and so it unit-tests without rendering.
 */
fun QuizUiState.Active.optionState(optionIndex: Int): OptionCardState = when (val p = phase) {
    AnswerPhase.Awaiting -> OptionCardState.Awaiting
    is AnswerPhase.Revealed -> {
        val correct = optionIndex == currentQuestion.correctIndex
        val chosen = optionIndex == p.selected
        when {
            correct && chosen -> OptionCardState.CorrectChosen
            correct -> OptionCardState.Correct
            chosen -> OptionCardState.WrongChosen
            else -> OptionCardState.Muted
        }
    }
}

fun optionLetter(index: Int): String = ('A' + index).toString()
