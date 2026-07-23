package com.akwiz.android.data

import androidx.datastore.core.CorruptionException
import com.akwiz.android.data.local.JsonSerializer
import com.akwiz.android.data.local.PlayerState
import com.akwiz.android.data.local.SavedAnswer
import com.akwiz.android.data.local.SavedSession
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class JsonSerializerTest {

    private val serializer = JsonSerializer(
        json = Json { ignoreUnknownKeys = true },
        kSerializer = PlayerState.serializer(),
        defaultValue = PlayerState(),
    )

    @Test fun `round-trips a player state`() = runBlocking {
        val original = PlayerState(
            bestStreak = 6,
            session = SavedSession(
                questionSetHash = "abc",
                answers = listOf(SavedAnswer(1, 2, "Correct"), SavedAnswer(2, null, "Skipped")),
                currentStreak = 1,
                longestStreak = 2,
                updatedAt = 1234L,
            ),
        )
        val bytes = ByteArrayOutputStream().also { serializer.writeTo(original, it) }.toByteArray()
        val restored = serializer.readFrom(ByteArrayInputStream(bytes))
        assertEquals(original, restored)
    }

    @Test fun `unknown fields survive a schema addition`() = runBlocking {
        val withExtra = """{"schemaVersion":1,"bestStreak":3,"addedInV2":true}"""
        val restored = serializer.readFrom(ByteArrayInputStream(withExtra.toByteArray()))
        assertEquals(3, restored.bestStreak)
    }

    @Test fun `garbage on disk raises a corruption exception`() {
        assertThrows(CorruptionException::class.java) {
            runBlocking { serializer.readFrom(ByteArrayInputStream("not json at all".toByteArray())) }
        }
    }
}
