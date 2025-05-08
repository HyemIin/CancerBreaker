package com.example.cancerbreaker.comment.dto.response

import com.example.cancerbreaker.comment.entity.Comment
import com.example.cancerbreaker.member.dto.UserGetDTO
import com.example.cancerbreaker.member.entity.Role
import com.example.cancerbreaker.member.entity.User

data class CommentEditResponse(
    val content: String = "",
    val user: UserGetDTO = UserGetDTO( "", "", "",Role.PATIENT),
) {
    fun fromEntity(comment: Comment) = CommentEditResponse(comment.content, comment.user.let { UserGetDTO.fromEntity(it) })

}