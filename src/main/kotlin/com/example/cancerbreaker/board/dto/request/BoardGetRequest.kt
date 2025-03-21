package com.example.cancerbreaker.board.dto.request

import com.example.cancerbreaker.board.entity.BoardCategory

data class BoardGetRequest(
    val category: BoardCategory
)

