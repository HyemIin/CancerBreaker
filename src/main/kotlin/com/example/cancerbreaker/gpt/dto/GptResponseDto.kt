package com.example.cancerbreaker.gpt.dto

data class GptResponseDto(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)