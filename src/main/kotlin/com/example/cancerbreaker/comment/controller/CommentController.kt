package com.example.cancerbreaker.comment.controller

import com.example.cancerbreaker.comment.dto.request.CommentCreateRequest
import com.example.cancerbreaker.comment.dto.request.CommentEditRequest
import com.example.cancerbreaker.comment.service.CommentService
import jakarta.servlet.http.HttpSession
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/comments")
class CommentController (private val commentService: CommentService){

    @PostMapping("{boardId}")
    fun createComment(@RequestBody request : CommentCreateRequest, session: HttpSession, @PathVariable boardId: Long) : ResponseEntity<Any> =
    ResponseEntity.ok(commentService.createComment(request,session,boardId))

    @GetMapping("{boardId}")
    fun getCommentList(@PathVariable boardId: Long): ResponseEntity<List<Any>> =
        ResponseEntity.ok(commentService.getCommentListByBoardId(boardId))

    @PutMapping("{commentId}")
    fun editComment(@RequestBody request: CommentEditRequest, session: HttpSession, @PathVariable commentId: Long): ResponseEntity<Any> =
        ResponseEntity.ok(commentService.editComment(commentId, session, request))

    @DeleteMapping("{commentId}")
    fun deleteComment(@PathVariable commentId: Long,session: HttpSession): ResponseEntity<Unit> =
        ResponseEntity.ok(commentService.deleteComment(commentId,session))

}