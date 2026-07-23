package com.akwiz.android.domain

enum class DataOrigin { Network, Cache, Bundled }

/**
 * Questions together with the hash of the payload they came from. The hash travels
 * with the list so a saved session can be checked against the set it belongs to.
 */
data class QuestionSet(
    val questions: List<Question>,
    val contentHash: String,
    val origin: DataOrigin,
) {
    init {
        require(questions.isNotEmpty()) { "A question set cannot be empty" }
        require(contentHash.isNotBlank()) { "A question set needs a content hash" }
    }

    val size: Int get() = questions.size
}
