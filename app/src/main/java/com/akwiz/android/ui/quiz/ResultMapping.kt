package com.akwiz.android.ui.quiz

import com.akwiz.android.domain.Outcome

/** One answered question, paired back with its text for the review list. */
data class ReviewItem(
    val questionNumber: Int,
    val questionText: String,
    val yourAnswer: String?,   // null when skipped
    val correctAnswer: String,
    val outcome: Outcome,
)

/**
 * Pairs each answer with its question. Possible for free because we persisted answer
 * records, not just a score (ADR-0004) — the review needs no extra data. Pure, so it
 * unit-tests without rendering.
 */
fun QuizUiState.Finished.review(): List<ReviewItem> =
    result.answers.mapIndexed { index, answer ->
        val question = set.questions.first { it.id == answer.questionId }
        ReviewItem(
            questionNumber = index + 1,
            questionText = question.text,
            yourAnswer = answer.selected?.let { question.options[it] },
            correctAnswer = question.options[question.correctIndex],
            outcome = answer.outcome,
        )
    }
