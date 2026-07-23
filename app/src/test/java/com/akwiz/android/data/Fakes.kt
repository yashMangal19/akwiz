package com.akwiz.android.data

import com.akwiz.android.data.local.BundledQuestionSource
import com.akwiz.android.data.local.PlayerStore
import com.akwiz.android.data.local.QuestionCacheStore
import com.akwiz.android.data.local.SavedSession
import com.akwiz.android.data.remote.QuestionDto
import com.akwiz.android.data.remote.QuizApi
import java.io.IOException

internal class FakeApi(val answer: () -> List<QuestionDto>) : QuizApi {
    override suspend fun getQuestions(): List<QuestionDto> = answer()
}

internal class FakeBundled(val answer: () -> List<QuestionDto>) : BundledQuestionSource {
    override fun read(): List<QuestionDto> = answer()
}

internal class FakeCacheStore(var stored: List<QuestionDto> = emptyList()) : QuestionCacheStore {
    var writes = 0
    override suspend fun read(): List<QuestionDto> = stored
    override suspend fun write(questions: List<QuestionDto>) { stored = questions; writes++ }
}

internal class FakePlayerStore(
    var session: SavedSession? = null,
    var best: Int = 0,
) : PlayerStore {
    override suspend fun readSession(): SavedSession? = session
    override suspend fun writeSession(session: SavedSession) { this.session = session }
    override suspend fun clearSession() { session = null }
    override suspend fun bestStreak(): Int = best
    override suspend fun recordBestStreak(value: Int) { best = maxOf(best, value) }
}

internal class FakeClock(var current: Long = 0L) : Clock {
    override fun now(): Long = current
}

internal fun failing(): () -> List<QuestionDto> = { throw IOException("unavailable") }
