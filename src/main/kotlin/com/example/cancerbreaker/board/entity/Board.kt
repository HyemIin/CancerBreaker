package com.example.cancerbreaker.board.entity

import com.example.cancerbreaker.board.dto.request.BoardEditRequest
import com.example.cancerbreaker.comment.entity.Comment
import com.example.cancerbreaker.global.entity.BaseEntity
import com.example.cancerbreaker.member.entity.User
import com.fasterxml.jackson.annotation.JsonCreator
import jakarta.persistence.*

@Entity
@Table(
    name = "board",
    indexes = [Index(name = "idx_fts", columnList = "title, content", unique = false)]
)
class Board private constructor(
    title: String,
    content: String,
    category: BoardCategory,
    user: User,
    comments: MutableList<Comment>

) : BaseEntity() {
    @Column(nullable = false)
    var title: String = title
        protected set

    @Column(columnDefinition = "TEXT", nullable = false)
    var content: String = content
        protected set

    @Column(nullable = false)
    var category: BoardCategory = category
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User = user
        protected set

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "board_id")
    var comments: MutableList<Comment> = comments
        protected set

    fun updateBoard(boardEditRequest: BoardEditRequest) {
        require(boardEditRequest.title.isNotBlank()) { "제목은 빈 값일 수 없습니다." }
        require(boardEditRequest.content.isNotBlank()) { "내용은 빈 값일 수 없습니다." }
        this.title = boardEditRequest.title
        this.content = boardEditRequest.content
        this.category = boardEditRequest.category
    }
    companion object{
        @JsonCreator
        fun from(
            title: String,
            content: String,
            category: BoardCategory,
            user: User,
            comments: MutableList<Comment>
        ) : Board {
            check (title.isNotBlank()) {throw IllegalStateException("제목은 빈 값일 수 없습니다.") }
            check (content.isNotBlank()) {throw IllegalStateException("내용은 빈 값일 수 없습니다.")}

            return Board(title = title, content = content, category = category, user = user, comments = comments)
        }
        operator fun invoke(
            title: String,
            content: String,
            category: BoardCategory,
            user: User,
            comments: MutableList<Comment>
        ) : Board = from(title, content, category, user,comments)
    }

}