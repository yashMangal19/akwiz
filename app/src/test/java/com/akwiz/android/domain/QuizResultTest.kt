package com.akwiz.android.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class QuizResultTest {

    private fun result(vararg answers: AnswerRecord, longest: Int = 0) =
        QuizResult(answers.toList(), longest)

    @Test fun `counts each outcome`() {
        val r = result(
            AnswerRecord.answered(1, 0, isCorrect = true),
            AnswerRecord.answered(2, 1, isCorrect = true),
            AnswerRecord.answered(3, 2, isCorrect = false),
            AnswerRecord.skipped(4),
            longest = 2,
        )
        assertEquals(4, r.total)
        assertEquals(2, r.correct)
        assertEquals(1, r.wrong)
        assertEquals(1, r.skipped)
    }

    @Test fun `an empty run counts zero of everything`() {
        val r = result()
        assertEquals(0, r.total)
        assertEquals(0, r.correct)
        assertEquals(0, r.wrong)
        assertEquals(0, r.skipped)
    }

    @Test fun `an all-skipped run scores zero without failing`() {
        val r = result(AnswerRecord.skipped(1), AnswerRecord.skipped(2))
        assertEquals(2, r.total)
        assertEquals(0, r.correct)
        assertEquals(2, r.skipped)
    }

    @Test fun `a perfect run keeps its streak`() {
        val answers = (1..10).map { AnswerRecord.answered(it, 0, isCorrect = true) }
        val r = QuizResult(answers, longestStreak = 10)
        assertEquals(10, r.correct)
        assertEquals(10, r.total)
        assertEquals(10, r.longestStreak)
    }

    @Test fun `rejects a negative streak`() {
        assertThrows(IllegalArgumentException::class.java) { result(longest = -1) }
    }
}
