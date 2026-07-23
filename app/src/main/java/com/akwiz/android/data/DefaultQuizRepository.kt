package com.akwiz.android.data

import com.akwiz.android.data.local.BundledQuestionSource
import com.akwiz.android.data.local.PlayerStore
import com.akwiz.android.data.local.QuestionCacheStore
import com.akwiz.android.data.local.SavedAnswer
import com.akwiz.android.data.local.SavedSession
import com.akwiz.android.data.remote.QuestionDto
import com.akwiz.android.data.remote.QuizApi
import com.akwiz.android.data.remote.toDomain
import com.akwiz.android.domain.AnswerRecord
import com.akwiz.android.domain.DataOrigin
import com.akwiz.android.domain.Outcome
import com.akwiz.android.domain.QuestionSet
import com.akwiz.android.domain.QuizProgress
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

private val SESSION_TTL_MS = TimeUnit.HOURS.toMillis(24)

/**
 * Owns which source wins and whether saved progress is still valid. Callers ask for
 * questions or progress and get them; they never see the storage shapes.
 */
internal class DefaultQuizRepository(
    private val api: QuizApi,
    private val cache: QuestionCacheStore,
    private val bundled: BundledQuestionSource,
    private val player: PlayerStore,
    private val clock: Clock,
    private val io: CoroutineDispatcher = Dispatchers.IO,
) : QuizRepository {

    // network → cache → bundled, tried in order
    override suspend fun loadQuestions(): Result<QuestionSet> {
        networkDtos()?.let { dtos ->
            dtos.toQuestionSet(DataOrigin.Network)?.let {
                cacheQuietly(dtos)
                return Result.success(it)
            }
        }
        fromCache()?.let { return Result.success(it) }
        fromBundled()?.let { return Result.success(it) }
        return Result.failure(NoQuestionsAvailable())
    }

    override suspend fun readProgress(set: QuestionSet): QuizProgress? {
        val session = player.readSession() ?: return null
        if (session.questionSetHash != set.contentHash) return null
        if (clock.now() - session.updatedAt > SESSION_TTL_MS) return null
        // At least one answer, up to and including a completed run — the caller
        // decides resume vs finished from answers.size.
        if (session.answers.size !in 1..set.questions.size) return null
        return session.toProgressOrNull()
    }

    override suspend fun saveProgress(progress: QuizProgress) {
        player.writeSession(progress.toSaved(clock.now()))
    }

    override suspend fun clearProgress() = player.clearSession()

    override suspend fun bestStreak(): Int = player.bestStreak()

    override suspend fun recordBestStreak(value: Int) = player.recordBestStreak(value)

    private suspend fun networkDtos(): List<QuestionDto>? = attempt { api.getQuestions() }

    private suspend fun fromCache(): QuestionSet? =
        attempt { withContext(io) { cache.read() } }?.toQuestionSet(DataOrigin.Cache)

    private suspend fun fromBundled(): QuestionSet? =
        attempt { withContext(io) { bundled.read() } }?.toQuestionSet(DataOrigin.Bundled)

    /** Best-effort: a cache write must never fail an otherwise-good load. */
    private suspend fun cacheQuietly(dtos: List<QuestionDto>) {
        try {
            cache.write(dtos)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // ignore — the questions are already in hand
        }
    }

    private suspend fun <T> attempt(block: suspend () -> T): T? =
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }
}

private fun List<QuestionDto>.toQuestionSet(origin: DataOrigin): QuestionSet? {
    val questions = toDomain()
    if (questions.isEmpty()) return null
    return QuestionSet(questions, contentHashOf(questions), origin)
}

// Any malformation from disk (bad outcome name, a skipped answer with a selection)
// throws during mapping and is treated as no resumable progress.
private fun SavedSession.toProgressOrNull(): QuizProgress? = runCatching {
    val records = answers.map { AnswerRecord(it.questionId, it.selected, Outcome.valueOf(it.outcome)) }
    QuizProgress(
        questionSetHash = questionSetHash,
        index = records.size,   // next unanswered question
        answers = records,
        currentStreak = currentStreak,
        longestStreak = longestStreak,
    )
}.getOrNull()

private fun QuizProgress.toSaved(now: Long) = SavedSession(
    questionSetHash = questionSetHash,
    answers = answers.map { SavedAnswer(it.questionId, it.selected, it.outcome.name) },
    currentStreak = currentStreak,
    longestStreak = longestStreak,
    updatedAt = now,
)
