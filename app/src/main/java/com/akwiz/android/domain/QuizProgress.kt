package com.akwiz.android.domain

/**
 * Resumable progress through a quiz. A domain type, so persistence models never
 * reach the state layer — the store maps to and from its own on-disk shape.
 */
data class QuizProgress(
    val questionSetHash: String,
    val index: Int,
    val answers: List<AnswerRecord>,
    val currentStreak: Int,
    val longestStreak: Int,
)
