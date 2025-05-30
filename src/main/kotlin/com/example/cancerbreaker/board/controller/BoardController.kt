package com.example.cancerbreaker.board.controller

import com.example.cancerbreaker.board.dto.request.BoardCreateRequest
import com.example.cancerbreaker.board.dto.request.BoardEditRequest
import com.example.cancerbreaker.board.dto.response.BoardCreateResponse
import com.example.cancerbreaker.board.dto.response.BoardEditResponse
import com.example.cancerbreaker.board.dto.response.BoardGetResponse
import com.example.cancerbreaker.board.entity.Board
import com.example.cancerbreaker.board.entity.BoardCategory
import com.example.cancerbreaker.board.service.BoardService
import com.example.cancerbreaker.global.aop.SessionCheck
import com.example.cancerbreaker.global.util.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/boards")
class BoardController(private val boardService: BoardService) {

    // 게시글 생성
    @PostMapping()
    @SessionCheck
    fun createBoard(@RequestBody request: BoardCreateRequest): ResponseEntity<ApiResponse<BoardCreateResponse>> {
        return boardService.executeCreateBoard(request)
            .fold(
                onSuccess = {
                    ResponseEntity.ok(ApiResponse.Success(it, "게시글 생성 완료"))
                },
                onFailure = {
                    ResponseEntity.badRequest().body(ApiResponse.Error(it.message ?: "알 수 없는 오류"))
                }
            )
    }


    // 전체 게시글 불러오기
    @GetMapping("/all")
    fun getAllBoardList(): ResponseEntity<ApiResponse<List<BoardGetResponse>>> {
        return boardService.excuteGetAllBoardList()
            .fold(
                onSuccess = {
                    ResponseEntity.ok(ApiResponse.Success(it, "게시글 전체 리스트 조회 완료"))
                },
                onFailure = {
                    ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.Error(it.message ?: "알 수 없는 오류"))
                }
            )
    }

    // 카테고리별 게시글 불러오기
    @GetMapping
    fun getBoardsByCategory(@RequestParam category: BoardCategory): ResponseEntity<ApiResponse<List<BoardGetResponse>>> {
        return boardService.executeGetBoardListByCategory(category)
            .fold(
                onSuccess = {
                    ResponseEntity.ok(ApiResponse.Success(it, "게시글 목록 조회 완료"))
                },
                onFailure = {
                    ResponseEntity.badRequest().body(ApiResponse.Error(it.message ?: "알 수 없는 오류"))
                }
            )
    }

    // 특정 게시글 불러오기
    @GetMapping("/{boardId}")
    fun getOneBoard(@PathVariable boardId: Long): ResponseEntity<ApiResponse<BoardGetResponse>> {
        return boardService.executeGetBoardByBoardId(boardId)
            .fold(
                onSuccess = {
                    ResponseEntity.ok(ApiResponse.Success(it, "게시글 조회 완료"))
                },
                onFailure = {
                    ResponseEntity.badRequest().body(ApiResponse.Error(it.message ?: "알 수 없는 오류"))
                }
            )
    }

    // 게시글 수정
    @PutMapping("/{boardId}")
    @SessionCheck
    fun editBoard(
        @PathVariable boardId: Long,
        @RequestBody request: BoardEditRequest
    ): ResponseEntity<ApiResponse<BoardEditResponse>> {
        return boardService.editBoardByBoardId(boardId, request)
            .fold(
                onSuccess = {
                    ResponseEntity.ok(ApiResponse.Success(it, "Board updated successfully"))
                },
                onFailure = {
                    ResponseEntity.badRequest().body(ApiResponse.Error(it.message ?: "알 수 없는 오류"))
                }
            )
    }

    // 게시글 삭제
    @DeleteMapping("/{boardId}")
    @SessionCheck
    fun deleteBoard(@PathVariable boardId: Long): ResponseEntity<ApiResponse<String>> {
        return boardService.deleteBoardByBoardId(boardId)
            .fold(
                onSuccess = {
                    ResponseEntity.ok(ApiResponse.Success(it, "Board deleted successfully"))
                },
                onFailure = {
                    ResponseEntity.badRequest().body(ApiResponse.Error(it.message ?: "알 수 없는 오류"))
                }
            )
    }

    // 게시글 검색
    @GetMapping("/search")
    fun searchBoard(@RequestParam keyword: String): ResponseEntity<ApiResponse<List<Board>>> {
        return boardService.searchBoard(keyword)
            .fold(
                onSuccess = {
                    ResponseEntity.ok(ApiResponse.Success(it, "Boards found for keyword: $keyword"))
                },
                onFailure = {
                    ResponseEntity.badRequest().body(ApiResponse.Error(it.message ?: "알 수 없는 오류"))
                }
            )
    }

    // gpt 게시글 요약
    @GetMapping("/summary/{boardId}")
    suspend fun boardSummary(@PathVariable boardId: Long): ResponseEntity<ApiResponse<String>> = withContext(Dispatchers.IO) {
        boardService.boardSummary(boardId).fold(
            onSuccess = { summary ->
                ResponseEntity.ok(ApiResponse.Success(summary, "Board summary retrieved successfully"))
            },
            onFailure = { exception ->
                ResponseEntity.badRequest().body(ApiResponse.Error(exception.message ?: "알 수 없는 오류"))
            }
        )
    }
    // gpt 게시글 요약(동기 방식)
    @GetMapping("/synsummary/{boardId}")
    fun synBoardSummary(@PathVariable boardId: Long): ResponseEntity<ApiResponse<String>> {
        return boardService.synBoardSummary(boardId)
            .fold(
                onSuccess = { summary ->
                    ResponseEntity.ok(ApiResponse.Success(summary, "Board summary retrieved successfully"))
                },
                onFailure = {
                    ResponseEntity.badRequest().body(ApiResponse.Error(it.message ?: "알 수 없는 오류"))
                }
            )
    }
}