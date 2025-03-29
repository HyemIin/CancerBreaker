package com.example.cancerbreaker.qna.dto.response

import com.example.cancerbreaker.member.entity.User
import com.example.cancerbreaker.qna.entity.Qna
import java.time.LocalDateTime

data class QnaGetResponse(
    val title: String = "",
    val content: String = "",
    val user: User = User(),
    val createdAt : LocalDateTime = LocalDateTime.now()
) {
    fun fromEntity(qna: Qna) = QnaGetResponse(qna.title, qna.content, qna.user, qna.createdAt)
}
