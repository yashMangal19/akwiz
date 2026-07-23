package com.akwiz.android.data

import com.akwiz.android.domain.Question
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ContentHashTest {

    private val base = listOf(
        Question(1, "First", listOf("A", "B", "C", "D"), 0),
        Question(2, "Second", listOf("W", "X", "Y", "Z"), 3),
    )

    @Test fun `is stable for the same content`() {
        assertEquals(contentHashOf(base), contentHashOf(base.map { it.copy() }))
    }

    @Test fun `changes when option text changes`() {
        val edited = base.mapIndexed { i, q -> if (i == 0) q.copy(options = listOf("A!", "B", "C", "D")) else q }
        assertNotEquals(contentHashOf(base), contentHashOf(edited))
    }

    @Test fun `changes when the correct answer moves`() {
        val edited = base.mapIndexed { i, q -> if (i == 0) q.copy(correctIndex = 1) else q }
        assertNotEquals(contentHashOf(base), contentHashOf(edited))
    }

    @Test fun `changes when questions are reordered`() {
        assertNotEquals(contentHashOf(base), contentHashOf(base.reversed()))
    }

    @Test fun `changes when a question is removed`() {
        assertNotEquals(contentHashOf(base), contentHashOf(base.take(1)))
    }

    // The reason for hashing questions rather than the response bytes: reformatting
    // the source must not throw away a session that's still valid.
    @Test fun `is unaffected by how the payload was formatted`() {
        val fromCompactJson = listOf(
            Question(1, "First", listOf("A", "B", "C", "D"), 0),
            Question(2, "Second", listOf("W", "X", "Y", "Z"), 3),
        )
        assertEquals(contentHashOf(base), contentHashOf(fromCompactJson))
    }

    @Test fun `produces a sha256 length hex string`() {
        val hash = contentHashOf(base)
        assertEquals(64, hash.length)
        assertEquals(true, hash.all { it in "0123456789abcdef" })
    }
}
