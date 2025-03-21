package com.example.cancerbreaker.board.dto.response

import com.example.cancerbreaker.board.entity.Board
import com.example.cancerbreaker.board.entity.BoardCategory
import com.example.cancerbreaker.member.entity.User
import java.time.LocalDateTime

data class BoardGetResponse(
    val title: String = "",
    val content: String = "",
    val category: BoardCategory = BoardCategory.ANTI_CANCER,
    val user: User = User(),
    val createdAt : LocalDateTime = LocalDateTime.now()
) {
    fun fromEntity(board: Board) = BoardGetResponse(board.title, board.content, board.category, board.user, board.createdAt)
}
