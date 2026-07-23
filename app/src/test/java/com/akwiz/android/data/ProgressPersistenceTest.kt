package com.akwiz.android.data

import com.akwiz.android.data.local.SavedAnswer
import com.akwiz.android.data.local.SavedSession
import com.akwiz.android.data.remote.QuestionDto
import com.akwiz.android.domain.AnswerRecord
import com.akwiz.android.domain.QuestionSet
import com.akwiz.android.domain.QuizProgress
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class ProgressPersistenceTest {

    private val dtos = (1..5).map { QuestionDto(it, "Q$it", listOf("A", "B", "C", "D"), 0) }

    private fun repository(
        player: FakePlayerStore = FakePlayerStore(),
        clock: FakeClock = FakeClock(),
    ) = DefaultQuizRepository(
        api = FakeApi { dtos },
        cache = FakeCacheStore(),
        bundled = FakeBundled { dtos },
        player = player,
        clock = clock,
        io = UnconfinedTestDispatcher(),
    )

    // The set the session belongs to, so hashes line up.
    private suspend fun loadedSet(repo: DefaultQuizRepository): QuestionSet =
        repo.loadQuestions().getOrThrow()

    // Two answers → the resume index derives to 2.
    private fun session(hash: String, updatedAt: Long = 0L) = SavedSession(
        questionSetHash = hash,
        answers = listOf(SavedAnswer(1, 0, "Correct"), SavedAnswer(2, null, "Skipped")),
        currentStreak = 1,
        longestStreak = 1,
        updatedAt = updatedAt,
    )

    @Test fun `offers matching progress within the TTL`() = runTest {
        val player = FakePlayerStore()
        val clock = FakeClock(TimeUnit.HOURS.toMillis(1))
        val repo = repository(player, clock)
        val set = loadedSet(repo)
        player.session = session(set.contentHash, updatedAt = 0L)

        val progress = repo.readProgress(set)
        assertEquals(2, progress!!.index)
        assertEquals(2, progress.answers.size)
    }

    @Test fun `discards progress for a different question set`() = runTest {
        val player = FakePlayerStore(session = session("some-other-hash"))
        val repo = repository(player)
        assertNull(repo.readProgress(loadedSet(repo)))
    }

    @Test fun `discards progress past the 24h TTL`() = runTest {
        val player = FakePlayerStore()
        val clock = FakeClock()
        val repo = repository(player, clock)
        val set = loadedSet(repo)
        player.session = session(set.contentHash, updatedAt = 0L)
        clock.current = TimeUnit.HOURS.toMillis(24) + 1

        assertNull(repo.readProgress(set))
    }

    @Test fun `keeps progress exactly on the TTL boundary`() = runTest {
        val player = FakePlayerStore()
        val clock = FakeClock()
        val repo = repository(player, clock)
        val set = loadedSet(repo)
        player.session = session(set.contentHash, updatedAt = 0L)
        clock.current = TimeUnit.HOURS.toMillis(24)

        assertEquals(2, repo.readProgress(set)!!.index)
    }

    // A completed run is returned (not discarded) so the caller can show the
    // result again on relaunch; index derives to the question count.
    @Test fun `returns a completed session with index at the end`() = runTest {
        val player = FakePlayerStore()
        val repo = repository(player)
        val set = loadedSet(repo)   // 5 questions
        player.session = session(set.contentHash).copy(
            answers = (1..5).map { SavedAnswer(it, 0, "Correct") },
        )
        val progress = repo.readProgress(set)
        assertEquals(5, progress!!.index)
        assertEquals(5, progress.answers.size)
    }

    @Test fun `discards progress with an unreadable outcome`() = runTest {
        val player = FakePlayerStore()
        val repo = repository(player)
        val set = loadedSet(repo)
        player.session = session(set.contentHash).copy(
            answers = listOf(SavedAnswer(1, 0, "NotAnOutcome")),
        )
        assertNull(repo.readProgress(set))
    }

    @Test fun `saved progress round-trips including a skipped answer`() = runTest {
        val player = FakePlayerStore()
        val clock = FakeClock(500L)
        val repo = repository(player, clock)
        val set = loadedSet(repo)

        val progress = QuizProgress(
            questionSetHash = set.contentHash,
            index = 2,   // two answers → next question is index 2
            answers = listOf(
                AnswerRecord.answered(1, selected = 2, isCorrect = true),
                AnswerRecord.skipped(2),
            ),
            currentStreak = 0,
            longestStreak = 1,
        )
        repo.saveProgress(progress)

        assertEquals(500L, player.session!!.updatedAt)
        assertEquals(progress, repo.readProgress(set))
    }

    @Test fun `clearing progress leaves the cache untouched`() = runTest {
        val player = FakePlayerStore()
        val cache = FakeCacheStore(stored = dtos)
        val repo = DefaultQuizRepository(
            FakeApi { dtos }, cache, FakeBundled { dtos }, player, FakeClock(),
            UnconfinedTestDispatcher(),
        )
        player.session = session("h")
        repo.clearProgress()

        assertNull(player.session)
        assertEquals(5, cache.stored.size)
    }

    @Test fun `best streak only moves up`() = runTest {
        val player = FakePlayerStore()
        val repo = repository(player)
        repo.recordBestStreak(4)
        repo.recordBestStreak(2)
        assertEquals(4, repo.bestStreak())
        repo.recordBestStreak(7)
        assertEquals(7, repo.bestStreak())
    }
}
