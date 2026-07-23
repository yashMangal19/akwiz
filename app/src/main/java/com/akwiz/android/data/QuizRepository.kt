package com.akwiz.android.data

import com.akwiz.android.domain.QuestionSet

interface QuizRepository {
    suspend fun loadQuestions(): Result<QuestionSet>
}

class NoQuestionsAvailable : Exception("No usable questions from any source")
