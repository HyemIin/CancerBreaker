package com.example.cancerbreaker.board.controller

import com.example.cancerbreaker.board.dto.request.BoardCreateRequest
import com.example.cancerbreaker.board.dto.request.BoardEditRequest
import com.example.cancerbreaker.board.dto.response.BoardCreateResponse
import com.example.cancerbreaker.board.dto.response.BoardGetResponse
import com.example.cancerbreaker.board.entity.Board
import com.example.cancerbreaker.board.entity.BoardCategory
import com.example.cancerbreaker.board.service.BoardService
import com.example.cancerbreaker.global.aop.SessionCheck
import com.example.cancerbreaker.global.util.ApiResponse
import com.example.cancerbreaker.global.util.Error
import com.example.cancerbreaker.global.util.Success
import com.example.cancerbreaker.global.util.handleResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/boards")
class BoardController(private val boardService: BoardService) {

    // 게시글 생성
    @PostMapping
    @SessionCheck
    fun createBoard(
        @RequestBody request: BoardCreateRequest
    ): ResponseEntity<ApiResponse<BoardCreateResponse>> {
        val response = boardService.createBoard(request)
        handleResponse(response)
        return when (response) {
            is Success -> ResponseEntity.ok(response)
            is Error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }
    }


    // 전체 게시글 불러오기
    @GetMapping("/all")
    fun getAllBoardLst(): ResponseEntity<ApiResponse<List<BoardGetResponse>>> {
        val response = boardService.getAllBoardList()
        handleResponse(response)
        return when (response) {
            is Success -> ResponseEntity.ok(response)
            is Error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }
    }

    // 카테고리별 게시글 불러오기
    @GetMapping
    fun getBoardList(@RequestParam category: BoardCategory): ResponseEntity<ApiResponse<List<BoardGetResponse>>> {
        val response = boardService.getBoardListByCategory(category)
        handleResponse(response)
        return when (response) {
            is Success -> ResponseEntity.ok(response)
            is Error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }
    }

    // 특정 게시글 불러오기
    @GetMapping("/{boardId}")
    fun getOneBoard(@PathVariable boardId: Long): ResponseEntity<ApiResponse<BoardGetResponse?>> {
        val response = boardService.getBoardByBoardId(boardId)?.let {
            Success(it, "Board retrieved successfully") as ApiResponse<BoardGetResponse?>
        } ?: Error("Board with ID $boardId not found")
        handleResponse(response)
        return when (response) {
            is Success -> ResponseEntity.ok(response)
            is Error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }
    }

    // 게시글 수정
    @PutMapping("/{boardId}")
    @SessionCheck
    fun editBoard(
        @PathVariable boardId: Long,
        @RequestBody request: BoardEditRequest
    ): ResponseEntity<ApiResponse<Board?>> {
        val response = boardService.editBoardByBoardId(boardId, request)?.let {
            Success(it, "Board updated successfully") as ApiResponse<Board?>
        } ?: Error("Failed to update board with ID $boardId")
        handleResponse(response)
        return when (response) {
            is Success -> ResponseEntity.ok(response)
            is Error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }
    }

    // 게시글 삭제
    @DeleteMapping("/{boardId}")
    @SessionCheck
    fun deleteBoard(@PathVariable boardId: Long): ResponseEntity<ApiResponse<String>> {
        val response = try {
            boardService.deleteBoardByBoardId(boardId)
            Success("Board deleted successfully", "Deletion completed") as ApiResponse<String>
        } catch (e: Exception) {
            Error("Failed to delete board with ID $boardId: ${e.message}")
        }
        handleResponse(response)
        return when (response) {
            is Success -> ResponseEntity.ok(response)
            is Error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }
    }

    // 게시글 검색
    @GetMapping("/search")
    fun searchBoard(@RequestParam keyword: String): ResponseEntity<ApiResponse<List<Board>>> {
        val response = boardService.searchBoard(keyword).let {
            if (it.isNotEmpty()) Success(it, "Boards found for keyword: $keyword") as ApiResponse<List<Board>>
            else Error("No boards found for keyword: $keyword")
        }
        handleResponse(response)
        return when (response) {
            is Success -> ResponseEntity.ok(response)
            is Error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }
    }

    // gpt 게시글 요약
    @GetMapping("/summary/{boardId}")
    fun boardSummary(@PathVariable boardId: Long): ResponseEntity<ApiResponse<Mono<String>>> {
        val response = boardService.boardSummary(boardId).let {
            Success(it, "Board summary retrieved successfully") as ApiResponse<Mono<String>>
        } ?: Error("Failed to retrieve summary for board with ID $boardId")
        handleResponse(response)
        return when (response) {
            is Success -> ResponseEntity.ok(response)
            is Error -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }
    }
}