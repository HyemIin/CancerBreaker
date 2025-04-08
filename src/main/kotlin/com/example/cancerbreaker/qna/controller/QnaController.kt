package com.example.cancerbreaker.qna.controller

import com.example.cancerbreaker.global.aop.SessionCheck
import com.example.cancerbreaker.global.util.ApiResponse
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
    fun createQna(@RequestBody request: QnaCreateRequest): ResponseEntity<ApiResponse<QnaCreateResponse>> {
        return qnaService.executeCreateQna(request)
            .fold(
                onSuccess = { ResponseEntity.ok(ApiResponse.Success(it, "QnA 생성 완료")) },
                onFailure = { ResponseEntity.badRequest().body(ApiResponse.Error(it.message ?: "알 수 없는 오류")) }
            )
    }

    // QnA 게시글 리스트 조회
    @GetMapping("/all")
    fun getAllQnaList(): ResponseEntity<ApiResponse<List<QnaGetResponse>>> {
        return qnaService.executeGetQnaList()
            .fold(
                onSuccess = { ResponseEntity.ok(ApiResponse.Success(it, "QnA 리스트 조회 완료")) },
                onFailure = { ResponseEntity.badRequest().body(ApiResponse.Error(it.message ?: "알 수 없는 오류")) }
            )
    }

    // QnA 특정 게시글 조회
    @GetMapping("/{qnaId}")
    fun getQnaById(@PathVariable qnaId: Long): ResponseEntity<ApiResponse<QnaGetResponse>> {
        return qnaService.getQnaById(qnaId)
            .fold(
                onSuccess = { ResponseEntity.ok(ApiResponse.Success(it, "QnA 조회 완료")) },
                onFailure = { ResponseEntity.badRequest().body(ApiResponse.Error(it.message ?: "알 수 없는 오류")) }
            )
    }

    // QnA 게시글 수정
    @PutMapping("/{qnaId}")
    @SessionCheck
    fun editQna(
        @PathVariable qnaId: Long,
        @RequestBody request: QnaEditRequest
    ): ResponseEntity<ApiResponse<QnaEditResponse>> {
        return qnaService.executeEditQna(qnaId, request)
            .fold(
                onSuccess = { ResponseEntity.ok(ApiResponse.Success(it, "QnA 수정 완료")) },
                onFailure = { ResponseEntity.badRequest().body(ApiResponse.Error(it.message ?: "알 수 없는 오류")) }
            )
    }

    // QnA 게시글 삭제
    @DeleteMapping("/{qnaId}")
    @SessionCheck
    fun deleteQna(@PathVariable qnaId: Long): ResponseEntity<ApiResponse<String>> {
        return qnaService.executeDeleteQna(qnaId)
            .fold(
                onSuccess = { ResponseEntity.ok(ApiResponse.Success(it, "QnA 삭제 성공")) },
                onFailure = { ResponseEntity.badRequest().body(ApiResponse.Error(it.message ?: "알 수 없는 오류")) }
            )
    }

}