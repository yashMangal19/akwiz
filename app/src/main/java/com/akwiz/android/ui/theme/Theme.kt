package com.akwiz.android.ui.theme

import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode

@Composable
fun AkwizTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // No dynamic colour: correct/incorrect need stable hues.
    val colorScheme = if (darkTheme) AkwizDarkColors else AkwizLightColors
    val quizColors = if (darkTheme) DarkQuizColors else LightQuizColors

    CompositionLocalProvider(LocalQuizColors provides quizColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AkwizTypography,
            shapes = AkwizShapes,
            content = content,
        )
    }
}

val MaterialTheme.quizColors: QuizColors
    @Composable @ReadOnlyComposable get() = LocalQuizColors.current

/** False when the user has turned off animations system-wide. */
@Composable
fun rememberAnimationsEnabled(): Boolean {
    val context = LocalContext.current
    val inspecting = LocalInspectionMode.current
    return remember(context, inspecting) {
        if (inspecting) return@remember true
        runCatching {
            Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1f,
            ) != 0f
        }.getOrDefault(true)
    }
}

/**
 * True when touch exploration (TalkBack) is active. Reactive — updates if the user
 * toggles it while the app is open. Drives the manual-advance flow, since a screen
 * reader can't hear the reveal before a 2-second auto-advance.
 */
@Composable
fun rememberTouchExplorationEnabled(): Boolean {
    val context = LocalContext.current
    if (LocalInspectionMode.current) return false

    val manager = remember(context) {
        context.getSystemService(AccessibilityManager::class.java)
    }
    val enabled by produceState(initialValue = manager?.isTouchExplorationEnabled == true, manager) {
        val m = manager ?: return@produceState
        val listener = AccessibilityManager.TouchExplorationStateChangeListener { value = it }
        m.addTouchExplorationStateChangeListener(listener)
        value = m.isTouchExplorationEnabled
        awaitDispose { m.removeTouchExplorationStateChangeListener(listener) }
    }
    return enabled
}
