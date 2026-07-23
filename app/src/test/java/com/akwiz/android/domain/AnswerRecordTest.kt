package com.akwiz.android.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class AnswerRecordTest {

    @Test fun `a correct answer keeps the selection`() {
        val r = AnswerRecord.answered(questionId = 3, selected = 2, isCorrect = true)
        assertEquals(Outcome.Correct, r.outcome)
        assertEquals(2, r.selected)
    }

    @Test fun `a wrong answer keeps the selection`() {
        val r = AnswerRecord.answered(questionId = 3, selected = 1, isCorrect = false)
        assertEquals(Outcome.Wrong, r.outcome)
        assertEquals(1, r.selected)
    }

    @Test fun `a skip has no selection`() {
        val r = AnswerRecord.skipped(questionId = 5)
        assertEquals(Outcome.Skipped, r.outcome)
        assertNull(r.selected)
    }

    @Test fun `rejects a skip that carries a selection`() {
        assertThrows(IllegalArgumentException::class.java) {
            AnswerRecord(questionId = 1, selected = 0, outcome = Outcome.Skipped)
        }
    }

    @Test fun `rejects an answer with no selection`() {
        assertThrows(IllegalArgumentException::class.java) {
            AnswerRecord(questionId = 1, selected = null, outcome = Outcome.Correct)
        }
    }
}
