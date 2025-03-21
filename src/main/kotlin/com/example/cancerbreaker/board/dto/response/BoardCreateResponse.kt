package com.example.cancerbreaker.board.dto.response

import com.example.cancerbreaker.board.entity.Board
import com.example.cancerbreaker.board.entity.BoardCategory

data class BoardCreateResponse(
    val title: String = "",
    val content: String = "",
    val category: BoardCategory = BoardCategory.ANTI_CANCER
) {
    fun fromEntity(board: Board) = BoardCreateResponse(board.title, board.content, board.category)

}
