package com.akwiz.android.data.remote

import com.akwiz.android.domain.Question

/**
 * Converts a wire object into a domain one, or null if it can't be trusted.
 *
 * Checks are explicit rather than relying on Question's own init block, so an
 * expected data problem never arrives as an exception. The init block stays as a
 * backstop for bugs on our side.
 */
internal fun QuestionDto.toDomainOrNull(): Question? {
    val text = question.trim()
    val trimmed = options.map { it.trim() }

    if (text.isBlank()) return null
    if (trimmed.size < 2) return null
    if (trimmed.any { it.isBlank() }) return null
    if (correctOptionIndex !in trimmed.indices) return null

    return Question(
        id = id,
        text = text,
        options = trimmed,
        correctIndex = correctOptionIndex,
    )
}

/** Drops anything malformed — one bad row shouldn't cost the whole quiz. */
internal fun List<QuestionDto>.toDomain(): List<Question> = mapNotNull { it.toDomainOrNull() }
