package com.example.cancerbreaker.qna.dto.response

import com.example.cancerbreaker.member.entity.Role
import com.example.cancerbreaker.member.entity.User
import com.example.cancerbreaker.qna.entity.Qna
import java.time.LocalDateTime

data class QnaCreateResponse(
    val title: String = "",
    val content: String = "",
    val user: User = User(0L, "", "","", Role.PATIENT),
    val createdAt : LocalDateTime = LocalDateTime.now()
) {
    fun fromEntity(qna: Qna) = QnaCreateResponse(qna.title, qna.content, qna.user, qna.createdAt)
}
