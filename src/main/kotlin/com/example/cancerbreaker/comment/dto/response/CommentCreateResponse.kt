package com.example.cancerbreaker.comment.dto.response

import com.example.cancerbreaker.board.entity.Board
import com.example.cancerbreaker.board.entity.BoardCategory
import com.example.cancerbreaker.comment.entity.Comment
import com.example.cancerbreaker.member.entity.User

data class CommentCreateResponse(
    val content: String = "",
    val user: User = User()
) {
    fun fromEntity(comment: Comment) = CommentCreateResponse(comment.content, comment.user)

}
