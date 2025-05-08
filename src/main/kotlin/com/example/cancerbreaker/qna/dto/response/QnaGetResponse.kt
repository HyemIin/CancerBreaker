package com.example.cancerbreaker.qna.dto.response

import com.example.cancerbreaker.member.dto.UserGetDTO
import com.example.cancerbreaker.member.entity.Role
import com.example.cancerbreaker.member.entity.User
import com.example.cancerbreaker.qna.entity.Qna
import java.time.LocalDateTime

data class QnaGetResponse(
    val id: Long? = 0,
    val title: String = "",
    val content: String = "",
    val isPublic: Boolean = false,
    val user: UserGetDTO = UserGetDTO( "", "", "",Role.PATIENT),
    val createdAt : LocalDateTime = LocalDateTime.now()
) {
    fun fromEntity(qna: Qna) = QnaGetResponse(qna.id,qna.title, qna.content,qna.isPublic, qna.user.let { UserGetDTO.fromEntity(it) }, qna.createdAt)
}
