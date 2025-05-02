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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var title: String,

    @Column(columnDefinition = "TEXT", nullable = false)
    var content: String,

    @Column(nullable = false)
    var category: BoardCategory,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "board_id")
    var comments: List<Comment> = emptyList()
) : BaseEntity() {
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
            comments: List<Comment>
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
            comments: List<Comment>
        ) : Board = from(title, content, category, user,comments)
    }

}