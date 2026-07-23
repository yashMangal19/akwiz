package com.akwiz.android.di

import android.content.Context
import com.akwiz.android.BuildConfig
import com.akwiz.android.data.DefaultQuizRepository
import com.akwiz.android.data.QuizRepository
import com.akwiz.android.data.local.AssetQuestionSource
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

    val quizRepository: QuizRepository = DefaultQuizRepository(
        api = api,
        bundled = AssetQuestionSource(app, json),
    )

    private companion object {
        const val BASE_URL = "https://gist.githubusercontent.com/"
        const val JSON_MEDIA_TYPE = "application/json"
        const val CALL_TIMEOUT_SECONDS = 5L
    }
}
