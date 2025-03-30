package com.example.cancerbreaker.board.entity

import com.example.cancerbreaker.board.dto.request.BoardEditRequest
import com.example.cancerbreaker.comment.entity.Comment
import com.example.cancerbreaker.global.entity.BaseEntity
import com.example.cancerbreaker.member.entity.User
import jakarta.persistence.*

@Entity
@Table(
    name = "board",
    indexes = [Index(name = "idx_fts", columnList = "title, content", unique = false)]
)
class Board(
    @Column(nullable = false)
    var title: String,

    @Column(columnDefinition = "TEXT", nullable = false)
    var content: String,

    @Column(nullable = false)
    var category: BoardCategory,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    var comments: List<Comment> = emptyList(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) : BaseEntity() {
    init {
        if (title.isBlank()) throw IllegalStateException("제목은 빈 값일 수 없습니다.")
        if (content.isBlank()) throw IllegalStateException("내용은 빈 값일 수 없습니다.")
    }

    fun updateBoard(boardEditRequest: BoardEditRequest) {
        this.title = boardEditRequest.title
        this.content = boardEditRequest.content
        this.category = boardEditRequest.category
    }
}