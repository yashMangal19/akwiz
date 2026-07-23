package com.akwiz.android.ui.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.akwiz.android.domain.Outcome
import com.akwiz.android.ui.components.ReviewRow
import com.akwiz.android.ui.theme.Spacing
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.akwiz.android.domain.AnswerRecord
import com.akwiz.android.domain.DataOrigin
import com.akwiz.android.domain.Question
import com.akwiz.android.domain.QuestionSet
import com.akwiz.android.domain.QuizResult
import com.akwiz.android.ui.theme.AkwizTheme
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w360dp-h1100dp-xhdpi")
class ResultScreenScreenshotTest {

    @get:Rule val compose = createComposeRule()

    private val questions = listOf(
        Question(1, "What hidden feature do recent Android versions reveal?",
            listOf("Flappy Bird game", "Virtual pet", "Performance menu", "System UI tuner"), 0),
        Question(2, "The biggest challenge with shake-to-undo?",
            listOf("Accidental shakes", "Battery drain", "No motion API", "Illegal"), 0),
        Question(3, "Permission to draw a floating overlay?",
            listOf("SYSTEM_ALERT_WINDOW", "ACCESS_OVERLAY_UI", "FOREGROUND_SERVICE", "BIND_NLS"), 0),
    )
    private val set = QuestionSet(questions, "h", DataOrigin.Network)

    private fun finished(personalBest: Boolean) = QuizUiState.Finished(
        set = set,
        result = QuizResult(
            answers = listOf(
                AnswerRecord.answered(1, selected = 0, isCorrect = true),
                AnswerRecord.answered(2, selected = 1, isCorrect = false),
                AnswerRecord.skipped(3),
            ),
            longestStreak = 4,
        ),
        isPersonalBest = personalBest,
    )

    private fun shot(name: String, dark: Boolean = false, content: @Composable () -> Unit) {
        compose.setContent {
            AkwizTheme(darkTheme = dark) { Surface(Modifier.fillMaxSize()) { content() } }
        }
        compose.onRoot().captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test fun result_personal_best_light() =
        shot("result_best_light") { ResultScreen(finished(personalBest = true), {}) }

    @Test fun result_dark() =
        shot("result_dark", dark = true) { ResultScreen(finished(personalBest = false), {}) }

    @Test fun review_rows_light() = shot("review_rows_light") {
        Column(
            Modifier.padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            ReviewRow(1, "Which gesture is most consistent across Android and iOS?",
                yourAnswer = "Swipe to dismiss", correctAnswer = "Swipe to dismiss", outcome = Outcome.Correct)
            ReviewRow(2, "The biggest challenge with shake-to-undo?",
                yourAnswer = "Battery drain", correctAnswer = "Accidental shakes", outcome = Outcome.Wrong)
            ReviewRow(3, "Permission to draw a floating overlay?",
                yourAnswer = null, correctAnswer = "SYSTEM_ALERT_WINDOW", outcome = Outcome.Skipped)
        }
    }

    @Test fun loading_light() = shot("loading_light") { LoadingScreen() }
    @Test fun loading_dark() = shot("loading_dark", dark = true) { LoadingScreen() }

    @Test fun resume_prompt_light() = shot("resume_light") {
        // Partial progress: answered one, on question 2 of 3.
        ResumePromptScreen(
            QuizUiState.ResumePrompt(
                set,
                com.akwiz.android.domain.QuizProgress(
                    questionSetHash = "h", index = 1, answers = emptyList(),
                    currentStreak = 2, longestStreak = 2,
                ),
            ),
            onResume = {}, onStartOver = {},
        )
    }
}
