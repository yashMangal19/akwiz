package com.akwiz.android.data.remote

import retrofit2.http.GET

internal interface QuizApi {
    /** Returns a bare JSON array — no envelope. */
    @GET("dr-samrat/53846277a8fcb034e482906ccc0d12b2/raw")
    suspend fun getQuestions(): List<QuestionDto>
}
