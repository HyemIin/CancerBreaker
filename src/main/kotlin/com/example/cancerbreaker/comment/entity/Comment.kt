package com.example.cancerbreaker.comment.entity

import com.example.cancerbreaker.board.entity.Board
import com.example.cancerbreaker.comment.dto.request.CommentEditRequest
import com.example.cancerbreaker.global.entity.BaseEntity
import com.example.cancerbreaker.member.entity.User
import jakarta.persistence.*

@Entity
class Comment(

    var content : String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user : User,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    var board : Board,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    ) : BaseEntity() {
    init {
        if (content.isBlank()) {
            throw IllegalStateException("댓글은 빈 값일 수 없습니다.")
        }
    }

    fun updateComment(commentEditRequest: CommentEditRequest) {
        this.content = commentEditRequest.content
    }

}