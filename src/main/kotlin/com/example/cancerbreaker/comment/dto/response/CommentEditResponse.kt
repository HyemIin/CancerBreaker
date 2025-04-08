package com.example.cancerbreaker.comment.dto.response

import com.example.cancerbreaker.comment.entity.Comment
import com.example.cancerbreaker.member.entity.User

data class CommentEditResponse(
    val content: String = "",
    val user: User = User()
) {
    fun fromEntity(comment: Comment) = CommentEditResponse(comment.content, comment.user)

}