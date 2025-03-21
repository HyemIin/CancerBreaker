package com.example.cancerbreaker.gpt.dto

data class GptRequestDto(
    val model: String,
    val messages: List<Message>,
    val temperature: Double = 0.7

)

data class Message(
    val role: String,
    val content: String
)
