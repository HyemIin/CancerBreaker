package com.example.cancerbreaker.comment.dto.response

import com.example.cancerbreaker.comment.entity.Comment
import com.example.cancerbreaker.member.entity.Role
import com.example.cancerbreaker.member.entity.User

data class CommentEditResponse(
    val content: String = "",
    val user: User = User(1L, "", "", "",Role.PATIENT),
) {
    fun fromEntity(comment: Comment) = CommentEditResponse(comment.content, comment.user)

}