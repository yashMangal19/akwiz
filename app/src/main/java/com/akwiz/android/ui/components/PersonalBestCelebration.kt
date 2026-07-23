package com.akwiz.android.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.semantics.clearAndSetSemantics
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.akwiz.android.ui.theme.quizColors

private const val PERSONAL_BEST_ASSET = "anim/personal_best.json"

/**
 * Celebration for a new best streak.
 *
 * Falls back to [ConfettiBurst] when the Lottie asset is missing, so the animation
 * file stays optional.
 */
@Composable
fun PersonalBestCelebration(
    playing: Boolean,
    modifier: Modifier = Modifier,
    animate: Boolean = true,
) {
    if (!animate) return

    val palette = MaterialTheme.quizColors.celebration
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset(PERSONAL_BEST_ASSET),
    )

    Box(modifier = modifier.clearAndSetSemantics { }) {
        if (composition == null) {
            ConfettiBurst(
                playing = playing,
                colors = palette,
                modifier = Modifier.fillMaxSize(),
                particleCount = 90,
                seed = 7,
            )
        } else {
            LottieAnimation(
                composition = composition,
                iterations = 1,
                isPlaying = playing,
                restartOnPlay = true,
                modifier = Modifier.fillMaxSize(),
                dynamicProperties = rememberPaletteTint(palette),
            )
        }
    }
}

// Keypaths are wildcarded since layer names come from the imported file; a mismatch
// just leaves the original colours in place.
@Composable
private fun rememberPaletteTint(palette: List<Color>): LottieDynamicProperties {
    val a = palette.getOrElse(0) { Color.Unspecified }
    val b = palette.getOrElse(1) { a }
    val c = palette.getOrElse(2) { a }

    return rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR_FILTER,
            value = ColorFilter.tint(a),
            keyPath = arrayOf("**", "Group 1", "**"),
        ),
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR_FILTER,
            value = ColorFilter.tint(b),
            keyPath = arrayOf("**", "Group 2", "**"),
        ),
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR_FILTER,
            value = ColorFilter.tint(c),
            keyPath = arrayOf("**", "Group 3", "**"),
        ),
    )
}
