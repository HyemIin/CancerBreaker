package com.example.cancerbreaker.comment.dto.response

import com.example.cancerbreaker.comment.entity.Comment
import com.example.cancerbreaker.member.entity.Role
import com.example.cancerbreaker.member.entity.User

data class CommentCreateResponse(
    val content: String = "",
    val user: User = User("", "","", Role.PATIENT),
) {
    fun fromEntity(comment: Comment) = CommentCreateResponse(comment.content, comment.user)

}
