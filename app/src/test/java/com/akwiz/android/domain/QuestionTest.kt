package com.akwiz.android.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class QuestionTest {

    private fun question(options: List<String> = listOf("a", "b", "c", "d"), correct: Int = 0) =
        Question(id = 1, text = "q", options = options, correctIndex = correct)

    @Test fun `exposes the correct option`() {
        assertEquals("c", question(correct = 2).correctOption)
    }

    @Test fun `rejects a correct index past the last option`() {
        assertThrows(IllegalArgumentException::class.java) { question(correct = 4) }
    }

    @Test fun `rejects a negative correct index`() {
        assertThrows(IllegalArgumentException::class.java) { question(correct = -1) }
    }

    @Test fun `rejects fewer than two options`() {
        assertThrows(IllegalArgumentException::class.java) { question(options = listOf("only")) }
    }

    @Test fun `accepts an option count other than four`() {
        val q = question(options = listOf("a", "b", "c", "d", "e"), correct = 4)
        assertEquals("e", q.correctOption)
    }

    @Test fun `copy is validated too`() {
        assertThrows(IllegalArgumentException::class.java) { question().copy(correctIndex = 9) }
    }
}
