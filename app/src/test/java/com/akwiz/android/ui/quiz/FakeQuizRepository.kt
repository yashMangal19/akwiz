package com.akwiz.android.ui.quiz

import com.akwiz.android.data.QuizRepository
import com.akwiz.android.domain.DataOrigin
import com.akwiz.android.domain.Question
import com.akwiz.android.domain.QuestionSet
import com.akwiz.android.domain.QuizProgress

internal class FakeQuizRepository(
    private val loadResult: Result<QuestionSet>,
    var savedProgress: QuizProgress? = null,
    var storedBest: Int = 0,
) : QuizRepository {

    val saves = mutableListOf<QuizProgress>()
    var clears = 0
    var recordedBest: Int? = null

    override suspend fun loadQuestions(): Result<QuestionSet> = loadResult

    override suspend fun readProgress(set: QuestionSet): QuizProgress? =
        savedProgress?.takeIf { it.questionSetHash == set.contentHash }

    override suspend fun saveProgress(progress: QuizProgress) {
        saves += progress
        savedProgress = progress
    }

    override suspend fun clearProgress() {
        clears++
        savedProgress = null
    }

    override suspend fun bestStreak(): Int = storedBest

    override suspend fun recordBestStreak(value: Int) {
        recordedBest = value
        storedBest = maxOf(storedBest, value)
    }
}

internal fun testSet(size: Int = 10): QuestionSet {
    val questions = (1..size).map { id ->
        // Correct answer is always index 0, so "answer correctly" == selectOption(0).
        Question(id = id, text = "Question $id", options = listOf("A", "B", "C", "D"), correctIndex = 0)
    }
    return QuestionSet(questions, contentHash = "hash-$size", origin = DataOrigin.Network)
}
