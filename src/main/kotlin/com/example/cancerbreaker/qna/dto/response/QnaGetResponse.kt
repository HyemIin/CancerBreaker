package com.example.cancerbreaker.qna.dto.response

import com.example.cancerbreaker.member.entity.Role
import com.example.cancerbreaker.member.entity.User
import com.example.cancerbreaker.qna.entity.Qna
import java.time.LocalDateTime

data class QnaGetResponse(
    val id: Long? = 0,
    val title: String = "",
    val content: String = "",
    val isPublic: Boolean = false,
    val user: User = User(0L, "", "", "",Role.PATIENT),
    val createdAt : LocalDateTime = LocalDateTime.now()
) {
    fun fromEntity(qna: Qna) = QnaGetResponse(qna.id,qna.title, qna.content,qna.isPublic, qna.user, qna.createdAt)
}
