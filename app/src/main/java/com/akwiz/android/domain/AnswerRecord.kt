package com.akwiz.android.domain

/** A skip is neither correct nor wrong, so it needs its own case. */
enum class Outcome { Correct, Wrong, Skipped }

data class AnswerRecord(
    val questionId: Int,
    val selected: Int?,
    val outcome: Outcome,
) {
    init {
        require((outcome == Outcome.Skipped) == (selected == null)) {
            "A skipped answer has no selection, an answered one must have exactly one"
        }
    }

    companion object {
        fun answered(questionId: Int, selected: Int, isCorrect: Boolean) = AnswerRecord(
            questionId = questionId,
            selected = selected,
            outcome = if (isCorrect) Outcome.Correct else Outcome.Wrong,
        )

        fun skipped(questionId: Int) = AnswerRecord(
            questionId = questionId,
            selected = null,
            outcome = Outcome.Skipped,
        )
    }
}
