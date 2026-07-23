package com.akwiz.android.data

import com.akwiz.android.data.remote.QuestionDto
import com.akwiz.android.domain.DataOrigin
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultQuizRepositoryTest {

    private fun dto(id: Int, correct: Int = 0) =
        QuestionDto(id, "Question $id", listOf("A", "B", "C", "D"), correct)

    private val good = listOf(dto(1), dto(2), dto(3))
    private val allBroken = listOf(dto(1, correct = 9), dto(2, correct = -1))

    private fun repository(
        network: () -> List<QuestionDto>,
        cache: FakeCacheStore = FakeCacheStore(),
        bundled: () -> List<QuestionDto> = { good },
        player: FakePlayerStore = FakePlayerStore(),
        clock: FakeClock = FakeClock(),
    ) = DefaultQuizRepository(
        api = FakeApi(network),
        cache = cache,
        bundled = FakeBundled(bundled),
        player = player,
        clock = clock,
        io = UnconfinedTestDispatcher(),
    )

    @Test fun `uses the network when it answers`() = runTest {
        val set = repository(network = { good }).loadQuestions().getOrThrow()
        assertEquals(DataOrigin.Network, set.origin)
        assertEquals(3, set.size)
    }

    @Test fun `a successful fetch is written to the cache`() = runTest {
        val cache = FakeCacheStore()
        repository(network = { good }, cache = cache).loadQuestions().getOrThrow()
        assertEquals(3, cache.stored.size)
        assertEquals(1, cache.writes)
    }

    @Test fun `falls back to the cache when the network fails`() = runTest {
        val cache = FakeCacheStore(stored = good)
        val set = repository(network = failing(), cache = cache).loadQuestions().getOrThrow()
        assertEquals(DataOrigin.Cache, set.origin)
    }

    @Test fun `falls back to bundled when network fails and cache is empty`() = runTest {
        val set = repository(network = failing(), cache = FakeCacheStore()).loadQuestions().getOrThrow()
        assertEquals(DataOrigin.Bundled, set.origin)
    }

    @Test fun `a corrupt cache falls through to bundled`() = runTest {
        val cache = FakeCacheStore(stored = allBroken)
        val set = repository(network = failing(), cache = cache).loadQuestions().getOrThrow()
        assertEquals(DataOrigin.Bundled, set.origin)
    }

    @Test fun `all-malformed network falls through to cache`() = runTest {
        val cache = FakeCacheStore(stored = good)
        val set = repository(network = { allBroken }, cache = cache).loadQuestions().getOrThrow()
        assertEquals(DataOrigin.Cache, set.origin)
    }

    @Test fun `fails only when every source is unusable`() = runTest {
        val result = repository(
            network = failing(),
            cache = FakeCacheStore(),
            bundled = failing(),
        ).loadQuestions()
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NoQuestionsAvailable)
    }

    @Test fun `network and cache hash identically for the same questions`() = runTest {
        val viaNetwork = repository(network = { good }).loadQuestions().getOrThrow()
        val viaCache = repository(network = failing(), cache = FakeCacheStore(stored = good))
            .loadQuestions().getOrThrow()
        assertEquals(viaNetwork.contentHash, viaCache.contentHash)
    }
}
