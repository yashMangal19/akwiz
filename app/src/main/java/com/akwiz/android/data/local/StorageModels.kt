package com.akwiz.android.data.local

import com.akwiz.android.data.remote.QuestionDto
import kotlinx.serialization.Serializable

/**
 * Persisted shapes. Kept separate from the domain models so a domain rename can't
 * break data already on a user's disk — the same reason the wire DTO is separate.
 *
 * Two roots, in two files, split on whether the data is re-derivable. The question
 * cache can always be re-fetched; the session and best streak can't. Keeping them
 * apart means a corrupt cache can't take the best streak down with it.
 */

@Serializable
internal data class QuestionCache(
    val schemaVersion: Int = 1,
    val questions: List<QuestionDto> = emptyList(),
)

@Serializable
internal data class PlayerState(
    val schemaVersion: Int = 1,
    val session: SavedSession? = null,
    val bestStreak: Int = 0,
)

@Serializable
internal data class SavedSession(
    val questionSetHash: String,
    // No index — the resume point is answers.size, so storing it too would be two
    // sources of truth that can drift.
    val answers: List<SavedAnswer>,
    val currentStreak: Int,
    val longestStreak: Int,
    val updatedAt: Long,
)

@Serializable
internal data class SavedAnswer(
    val questionId: Int,
    val selected: Int?,
    // Stored as the enum name rather than its ordinal, so reordering Outcome can't
    // silently reinterpret old data.
    val outcome: String,
)
