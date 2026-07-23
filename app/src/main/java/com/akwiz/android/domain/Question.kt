package com.akwiz.android.domain

/**
 * A single quiz question. The option count is not fixed — the source happens to
 * send four, but nothing here depends on that.
 */
data class Question(
    val id: Int,
    val text: String,
    val options: List<String>,
    val correctIndex: Int,
) {
    init {
        require(options.size >= 2) { "A question needs at least two options, got ${options.size}" }
        require(correctIndex in options.indices) {
            "correctIndex $correctIndex is out of range for ${options.size} options"
        }
    }

    val correctOption: String get() = options[correctIndex]
}
