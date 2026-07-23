package com.akwiz.android.data.local

import android.content.Context
import com.akwiz.android.data.remote.QuestionDto
import kotlinx.serialization.json.Json

/** Separate from the implementation so the repository can be tested without a Context. */
internal interface BundledQuestionSource {
    fun read(): List<QuestionDto>
}

internal class AssetQuestionSource(
    private val context: Context,
    private val json: Json,
) : BundledQuestionSource {

    override fun read(): List<QuestionDto> =
        context.assets.open(ASSET_PATH).bufferedReader().use { json.decodeFromString(it.readText()) }

    private companion object {
        const val ASSET_PATH = "questions.json"
    }
}
