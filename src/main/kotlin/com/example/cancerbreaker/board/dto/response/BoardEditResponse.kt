package com.example.cancerbreaker.board.dto.response

import com.example.cancerbreaker.board.entity.Board
import com.example.cancerbreaker.board.entity.BoardCategory
import com.example.cancerbreaker.comment.dto.response.CommentGetResponse
import com.example.cancerbreaker.member.dto.UserGetDTO
import com.example.cancerbreaker.member.entity.Role
import java.time.LocalDateTime

data class BoardEditResponse(
    val id: Long? = 0L,
    val title: String = "",
    val content: String = "",
    val category: BoardCategory = BoardCategory.ANTI_CANCER,
    val user: UserGetDTO = UserGetDTO( "", "", "",Role.PATIENT),
    val createdAt: LocalDateTime? = LocalDateTime.now(),
    val commentList: List<CommentGetResponse> = emptyList()
) {
    companion object {
        fun fromEntity(board: Board) = BoardEditResponse(
            board.id,
            board.title,
            board.content,
            board.category,
            board.user.let { UserGetDTO.fromEntity(it) },
            board.createdAt,
            board.comments.map { CommentGetResponse.fromEntity(it) })
    }
}
