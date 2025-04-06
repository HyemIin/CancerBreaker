package com.example.cancerbreaker.board.service

import com.example.cancerbreaker.board.dto.request.BoardCreateRequest
import com.example.cancerbreaker.board.entity.Board
import com.example.cancerbreaker.board.entity.BoardCategory
import com.example.cancerbreaker.member.entity.User
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class BoardServiceTest : FunSpec() {
    @Test
    fun `createBoardEntity should return Board with correct properties`() {
        // Given
        val request = BoardCreateRequest(
            title = "Test Title",
            content = "Test Content",
            category = BoardCategory.ANTI_CANCER
        )
        val user = User(id = 1L, username = "Test User")

        // When
        val board = createBoardEntity(request, user)

        // Then
        board.title shouldBe "Test Title"
        board.content shouldBe "Test Content"
        board.category shouldBe BoardCategory.ANTI_CANCER
        board.user shouldBe user
    }

    private fun createBoardEntity(boardCreateRequest: BoardCreateRequest, user: User): Board {
        return Board(
            title = boardCreateRequest.title,
            content = boardCreateRequest.content,
            category = boardCreateRequest.category,
            user = user
        )
    }
}