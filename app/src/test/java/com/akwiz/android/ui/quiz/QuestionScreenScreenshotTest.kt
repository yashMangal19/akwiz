package com.akwiz.android.ui.quiz

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.akwiz.android.domain.DataOrigin
import com.akwiz.android.domain.Question
import com.akwiz.android.domain.QuestionSet
import com.akwiz.android.ui.theme.AkwizTheme
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w360dp-h800dp-xhdpi")
class QuestionScreenScreenshotTest {

    @get:Rule val compose = createComposeRule()

    private val set = QuestionSet(
        questions = listOf(
            Question(1, "Which gesture is most consistent across Android and iOS notifications?",
                listOf("Swipe to dismiss", "Long-press menu", "Double-tap", "Pinch"), correctIndex = 0),
            Question(2, "Second", listOf("A", "B", "C", "D"), 0),
        ),
        contentHash = "h", origin = DataOrigin.Network,
    )

    private fun active(phase: AnswerPhase, streak: Int = 0) = QuizUiState.Active(
        set = set, index = 0, phase = phase, answers = emptyList(),
        currentStreak = streak, longestStreak = streak,
    )

    private fun shot(name: String, dark: Boolean = false, content: @Composable () -> Unit) {
        compose.setContent {
            AkwizTheme(darkTheme = dark) { Surface(Modifier.fillMaxSize()) { content() } }
        }
        compose.onRoot().captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test fun awaiting_light() = shot("screen_awaiting_light") {
        QuestionScreen(active(AnswerPhase.Awaiting, streak = 2), {}, {}, {})
    }

    @Test fun awaiting_dark() = shot("screen_awaiting_dark", dark = true) {
        QuestionScreen(active(AnswerPhase.Awaiting, streak = 2), {}, {}, {})
    }

    @Test fun revealed_wrong_light() = shot("screen_revealed_wrong_light") {
        QuestionScreen(active(AnswerPhase.Revealed(selected = 1)), {}, {}, {})
    }

    @Test fun revealed_correct_dark() = shot("screen_revealed_correct_dark", dark = true) {
        QuestionScreen(active(AnswerPhase.Revealed(selected = 0), streak = 3), {}, {}, {})
    }

    @Test fun error_light() = shot("screen_error_light") {
        ErrorScreen("Couldn't load the quiz.", canRetry = true, onRetry = {})
    }

    // Reveal with a screen reader on: manual "Next question" replaces the timer.
    @Test fun revealed_manual_advance() = shot("screen_revealed_manual_light") {
        QuestionScreen(
            active(AnswerPhase.Revealed(selected = 1)), {}, {}, {},
            manualAdvance = true,
        )
    }

    // 200% font — verify nothing clips.
    @Test fun awaiting_large_font() {
        RuntimeEnvironment.setQualifiers("+h1400dp")
        compose.setContent {
            val d = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(d.density, fontScale = 2f)) {
                AkwizTheme {
                    Surface(Modifier.fillMaxSize()) {
                        QuestionScreen(active(AnswerPhase.Awaiting, streak = 2), {}, {}, {})
                    }
                }
            }
        }
        compose.onRoot().captureRoboImage("src/test/screenshots/screen_awaiting_font2.png")
    }

    // Landscape — verify it stays centred and usable.
    @Test fun awaiting_landscape() {
        RuntimeEnvironment.setQualifiers("w740dp-h360dp-land-xhdpi")
        shot("screen_awaiting_landscape") {
            QuestionScreen(active(AnswerPhase.Awaiting, streak = 2), {}, {}, {})
        }
    }
}
