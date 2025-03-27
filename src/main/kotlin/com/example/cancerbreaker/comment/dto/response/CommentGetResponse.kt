package com.example.cancerbreaker.comment.dto.response

import com.example.cancerbreaker.board.entity.Board
import com.example.cancerbreaker.board.entity.BoardCategory
import com.example.cancerbreaker.comment.entity.Comment
import com.example.cancerbreaker.member.entity.User
import java.time.LocalDateTime

data class CommentGetResponse(
    val content: String = "",
    val user: User = User(),
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    fun fromEntity(comment: Comment) = CommentGetResponse(comment.content, comment.user,comment.createdAt)
}
