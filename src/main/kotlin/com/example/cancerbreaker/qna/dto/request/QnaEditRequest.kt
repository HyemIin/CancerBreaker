package com.example.cancerbreaker.qna.dto.request

data class QnaEditRequest(
    val title: String,
    val content: String,
가    val isPublic: Boolean
)
