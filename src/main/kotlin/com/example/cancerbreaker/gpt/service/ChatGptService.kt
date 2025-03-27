package com.example.cancerbreaker.gpt.service

import com.example.cancerbreaker.board.repository.BoardRepository
import com.example.cancerbreaker.gpt.dto.GptRequestDto
import com.example.cancerbreaker.gpt.dto.GptResponseDto
import com.example.cancerbreaker.gpt.dto.Message
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class ChatGptService(
    private val webClient: WebClient,
    private val boardRepository: BoardRepository
) {

    @Value("\${api.openai.api-key}")
    private lateinit var apiKey: String

    @Value("\${api.openai.model}")
    private lateinit var model: String

    @Value("\${api.openai.url}")
    private lateinit var apiUrl: String

    fun askGPT(prompt: String): Mono<String> {
        val request = GptRequestDto(
            model = model,
            messages = listOf(Message(role = "user", content = prompt))
        )

        return webClient.post()
            .uri(apiUrl)
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(GptResponseDto::class.java)
            .map { it.choices.firstOrNull()?.message?.content ?: "No response" }
    }

    fun boardSummary(boardId: Long): Mono<String> {
        val context = boardRepository.findById(boardId).get().content

        val request = GptRequestDto(
            model = model,
            messages = listOf(Message(role = "user", content = context + " 요약해줘"))
        )

        return webClient.post()
            .uri(apiUrl)
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(GptResponseDto::class.java)
            .map { it.choices.firstOrNull()?.message?.content ?: "No response" }
    }
}