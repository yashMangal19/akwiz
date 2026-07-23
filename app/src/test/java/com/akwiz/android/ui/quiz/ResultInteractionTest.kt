package com.akwiz.android.ui.quiz

import androidx.compose.material3.Surface
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.akwiz.android.domain.AnswerRecord
import com.akwiz.android.domain.DataOrigin
import com.akwiz.android.domain.Question
import com.akwiz.android.domain.QuestionSet
import com.akwiz.android.domain.QuizResult
import com.akwiz.android.ui.theme.AkwizTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ResultInteractionTest {

    @get:Rule val compose = createComposeRule()

    private val finished = QuizUiState.Finished(
        set = QuestionSet(
            questions = listOf(
                Question(1, "First?", listOf("A", "B", "C", "D"), 0),
                Question(2, "Second?", listOf("A", "B", "C", "D"), 1),
            ),
            contentHash = "h", origin = DataOrigin.Network,
        ),
        result = QuizResult(
            answers = listOf(
                AnswerRecord.answered(1, selected = 0, isCorrect = true),
                AnswerRecord.answered(2, selected = 0, isCorrect = false),
            ),
            longestStreak = 1,
        ),
        isPersonalBest = false,
    )

    // Opening the review sheet exercises the LazyColumn-in-ModalBottomSheet path,
    // which no screenshot test covers (popups aren't captured). A layout crash here
    // would fail the click.
    @Test fun `opening the review sheet renders the rows`() {
        compose.setContent { AkwizTheme { Surface { ResultScreen(finished, onRestart = {}) } } }

        compose.onNodeWithText("Review answers").performClick()

        // Getting here means the sheet opened and its LazyColumn measured without a
        // crash; the header being displayed confirms the sheet content laid out.
        compose.onNodeWithText("Review").assertIsDisplayed()
    }

    @Test fun `restart is wired to the primary action`() {
        var restarted = false
        compose.setContent { AkwizTheme { Surface { ResultScreen(finished, onRestart = { restarted = true }) } } }

        compose.onNodeWithText("Play again").performClick()

        assert(restarted)
    }
}
