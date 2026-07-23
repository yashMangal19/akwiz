package com.akwiz.android.data

import com.akwiz.android.data.local.BundledQuestionSource
import com.akwiz.android.data.remote.QuestionDto
import com.akwiz.android.data.remote.QuizApi
import com.akwiz.android.data.remote.toDomain
import com.akwiz.android.domain.DataOrigin
import com.akwiz.android.domain.QuestionSet
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Tries the network, then the copy bundled in the app. Callers get questions and
 * never learn which one answered, beyond the origin tag.
 *
 * The set is fetched once before a quiz starts, so it can't change underneath a
 * quiz in progress.
 */
internal class DefaultQuizRepository(
    private val api: QuizApi,
    private val bundled: BundledQuestionSource,
    private val io: CoroutineDispatcher = Dispatchers.IO,
) : QuizRepository {

    override suspend fun loadQuestions(): Result<QuestionSet> {
        fromNetwork()?.let { return Result.success(it) }
        fromBundled()?.let { return Result.success(it) }
        return Result.failure(NoQuestionsAvailable())
    }

    private suspend fun fromNetwork(): QuestionSet? = attempt {
        api.getQuestions().toQuestionSet(DataOrigin.Network)
    }

    private suspend fun fromBundled(): QuestionSet? = attempt {
        withContext(io) { bundled.read() }.toQuestionSet(DataOrigin.Bundled)
    }

    /**
     * runCatching would also swallow CancellationException and quietly break
     * structured concurrency, so cancellation is rethrown explicitly.
     */
    private suspend fun attempt(block: suspend () -> QuestionSet?): QuestionSet? =
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }
}

/** Null when nothing survived validation — treated the same as the source failing. */
private fun List<QuestionDto>.toQuestionSet(origin: DataOrigin): QuestionSet? {
    val questions = toDomain()
    if (questions.isEmpty()) return null
    return QuestionSet(
        questions = questions,
        contentHash = contentHashOf(questions),
        origin = origin,
    )
}
