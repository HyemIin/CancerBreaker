package com.example.cancerbreaker.qna.entity

import com.example.cancerbreaker.global.entity.BaseEntity
import com.example.cancerbreaker.member.entity.User
import com.example.cancerbreaker.qna.dto.request.QnaEditRequest
import com.fasterxml.jackson.annotation.JsonCreator
import jakarta.persistence.*

@Entity
class Qna private constructor(
    @Column(nullable = false)
    var title: String = "",

    @Column(columnDefinition = "TEXT", nullable = false)
    var content: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Column(nullable = false)
    var isPublic: Boolean,


) :BaseEntity() {
    init {
        check (title.isNotBlank()) {("제목은 빈 값일 수 없습니다.")}
        check (content.isNotBlank()) {("내용은 빈 값일 수 없습니다.")}
    }

    fun updateQna(qnaEditRequest: QnaEditRequest) {
        require(qnaEditRequest.title.isNotBlank()) { "QNA 제목은 빈 값일 수 없습니다." }
        require(qnaEditRequest.content.isNotBlank()) { "QNA 내용은 빈 값일 수 없습니다." }
        this.title = qnaEditRequest.title
        this.content = qnaEditRequest.content
        this.isPublic = qnaEditRequest.isPublic
    }

    companion object{
        @JsonCreator
        fun from(
            title: String,
            content: String,
            user: User,
            isPublic: Boolean
        ): Qna = Qna(title, content, user, isPublic)

        operator fun invoke(
            title: String,
            content: String,
            user: User,
            isPublic: Boolean
        ): Qna = from(title, content, user, isPublic)
    }
}