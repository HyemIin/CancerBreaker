package com.example.cancerbreaker.board.controller

import com.example.cancerbreaker.board.dto.request.BoardCreateRequest
import com.example.cancerbreaker.board.dto.request.BoardEditRequest
import com.example.cancerbreaker.board.dto.response.BoardCreateResponse
import com.example.cancerbreaker.board.dto.response.BoardGetResponse
import com.example.cancerbreaker.board.entity.Board
import com.example.cancerbreaker.board.entity.BoardCategory
import com.example.cancerbreaker.board.service.BoardService
import jakarta.servlet.http.HttpSession
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/boards")
class BoardController(private val boardService: BoardService) {

    // 게시글 생성
    @PostMapping
    fun createBoard(
        @RequestBody request: BoardCreateRequest,
        session: HttpSession
    ): ResponseEntity<BoardCreateResponse> =
        ResponseEntity.ok(boardService.createBoard(request, session))


    // 전체 게시글 불러오기
    @GetMapping("/all")
    fun getAllBoardLst(): ResponseEntity<List<Board>> = ResponseEntity.ok(boardService.getAllBoardList())

    // 카테고리별 게시글 불러오기
    @GetMapping
    fun getBoardList(@RequestParam category: BoardCategory): ResponseEntity<List<Board>> =
        ResponseEntity.ok(boardService.getBoardListByCategory(category) ?: emptyList())

    // 특정 게시글 불러오기
    @GetMapping("/{boardId}")
    fun getOneBoard(@PathVariable boardId: Long): ResponseEntity<BoardGetResponse?> =
        ResponseEntity.ok(boardService.getBoardByBoardId(boardId))

    // 게시글 수정
    @PutMapping("/{boardId}")
    fun editBoard(
        @PathVariable boardId: Long,
        @RequestBody request: BoardEditRequest,
        session: HttpSession
    ): ResponseEntity<Board?> {
        return ResponseEntity.ok(boardService.editBoardByBoardId(boardId, request, session))
    }

    // 게시글 삭제
    @DeleteMapping("/{boardId}")
    fun deleteBoard(@PathVariable boardId: Long, session: HttpSession): ResponseEntity<String> {
        boardService.deleteBoardByBoardId(boardId, session)
        return ResponseEntity.ok("Board deleted successfully")
    }

    // 게시글 검색
    @GetMapping("/search")
    fun searchBoard(@RequestParam keyword: String): ResponseEntity<List<Board>> {
        return ResponseEntity.ok(boardService.searchBoard(keyword))
    }

    // gpt 게시글 요약
    @GetMapping("/summary/{boardId}")
    fun boardSummary(@PathVariable boardId: Long) : ResponseEntity<Mono<String>> {
        return ResponseEntity.ok(boardService.boardSummary(boardId))

    }
}