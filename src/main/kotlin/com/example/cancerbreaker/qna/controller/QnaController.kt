package com.example.cancerbreaker.qna.controller

import com.example.cancerbreaker.global.aop.SessionCheck
import com.example.cancerbreaker.qna.dto.request.QnaCreateRequest
import com.example.cancerbreaker.qna.dto.request.QnaEditRequest
import com.example.cancerbreaker.qna.dto.response.QnaCreateResponse
import com.example.cancerbreaker.qna.dto.response.QnaEditResponse
import com.example.cancerbreaker.qna.dto.response.QnaGetResponse
import com.example.cancerbreaker.qna.service.QnaService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/qna")
class QnaController(
    private val qnaService: QnaService
) {
    // QnA 게시글 생성
    @PostMapping
    @SessionCheck
    fun createQna(
        @RequestBody request: QnaCreateRequest
    ) : ResponseEntity<QnaCreateResponse>
    = ResponseEntity.ok(qnaService.createQna(request))
    // QnA 게시글 리스트 조회
    @GetMapping("/all")
    fun getAllQnaList(): ResponseEntity<List<QnaGetResponse>>
    = ResponseEntity.ok(qnaService.getQnaList())
    // QnA 특정 게시글 조회
    @GetMapping("/{qnaId}")
    fun getQnaById(@PathVariable qnaId: Long): ResponseEntity<QnaGetResponse>
    = ResponseEntity.ok(qnaService.getQnaById(qnaId))
    // QnA 게시글 수정
    @PutMapping("/{qnaId}")
    @SessionCheck
    fun editQna(@PathVariable qnaId: Long, @RequestBody request: QnaEditRequest): ResponseEntity<QnaEditResponse>
    = ResponseEntity.ok(qnaService.editQna(qnaId, request))
    // QnA 게시글 삭제
    @DeleteMapping("/{qnaId}")
    @SessionCheck
    fun deleteQna(@PathVariable qnaId: Long): ResponseEntity<Unit>
    = ResponseEntity.ok(qnaService.deleteQna(qnaId))

}