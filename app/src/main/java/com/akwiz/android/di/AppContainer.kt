package com.akwiz.android.di

import android.content.Context
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import com.akwiz.android.BuildConfig
import com.akwiz.android.data.Clock
import com.akwiz.android.data.DefaultQuizRepository
import com.akwiz.android.data.QuizRepository
import com.akwiz.android.data.local.AssetQuestionSource
import com.akwiz.android.data.local.DataStorePlayerStore
import com.akwiz.android.data.local.DataStoreQuestionCache
import com.akwiz.android.data.local.JsonSerializer
import com.akwiz.android.data.local.PlayerState
import com.akwiz.android.data.local.QuestionCache
import com.akwiz.android.data.remote.QuizApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/** The whole object graph. Small enough that a DI library would only hide it. */
class AppContainer(context: Context) {

    private val app = context.applicationContext

    private val json = Json { ignoreUnknownKeys = true }

    // callTimeout bounds the whole call. connect and read timeouts are each 10s by
    // default and compose, which would leave the splash up for far longer.
    private val client = OkHttpClient.Builder()
        .callTimeout(CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            },
        )
        .build()

    private val api: QuizApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(json.asConverterFactory(JSON_MEDIA_TYPE.toMediaType()))
        .build()
        .create(QuizApi::class.java)

    // Two stores, split on re-derivable vs irreplaceable. A corrupt cache is replaced
    // without touching the player's session or best streak.
    private val cacheStore = DataStoreQuestionCache(
        DataStoreFactory.create(
            serializer = JsonSerializer(json, QuestionCache.serializer(), QuestionCache()),
            corruptionHandler = ReplaceFileCorruptionHandler { QuestionCache() },
        ) { app.dataStoreFile(CACHE_FILE) },
    )

    private val playerStore = DataStorePlayerStore(
        DataStoreFactory.create(
            serializer = JsonSerializer(json, PlayerState.serializer(), PlayerState()),
            corruptionHandler = ReplaceFileCorruptionHandler { PlayerState() },
        ) { app.dataStoreFile(PLAYER_FILE) },
    )

    val quizRepository: QuizRepository = DefaultQuizRepository(
        api = api,
        cache = cacheStore,
        bundled = AssetQuestionSource(app, json),
        player = playerStore,
        clock = Clock.System,
    )

    private companion object {
        const val BASE_URL = "https://gist.githubusercontent.com/"
        const val JSON_MEDIA_TYPE = "application/json"
        const val CALL_TIMEOUT_SECONDS = 5L
        const val CACHE_FILE = "question_cache.json"
        const val PLAYER_FILE = "player.json"
    }
}
