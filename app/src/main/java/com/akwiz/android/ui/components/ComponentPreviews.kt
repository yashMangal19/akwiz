package com.akwiz.android.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.akwiz.android.ui.theme.AkwizTheme
import com.akwiz.android.ui.theme.Spacing

@Composable
internal fun ComponentGallery() {
    AkwizTheme {
        Surface {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Text("Option states")
                OptionCard("Detecting accidental shakes", "A", OptionCardState.Awaiting, {})
                OptionCard("Detecting accidental shakes", "A", OptionCardState.CorrectChosen, {})
                OptionCard("Battery drain due to sensors", "B", OptionCardState.WrongChosen, {})
                OptionCard("Hidden performance menu", "C", OptionCardState.Correct, {})
                OptionCard("System UI tuner", "D", OptionCardState.Muted, {})

                HorizontalDivider()

                Text("Streak — dormant, building, hot")
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    StreakBadge(streak = 1)
                    StreakBadge(streak = 2)
                    StreakBadge(streak = 5)
                }

                HorizontalDivider()

                Text("Progress header")
                ProgressHeader(questionNumber = 5, total = 10, progress = 0.5f, streak = 3)

                HorizontalDivider()

                Text("Score ring")
                ScoreRing(correct = 8, total = 10)
            }
        }
    }
}

@Preview(name = "Light", showBackground = true, heightDp = 900)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true, heightDp = 900)
@Preview(name = "Large font", fontScale = 2f, showBackground = true, heightDp = 1400)
@Composable
private fun GalleryPreview() {
    ComponentGallery()
}
