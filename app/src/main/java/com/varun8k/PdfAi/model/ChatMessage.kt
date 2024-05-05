package com.varun8k.PdfAi.model

data class ChatMessage(
    val answer: String,
    val isUserMessage:Boolean=false
)
data class QuestionRequestBody(val question: String)

