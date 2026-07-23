package com.akwiz.android.ui.quiz

import com.akwiz.android.domain.AnswerRecord
import com.akwiz.android.domain.DataOrigin
import com.akwiz.android.domain.Outcome
import com.akwiz.android.domain.Question
import com.akwiz.android.domain.QuestionSet
import com.akwiz.android.domain.QuizResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ResultMappingTest {

    private val set = QuestionSet(
        questions = listOf(
            Question(1, "First?", listOf("A0", "A1", "A2", "A3"), correctIndex = 0),
            Question(2, "Second?", listOf("B0", "B1", "B2", "B3"), correctIndex = 2),
            Question(3, "Third?", listOf("C0", "C1", "C2", "C3"), correctIndex = 1),
        ),
        contentHash = "h", origin = DataOrigin.Network,
    )

    private fun finished(vararg answers: AnswerRecord) = QuizUiState.Finished(
        set = set,
        result = QuizResult(answers.toList(), longestStreak = 1),
        isPersonalBest = false,
    )

    @Test fun `pairs a correct answer with its question`() {
        val item = finished(AnswerRecord.answered(1, selected = 0, isCorrect = true)).review().single()
        assertEquals(1, item.questionNumber)
        assertEquals("First?", item.questionText)
        assertEquals("A0", item.yourAnswer)
        assertEquals("A0", item.correctAnswer)
        assertEquals(Outcome.Correct, item.outcome)
    }

    @Test fun `a wrong answer shows both your pick and the correct one`() {
        val item = finished(AnswerRecord.answered(2, selected = 0, isCorrect = false)).review().single()
        assertEquals("B0", item.yourAnswer)      // what you picked
        assertEquals("B2", item.correctAnswer)   // what was right
        assertEquals(Outcome.Wrong, item.outcome)
    }

    @Test fun `a skipped answer has no your-answer but keeps the correct one`() {
        val item = finished(AnswerRecord.skipped(3)).review().single()
        assertNull(item.yourAnswer)
        assertEquals("C1", item.correctAnswer)
        assertEquals(Outcome.Skipped, item.outcome)
    }

    @Test fun `numbers the review in order`() {
        val review = finished(
            AnswerRecord.answered(1, 0, isCorrect = true),
            AnswerRecord.skipped(2),
            AnswerRecord.answered(3, 0, isCorrect = false),
        ).review()
        assertEquals(listOf(1, 2, 3), review.map { it.questionNumber })
        assertEquals(listOf("First?", "Second?", "Third?"), review.map { it.questionText })
    }
}
