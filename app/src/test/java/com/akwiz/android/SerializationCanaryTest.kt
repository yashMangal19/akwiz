package com.akwiz.android

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class SerializationCanaryTest {
    @Serializable
    data class Probe(val id: Int, val question: String, val options: List<String>, val correctOptionIndex: Int)

    private val json = Json { ignoreUnknownKeys = true }

    @Test fun `parses the endpoint payload and ignores unknown fields`() {
        val payload = """
            [{"id":1,"question":"q","options":["a","b","c","d"],"correctOptionIndex":0,"extra":"ignored"}]
        """.trimIndent()
        val parsed = json.decodeFromString<List<Probe>>(payload)
        assertEquals(1, parsed.size)
        assertEquals(4, parsed[0].options.size)
        assertEquals(0, parsed[0].correctOptionIndex)
    }
}
