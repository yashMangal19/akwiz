package com.akwiz.android.ui.quiz

/**
 * One-shot signals — haptics and celebrations. Kept out of the state so they don't
 * replay when the screen is recreated on rotation.
 */
sealed interface QuizEffect {
    data object AnswerCorrect : QuizEffect
    data object AnswerWrong : QuizEffect
    data object StreakIgnited : QuizEffect
    data object PersonalBest : QuizEffect
}
