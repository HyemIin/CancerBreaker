package com.example.cancerbreaker.comment.entity

import com.example.cancerbreaker.board.entity.Board
import com.example.cancerbreaker.comment.dto.request.CommentEditRequest
import com.example.cancerbreaker.global.entity.BaseEntity
import com.example.cancerbreaker.member.entity.User
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*

@Entity
class Comment private constructor(

    var content : String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user : User,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    var board : Board,

    ) : BaseEntity() {
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