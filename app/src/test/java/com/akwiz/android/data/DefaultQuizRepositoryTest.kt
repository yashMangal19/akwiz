package com.akwiz.android.data

import com.akwiz.android.data.local.BundledQuestionSource
import com.akwiz.android.data.remote.QuestionDto
import com.akwiz.android.data.remote.QuizApi
import com.akwiz.android.domain.DataOrigin
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultQuizRepositoryTest {

    private fun dto(id: Int, correct: Int = 0) =
        QuestionDto(id, "Question $id", listOf("A", "B", "C", "D"), correct)

    private val good = listOf(dto(1), dto(2), dto(3))
    private val allBroken = listOf(dto(1, correct = 9), dto(2, correct = -1))

    private class FakeApi(val answer: () -> List<QuestionDto>) : QuizApi {
        override suspend fun getQuestions() = answer()
    }

    private class FakeBundled(val answer: () -> List<QuestionDto>) : BundledQuestionSource {
        override fun read() = answer()
    }

    private fun repository(
        network: () -> List<QuestionDto>,
        bundled: () -> List<QuestionDto> = { good },
    ) = DefaultQuizRepository(FakeApi(network), FakeBundled(bundled), UnconfinedTestDispatcher())

    @Test fun `uses the network when it answers`() = runTest {
        val set = repository(network = { good }).loadQuestions().getOrThrow()
        assertEquals(DataOrigin.Network, set.origin)
        assertEquals(3, set.size)
    }

    @Test fun `falls back to the bundled copy when the network fails`() = runTest {
        val set = repository(network = { throw IOException("offline") }).loadQuestions().getOrThrow()
        assertEquals(DataOrigin.Bundled, set.origin)
        assertEquals(3, set.size)
    }

    // Garbage from the server and no answer from the server are the same event
    // as far as the player is concerned: an unusable response.
    @Test fun `falls back when every question from the network is malformed`() = runTest {
        val set = repository(network = { allBroken }).loadQuestions().getOrThrow()
        assertEquals(DataOrigin.Bundled, set.origin)
    }

    @Test fun `falls back when the network returns nothing`() = runTest {
        val set = repository(network = { emptyList() }).loadQuestions().getOrThrow()
        assertEquals(DataOrigin.Bundled, set.origin)
    }

    @Test fun `keeps the good rows when only some are malformed`() = runTest {
        val mixed = listOf(dto(1), dto(2, correct = 9), dto(3))
        val set = repository(network = { mixed }).loadQuestions().getOrThrow()
        assertEquals(2, set.size)
        assertEquals(listOf(1, 3), set.questions.map { it.id })
    }

    @Test fun `fails only when both sources are unusable`() = runTest {
        val result = repository(
            network = { throw IOException("offline") },
            bundled = { throw IOException("missing asset") },
        ).loadQuestions()
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NoQuestionsAvailable)
    }

    @Test fun `fails when the bundled copy is also malformed`() = runTest {
        val result = repository(network = { throw IOException("offline") }, bundled = { allBroken })
        .loadQuestions()
        assertTrue(result.isFailure)
    }

    // Same questions from either source must hash the same, otherwise falling back
    // would discard a session that's still valid.
    @Test fun `network and bundled hash identically for the same questions`() = runTest {
        val viaNetwork = repository(network = { good }).loadQuestions().getOrThrow()
        val viaBundled = repository(network = { throw IOException() }, bundled = { good })
            .loadQuestions().getOrThrow()
        assertEquals(viaNetwork.contentHash, viaBundled.contentHash)
        assertEquals(DataOrigin.Network, viaNetwork.origin)
        assertEquals(DataOrigin.Bundled, viaBundled.origin)
    }
}
