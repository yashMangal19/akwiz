package com.akwiz.android.data

import com.akwiz.android.domain.QuestionSet
import com.akwiz.android.domain.QuizProgress

interface QuizRepository {
    suspend fun loadQuestions(): Result<QuestionSet>

    /** Resumable progress for this set, or null if there's none valid to offer. */
    suspend fun readProgress(set: QuestionSet): QuizProgress?
    suspend fun saveProgress(progress: QuizProgress)
    suspend fun clearProgress()

    suspend fun bestStreak(): Int
    suspend fun recordBestStreak(value: Int)
}

class NoQuestionsAvailable : Exception("No usable questions from any source")
