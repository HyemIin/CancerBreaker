package com.example.cancerbreaker.qna.entity

import com.example.cancerbreaker.global.entity.BaseEntity
import com.example.cancerbreaker.member.entity.User
import com.example.cancerbreaker.qna.dto.request.QnaEditRequest
import jakarta.persistence.*

@Entity
class Qna(
    @Column(nullable = false)
    var title: String,

    @Column(columnDefinition = "TEXT", nullable = false)
    var content: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Column(nullable = false)
    var isPublic: Boolean,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

) :BaseEntity() {
    init {
        if (title.isBlank()) throw IllegalStateException("제목은 빈 값일 수 없습니다.")
        if (content.isBlank()) throw IllegalStateException("내용은 빈 값일 수 없습니다.")
    }

    fun updateQna(qnaEditRequest: QnaEditRequest) {
        this.title = qnaEditRequest.title
        this.content = qnaEditRequest.content
        this.isPublic = qnaEditRequest.isPublic
    }
}