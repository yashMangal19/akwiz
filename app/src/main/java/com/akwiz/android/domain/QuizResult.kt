package com.akwiz.android.domain

/**
 * Outcome of a completed run. Counts are folds over [answers] so they can't drift;
 * [longestStreak] is kept because it's a maximum over the run, not a property of
 * the final state.
 */
data class QuizResult(
    val answers: List<AnswerRecord>,
    val longestStreak: Int,
) {
    init { require(longestStreak >= 0) { "longestStreak cannot be negative" } }

    val total: Int get() = answers.size
    val correct: Int get() = answers.count { it.outcome == Outcome.Correct }
    val wrong: Int get() = answers.count { it.outcome == Outcome.Wrong }
    val skipped: Int get() = answers.count { it.outcome == Outcome.Skipped }
}
