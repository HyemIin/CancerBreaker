package com.example.cancerbreaker.comment.dto.response

import com.example.cancerbreaker.comment.entity.Comment
import com.example.cancerbreaker.member.dto.MemberGetResponse
import java.time.LocalDateTime

data class CommentGetResponse(
    val id: Long? = 0,
    val content: String = "",
    val user: MemberGetResponse,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun fromEntity(comment: Comment): CommentGetResponse {
            return CommentGetResponse(
                id = comment.id,
                content = comment.content,
                user = MemberGetResponse.fromEntity(comment.user),  // 🔥 Comment 내 User도 DTO 변환
                createdAt = comment.createdAt
            )
        }
    }
}
