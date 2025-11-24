package com.giotech.nlpproject

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface NLPApi {


    @GET("/summarize")
    @Headers("Content-Type: text/plain")
    suspend fun processText(
        @Query("text") text: String
    ): Response<NLPResponse>

}
