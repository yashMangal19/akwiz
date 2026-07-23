package com.akwiz.android.ui.quiz

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

/**
 * Distinct feedback for correct / wrong / streak.
 *
 * Goes through the View rather than the Vibrator, so it needs no permission and
 * honours the system touch-feedback setting for free — it simply no-ops when the
 * user has turned haptics off.
 */
class Haptics(private val view: View) {
    fun correct() = play(HapticFeedbackConstants.CONFIRM, HapticFeedbackConstants.CONTEXT_CLICK)
    fun wrong() = play(HapticFeedbackConstants.REJECT, HapticFeedbackConstants.LONG_PRESS)
    fun streak() = play(HapticFeedbackConstants.GESTURE_END, HapticFeedbackConstants.LONG_PRESS)

    private fun play(api30: Int, fallback: Int) {
        val constant = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) api30 else fallback
        view.performHapticFeedback(constant)
    }
}

@Composable
fun rememberHaptics(): Haptics {
    val view = LocalView.current
    return remember(view) { Haptics(view) }
}
