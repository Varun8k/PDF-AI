package com.varun8k.PdfAi.api

import com.varun8k.PdfAi.model.ChatMessage
import com.varun8k.PdfAi.model.QuestionRequestBody
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface service {
    @Multipart
    @POST("/pdf")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part,
    ): Response<api>

    @POST("/question")
    suspend fun question(@Body text: QuestionRequestBody): Response<ChatMessage>

    @GET("/reset")
    suspend fun reset(): Response<api>
}