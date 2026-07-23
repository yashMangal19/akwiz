package com.akwiz.android

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.akwiz.android.ui.components.ConfettiBurst
import com.akwiz.android.ui.components.IgnitionRing
import com.akwiz.android.ui.components.PersonalBestCelebration
import com.akwiz.android.ui.theme.AkwizTheme
import com.akwiz.android.ui.theme.Motion
import com.akwiz.android.ui.theme.Spacing
import com.akwiz.android.ui.theme.quizColors
import com.akwiz.android.ui.theme.rememberAnimationsEnabled
import kotlinx.coroutines.delay

// Temporary screen for checking the celebration animations on a device.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AkwizTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { insets ->
                    CelebrationPlayground(Modifier.padding(insets))
                }
            }
        }
    }
}

@Composable
private fun CelebrationPlayground(modifier: Modifier = Modifier) {
    var confetti by remember { mutableStateOf(false) }
    var ignition by remember { mutableStateOf(false) }
    var personalBest by remember { mutableStateOf(false) }
    val animationsOn = rememberAnimationsEnabled()

    // Reset each trigger once it has run so it can fire again.
    LaunchedEffect(confetti) {
        if (confetti) { delay(Motion.CELEBRATION.toLong() + 100); confetti = false }
    }
    LaunchedEffect(ignition) {
        if (ignition) { delay(Motion.EMPHATIC.toLong() + 100); ignition = false }
    }
    LaunchedEffect(personalBest) {
        if (personalBest) { delay(2_000); personalBest = false }
    }

    Box(modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.md, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Akwiz", style = MaterialTheme.typography.displayMedium)
            Text(
                "Motion harness — Rosé Pine · IBM Plex",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                if (animationsOn) "System animations: on"
                else "System animations: off — celebrations suppressed",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.quizColors.streakActive,
            )

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                Button({ confetti = true }, Modifier.weight(1f)) { Text("Confetti") }
                OutlinedButton({ ignition = true }, Modifier.weight(1f)) { Text("Ignite") }
            }
            Button({ personalBest = true }, Modifier.fillMaxWidth()) { Text("Personal best") }
        }

        IgnitionRing(
            playing = ignition,
            color = MaterialTheme.quizColors.streakActive,
            modifier = Modifier.fillMaxSize(),
            animate = animationsOn,
        )
        ConfettiBurst(
            playing = confetti,
            colors = MaterialTheme.quizColors.celebration,
            modifier = Modifier.fillMaxSize(),
            animate = animationsOn,
        )
        PersonalBestCelebration(
            playing = personalBest,
            modifier = Modifier.fillMaxSize(),
            animate = animationsOn,
        )
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun PlaygroundPreview() {
    AkwizTheme { CelebrationPlayground() }
}
