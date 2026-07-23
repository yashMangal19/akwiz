package com.akwiz.android.data

import com.akwiz.android.data.remote.QuestionDto
import com.akwiz.android.data.remote.toDomain
import com.akwiz.android.data.remote.toDomainOrNull
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class QuestionMapperTest {

    private fun dto(
        id: Int = 1,
        question: String = "Which permission?",
        options: List<String> = listOf("A", "B", "C", "D"),
        correct: Int = 0,
    ) = QuestionDto(id, question, options, correct)

    @Test fun `maps a well formed question`() {
        val q = dto(correct = 2).toDomainOrNull()
        assertNotNull(q)
        assertEquals("Which permission?", q!!.text)
        assertEquals(2, q.correctIndex)
        assertEquals("C", q.correctOption)
    }

    @Test fun `trims surrounding whitespace`() {
        val q = dto(question = "  spaced  ", options = listOf(" A ", "B", "C", "D")).toDomainOrNull()
        assertEquals("spaced", q!!.text)
        assertEquals("A", q.options[0])
    }

    // The most likely real malformation: index equal to the option count.
    @Test fun `drops an index one past the end`() {
        assertNull(dto(correct = 4).toDomainOrNull())
    }

    @Test fun `drops a negative index`() {
        assertNull(dto(correct = -1).toDomainOrNull())
    }

    @Test fun `drops an empty option list`() {
        assertNull(dto(options = emptyList(), correct = 0).toDomainOrNull())
    }

    @Test fun `drops a single option`() {
        assertNull(dto(options = listOf("only")).toDomainOrNull())
    }

    @Test fun `drops a blank option`() {
        assertNull(dto(options = listOf("A", "   ", "C", "D")).toDomainOrNull())
    }

    @Test fun `drops a blank question`() {
        assertNull(dto(question = "   ").toDomainOrNull())
    }

    @Test fun `keeps the good rows and discards the bad`() {
        val questions = listOf(
            dto(id = 1),
            dto(id = 2, correct = 9),
            dto(id = 3),
            dto(id = 4, question = ""),
        ).toDomain()
        assertEquals(2, questions.size)
        assertEquals(listOf(1, 3), questions.map { it.id })
    }

    @Test fun `returns empty when nothing survives`() {
        assertEquals(emptyList<Any>(), listOf(dto(correct = 9), dto(question = "")).toDomain())
    }

    @Test fun `unknown fields in the payload are ignored`() {
        val json = Json { ignoreUnknownKeys = true }
        val payload = """
            [{"id":1,"question":"q","options":["a","b"],"correctOptionIndex":0,"addedLater":true}]
        """.trimIndent()
        val parsed = json.decodeFromString<List<QuestionDto>>(payload).toDomain()
        assertEquals(1, parsed.size)
    }

    @Test fun `accepts a question with more than four options`() {
        val q = dto(options = listOf("A", "B", "C", "D", "E"), correct = 4).toDomainOrNull()
        assertEquals("E", q!!.correctOption)
    }
}
