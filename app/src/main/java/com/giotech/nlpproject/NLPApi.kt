package com.giotech.nlpproject

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface NLPApi {


    @POST("/process-nlp")
    @Headers("Content-Type: text/plain")
    suspend fun processText(
        @Body text: String
    ): Response<NLPResponse>


    @Multipart
    @POST("/process-nlp")
    suspend fun processPdf(
        @Part pdf: MultipartBody.Part
    ): Response<NLPResponse>


}
