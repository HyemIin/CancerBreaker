package com.example.cancerbreaker.gpt.controller

import com.example.cancerbreaker.gpt.service.ChatGptService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/gpt")
class ChatGPTController(
    private val chatGptService: ChatGptService
) {
    // gpt 질의
    @GetMapping("/ask")
    suspend fun askGpt(@RequestParam prompt: String): String {
        return chatGptService.askGPT(prompt)
    }

    // 게시글 요약
    @GetMapping("/summary")
    fun summaryBoardByGPT(@RequestParam boardId: Long): Mono<String> {
        return chatGptService.boardSummary(boardId)
    }
}