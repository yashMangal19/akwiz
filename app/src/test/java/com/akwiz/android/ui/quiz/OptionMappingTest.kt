package com.akwiz.android.ui.quiz

import com.akwiz.android.ui.components.OptionCardState
import org.junit.Assert.assertEquals
import org.junit.Test

class OptionMappingTest {

    // Correct answer is index 0 in the test set.
    private fun active(phase: AnswerPhase) = QuizUiState.Active(
        set = testSet(3),
        index = 0,
        phase = phase,
        answers = emptyList(),
        currentStreak = 0,
        longestStreak = 0,
    )

    private fun states(phase: AnswerPhase) =
        active(phase).let { a -> List(4) { a.optionState(it) } }

    @Test fun `awaiting shows every option as tappable`() {
        assertEquals(List(4) { OptionCardState.Awaiting }, states(AnswerPhase.Awaiting))
    }

    @Test fun `answering correctly shows one correct-chosen and the rest muted`() {
        assertEquals(
            listOf(
                OptionCardState.CorrectChosen,
                OptionCardState.Muted,
                OptionCardState.Muted,
                OptionCardState.Muted,
            ),
            states(AnswerPhase.Revealed(selected = 0)),
        )
    }

    // FR-3: a wrong answer shows the correct card AND the chosen card, at once.
    @Test fun `answering wrong shows correct and your-pick together`() {
        assertEquals(
            listOf(
                OptionCardState.Correct,
                OptionCardState.WrongChosen,
                OptionCardState.Muted,
                OptionCardState.Muted,
            ),
            states(AnswerPhase.Revealed(selected = 1)),
        )
    }

    @Test fun `letters run A B C D`() {
        assertEquals(listOf("A", "B", "C", "D"), List(4) { optionLetter(it) })
    }
}
