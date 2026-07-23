package com.akwiz.android.ui.quiz

import com.akwiz.android.domain.Outcome
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class QuizTransitionsTest {

    private fun active(index: Int = 0, streak: Int = 0, longest: Int = 0) = QuizUiState.Active(
        set = testSet(3),
        index = index,
        phase = AnswerPhase.Awaiting,
        answers = emptyList(),
        currentStreak = streak,
        longestStreak = longest,
    )

    @Test fun `a correct answer reveals it and grows the streak`() {
        val next = active(streak = 2, longest = 2).recordAnswer(0)
        assertEquals(AnswerPhase.Revealed(0), next.phase)
        assertEquals(3, next.currentStreak)
        assertEquals(3, next.longestStreak)
        assertEquals(Outcome.Correct, next.answers.single().outcome)
    }

    @Test fun `a wrong answer resets the streak but keeps the longest`() {
        val next = active(streak = 4, longest = 4).recordAnswer(1)
        assertEquals(0, next.currentStreak)
        assertEquals(4, next.longestStreak)
        assertEquals(Outcome.Wrong, next.answers.single().outcome)
    }

    @Test fun `a skip records a skip and breaks the streak`() {
        val next = active(streak = 2).recordSkip()
        assertEquals(0, next.currentStreak)
        assertEquals(Outcome.Skipped, next.answers.single().outcome)
        assertNull(next.answers.single().selected)
    }

    @Test fun `advancing moves to the next question awaiting`() {
        val next = active(index = 0).recordAnswer(0).advancedOrNull()!!
        assertEquals(1, next.index)
        assertEquals(AnswerPhase.Awaiting, next.phase)
    }

    @Test fun `advancing past the last question ends the quiz`() {
        assertNull(active(index = 2).advancedOrNull())
    }
}
