package com.example.cancerbreaker.comment.controller

import com.example.cancerbreaker.comment.dto.request.CommentCreateRequest
import com.example.cancerbreaker.comment.dto.request.CommentEditRequest
import com.example.cancerbreaker.comment.dto.response.CommentCreateResponse
import com.example.cancerbreaker.comment.dto.response.CommentEditResponse
import com.example.cancerbreaker.comment.service.CommentService
import com.example.cancerbreaker.global.aop.SessionCheck
import com.example.cancerbreaker.global.util.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/comments")
class CommentController (private val commentService: CommentService){

    @PostMapping("{boardId}")
    @SessionCheck
    fun createComment(@RequestBody request : CommentCreateRequest, @PathVariable boardId: Long) : ResponseEntity<ApiResponse<CommentCreateResponse>> {
        return commentService.executeCreateComment(request, boardId)
            .fold(
                onSuccess = {
                    ResponseEntity.ok(ApiResponse.Success(it, "댓글 생성 완료"))
                },
                onFailure = {
                    ResponseEntity.badRequest().body(ApiResponse.Error(it.message ?: "알 수 없는 오류"))
                }
            )
    }


    @GetMapping("{boardId}")
    fun getCommentList(@PathVariable boardId: Long): ResponseEntity<ApiResponse<List<Any>>> {
        return commentService.executeGetCommentListByBoardId(boardId)
            .fold(
                onSuccess = {
                    ResponseEntity.ok(ApiResponse.Success(it, "댓글 목록 조회 완료"))
                },
                onFailure = {
                    ResponseEntity.badRequest().body(ApiResponse.Error(it.message ?: "알 수 없는 오류"))
                }
        )
    }

    @PutMapping("/{commentId}")
    @SessionCheck
    fun editComment(
        @RequestBody request: CommentEditRequest,
        @PathVariable commentId: Long
    ): ResponseEntity<ApiResponse<CommentEditResponse>> {
        return commentService.executeEditComment(commentId, request)
            .fold(
                onSuccess = {
                    ResponseEntity.ok(ApiResponse.Success(it, "댓글 수정 완료"))
                },
                onFailure = {
                    ResponseEntity.badRequest().body(ApiResponse.Error(it.message ?: "알 수 없는 오류"))
                }
            )
    }
    @DeleteMapping("/{commentId}")
    @SessionCheck
    fun deleteComment(@PathVariable commentId: Long): ResponseEntity<ApiResponse<String>> {
        return commentService.executeDeleteComment(commentId)
            .fold(
                onSuccess = {
                    ResponseEntity.ok(ApiResponse.Success(it, "댓글 삭제 성공"))
                },
                onFailure = {
                    ResponseEntity.badRequest().body(ApiResponse.Error(it.message ?: "알 수 없는 오류"))
                }
            )
    }

}