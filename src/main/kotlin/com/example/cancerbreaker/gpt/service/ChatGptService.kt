package com.example.cancerbreaker.gpt.service

import com.example.cancerbreaker.board.repository.BoardRepository
import com.example.cancerbreaker.gpt.dto.GptRequestDto
import com.example.cancerbreaker.gpt.dto.GptResponseDto
import com.example.cancerbreaker.gpt.dto.Message
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate

@Service
class ChatGptService(
    private val restTemplate: RestTemplate,
    private val webClient: WebClient,
    private val boardRepository: BoardRepository
) {

    @Value("\${api.openai.api-key}")
    private lateinit var apiKey: String

    @Value("\${api.openai.model}")
    private lateinit var model: String

    @Value("\${api.openai.url}")
    private lateinit var apiUrl: String

    suspend fun askGPT(prompt: String): String {
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
            .awaitFirstOrNull() ?: "No response"
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

    // 동기 방식(테스트용)
    fun synAskGPT(prompt: String): String {
        val request = GptRequestDto(
            model = model,
            messages = listOf(Message(role = "user", content = prompt))
        )

        val headers = HttpHeaders().apply {
            set("Authorization", "Bearer $apiKey")
            setContentType(MediaType.APPLICATION_JSON)
        }
        val httpEntity = HttpEntity(request, headers)

        val response = restTemplate.postForEntity(apiUrl, httpEntity, GptResponseDto::class.java)
        return response.body?.choices?.firstOrNull()?.message?.content ?: "No response"
    }

    fun SynBoardSummary(boardId: Long): Result<String> {
        return runCatching {
            val board = boardRepository.findByIdOrNull(boardId)
                ?: throw IllegalArgumentException("게시글을 찾을 수 없습니다.")
            synAskGPT("${board.content} 요약해줘")
        }
    }
}