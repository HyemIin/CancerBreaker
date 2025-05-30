package com.example.cancerbreaker.comment.entity

import com.example.cancerbreaker.board.entity.Board
import com.example.cancerbreaker.comment.dto.request.CommentEditRequest
import com.example.cancerbreaker.global.entity.BaseEntity
import com.example.cancerbreaker.member.entity.User
import com.fasterxml.jackson.annotation.JsonCreator
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class Comment private constructor(
    content: String,
    user: User,
    board: Board
    ) : BaseEntity() {
    @Column(nullable = false)
    var content : String = content
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user : User = user
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    var board : Board = board
        protected set
    init {
        check (content.isNotBlank()) {
            throw IllegalStateException("댓글은 빈 값일 수 없습니다.")
        }
    }

    fun updateComment(commentEditRequest: CommentEditRequest) {
        require(commentEditRequest.content.isNotBlank()) { "수정 시 댓글은 빈 값일 수 없습니다." }
        this.content = commentEditRequest.content
    }

    companion object {
        @JsonCreator
        fun from(
            content: String,
            user: User,
            board: Board
        ) : Comment = Comment(content=content, user = user, board = board)

        operator fun invoke(
            content: String,
            user: User,
            board: Board
            ) : Comment = from(content, user, board)
    }

}