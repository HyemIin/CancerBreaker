package com.example.cancerbreaker.board.dto.response

import com.example.cancerbreaker.board.entity.Board
import com.example.cancerbreaker.board.entity.BoardCategory
import com.example.cancerbreaker.comment.dto.response.CommentGetResponse
import com.example.cancerbreaker.member.entity.User
import java.time.LocalDateTime

data class BoardGetResponse(
    val id: Long? = 0L,
    val title: String = "",
    val content: String = "",
    val category: BoardCategory = BoardCategory.ANTI_CANCER,
    val user: User = User(),
    val createdAt: LocalDateTime? = LocalDateTime.now(),
    val commentList: List<CommentGetResponse> = emptyList()
) {
    fun fromEntity(board: Board) = BoardGetResponse(board.id,board.title, board.content, board.category, board.user, board.createdAt,board.comments.map { CommentGetResponse.fromEntity(it) })
}
