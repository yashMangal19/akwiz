package com.akwiz.android.data.remote

import kotlinx.serialization.Serializable

/** Field names match the endpoint exactly. The domain model renames them. */
@Serializable
internal data class QuestionDto(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctOptionIndex: Int,
)
