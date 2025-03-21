package com.example.cancerbreaker.comment.service

import com.example.cancerbreaker.board.repository.BoardRepository
import com.example.cancerbreaker.comment.dto.request.CommentCreateRequest
import com.example.cancerbreaker.comment.dto.request.CommentEditRequest
import com.example.cancerbreaker.comment.dto.response.CommentCreateResponse
import com.example.cancerbreaker.comment.dto.response.CommentGetResponse
import com.example.cancerbreaker.comment.entity.Comment
import com.example.cancerbreaker.comment.repository.CommentRepository
import com.example.cancerbreaker.member.repository.UserRepository
import jakarta.servlet.http.HttpSession
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val boardRepository: BoardRepository,
    private val userRepository: UserRepository
) {

    @Transactional
    fun createComment(commentCreateRequest: CommentCreateRequest, session: HttpSession, boardId:Long) : CommentCreateResponse {
        val userId = session.getAttribute("id") as Long?
            ?: throw IllegalArgumentException("User not logged in")
        val user = userRepository.findByIdOrNull(userId)
        val board = boardRepository.findByIdOrNull(boardId)
        val createdComment = commentRepository.save(Comment(commentCreateRequest.content,user!!,
            board!!
        ))
        return CommentCreateResponse().fromEntity(createdComment)
    }

    @Transactional(readOnly = true)
    fun getCommentListByBoardId(boardId: Long): List<CommentGetResponse> {
        val board = boardRepository.findByIdOrNull(boardId)
            ?: throw IllegalArgumentException("Board not found")
        val comments = commentRepository.findAllByBoard(board)
        return comments.map { CommentGetResponse().fromEntity(it) }
    }

    @Transactional
    fun editComment(commentId: Long, session: HttpSession, request: CommentEditRequest): Any? {
        val userId = session.getAttribute("id") as Long?
            ?: throw IllegalArgumentException("User not logged in")
        val comment = commentRepository.findByIdOrNull(commentId)
        when (userId) {
            comment!!.user.id -> comment.updateComment(request)
            else -> throw IllegalArgumentException("당사자만 수정할 수 있습니다.")
        }
        return CommentGetResponse().fromEntity(comment)

    }
}