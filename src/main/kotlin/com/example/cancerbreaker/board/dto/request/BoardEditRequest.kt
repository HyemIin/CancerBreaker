package com.example.cancerbreaker.board.dto.request

import com.example.cancerbreaker.board.entity.BoardCategory

data class BoardEditRequest(
    val title: String,
    val content: String,
    val category: BoardCategory
)
