package com.akwiz.android.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.akwiz.android.ui.theme.AkwizTheme
import com.akwiz.android.ui.theme.Spacing
import com.github.takahirom.roborazzi.RoborazziRule
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w360dp-h800dp-xhdpi")
class ComponentScreenshotTest {

    @get:Rule val compose = createComposeRule()

    private fun shot(name: String, dark: Boolean = false, content: @Composable () -> Unit) {
        compose.setContent {
            AkwizTheme(darkTheme = dark) {
                Surface {
                    androidx.compose.foundation.layout.Box(Modifier.padding(Spacing.md)) { content() }
                }
            }
        }
        compose.onRoot().captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test fun gallery_light() = shot("gallery_light") { ComponentGallery() }
    @Test fun gallery_dark() = shot("gallery_dark", dark = true) { ComponentGallery() }

    @Test fun option_states_light() = shot("option_states_light") { OptionStates() }
    @Test fun option_states_dark() = shot("option_states_dark", dark = true) { OptionStates() }

    @Test fun score_ring_light() = shot("score_ring_light") { ScoreRing(correct = 8, total = 10) }
    @Test fun score_ring_dark() = shot("score_ring_dark", dark = true) { ScoreRing(correct = 8, total = 10) }

    @Test fun streaks_light() = shot("streaks_light") { StreakRow() }
}

@Composable
private fun StreakRow() {
    androidx.compose.foundation.layout.Column(
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(Spacing.sm),
    ) {
        StreakBadge(streak = 0)
        StreakBadge(streak = 1)
        StreakBadge(streak = 2)
        StreakBadge(streak = 3)
        StreakBadge(streak = 7)
    }
}

@Composable
private fun OptionStates() {
    androidx.compose.foundation.layout.Column(
        Modifier.width(320.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(Spacing.sm),
    ) {
        OptionCard("Detecting accidental shakes", "A", OptionCardState.Awaiting, {}, Modifier.fillMaxWidth())
        OptionCard("Detecting accidental shakes", "A", OptionCardState.CorrectChosen, {}, Modifier.fillMaxWidth())
        OptionCard("Battery drain due to sensors", "B", OptionCardState.WrongChosen, {}, Modifier.fillMaxWidth())
        OptionCard("Hidden performance menu", "C", OptionCardState.Correct, {}, Modifier.fillMaxWidth())
        OptionCard("System UI tuner", "D", OptionCardState.Muted, {}, Modifier.fillMaxWidth())
    }
}
