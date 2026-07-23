package com.akwiz.android.data.local

import androidx.datastore.core.DataStore
import com.akwiz.android.data.remote.QuestionDto
import kotlinx.coroutines.flow.first

/** The re-derivable half of storage: the last question set we successfully fetched. */
internal interface QuestionCacheStore {
    suspend fun read(): List<QuestionDto>
    suspend fun write(questions: List<QuestionDto>)
}

internal class DataStoreQuestionCache(
    private val store: DataStore<QuestionCache>,
) : QuestionCacheStore {

    override suspend fun read(): List<QuestionDto> = store.data.first().questions

    override suspend fun write(questions: List<QuestionDto>) {
        store.updateData { it.copy(questions = questions) }
    }
}
