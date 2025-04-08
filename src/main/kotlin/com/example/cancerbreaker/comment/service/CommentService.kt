package com.example.cancerbreaker.comment.service

import com.example.cancerbreaker.board.entity.Board
import com.example.cancerbreaker.board.repository.BoardRepository
import com.example.cancerbreaker.comment.dto.request.CommentCreateRequest
import com.example.cancerbreaker.comment.dto.request.CommentEditRequest
import com.example.cancerbreaker.comment.dto.response.CommentCreateResponse
import com.example.cancerbreaker.comment.dto.response.CommentEditResponse
import com.example.cancerbreaker.comment.dto.response.CommentGetResponse
import com.example.cancerbreaker.comment.entity.Comment
import com.example.cancerbreaker.comment.repository.CommentRepository
import com.example.cancerbreaker.global.util.SessionUtil
import com.example.cancerbreaker.member.entity.User
import com.example.cancerbreaker.member.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val boardRepository: BoardRepository,
    private val userRepository: UserRepository,
    private val sessionUtil: SessionUtil
) {
    // 댓글 생성(부수효과 포함 로직)
    @Transactional
    fun executeCreateComment(
        commentCreateRequest: CommentCreateRequest,
        boardId: Long
    ): Result<CommentCreateResponse> {
        return createComment(
            commentCreateRequest,
            boardId,
            getUserId = { sessionUtil.getCurrentUserId() },
            findUser = { userId -> userRepository.findByIdOrNull(userId) },
            findBoard = { boardId -> boardRepository.findByIdOrNull(boardId) },
            saveComment = { comment -> commentRepository.save(comment) }
        )
    }
    // 댓글 생성(순수함수)
    private fun createComment(
        request: CommentCreateRequest,
        boardId : Long,
        getUserId: () -> Long,
        findUser : (Long) -> User?,
        findBoard: (Long) -> Board?,
        saveComment : (Comment) -> Comment
    ) :Result<CommentCreateResponse> {
        return runCatching {
            val userId = getUserId()
            val user = findUser(userId) ?: throw IllegalArgumentException("사용자를 찾을 수 없습니다.")
            val board = findBoard(boardId) ?: throw IllegalArgumentException("게시글을 찾을 수 없습니다.")
            val comment = createCommentEntity(request, user, board)
            val savedComment = saveComment(comment)
            CommentCreateResponse(content = savedComment.content, user = savedComment.user)
        }
    }
    //CommentEntity 생성(순수함수)
    private fun createCommentEntity(request: CommentCreateRequest, user: User, board: Board): Comment {
        return Comment(
            content = request.content,
            user = user,
            board = board
        )
    }
    // 게시글 내 댓글 리스트 조회(부수효과 포함 로직)
    @Transactional(readOnly = true)
    fun executeGetCommentListByBoardId(boardId: Long): Result<List<CommentGetResponse>> {
        return getCommentListByBoardId(
            boardId,
            findBoard = { id -> boardRepository.findByIdOrNull(id) },
            commentList = { board -> commentRepository.findAllByBoard(board) }
        )
    }

    // 게시글 내 댓글 리스트 조회 (순수함수)
    private fun getCommentListByBoardId(
        boardId: Long,
        findBoard : (Long) -> Board?,
        commentList : (Board) -> List<Comment>
    ): Result<List<CommentGetResponse>> {
        return runCatching {
            val board = findBoard(boardId)
            val comments = commentList(board?: throw IllegalArgumentException("게시글을 찾을 수 없습니다."))
            comments.map { CommentGetResponse.fromEntity(it) }
        }
    }
    // 댓글 수정(부수효과 포함 로직)
    @Transactional
    fun executeEditComment(commentId: Long, request: CommentEditRequest): Result<CommentEditResponse> {
        return editComment(
            commentId = commentId,
            request = request,
            findComment = { id -> commentRepository.findByIdOrNull(id) },
            getUserId = { sessionUtil.getCurrentUserId() }
        )
    }
    // 댓글 수정(순수 함수)
    private fun editComment(
        commentId: Long,
        request: CommentEditRequest,
        findComment: (Long) -> Comment?,
        getUserId: () -> Long
    ): Result<CommentEditResponse> {
        return runCatching {
            val comment = findComment(commentId)
                ?: throw IllegalArgumentException("댓글을 찾을 수 없습니다.")
            val userId = getUserId()
            when (userId == comment.user.id) {
                true -> comment.updateComment(request)
                else -> throw IllegalArgumentException("당사자만 수정할 수 있습니다.")
            }
            CommentEditResponse().fromEntity(comment)
        }
    }

    @Transactional
    fun executeDeleteComment(commentId: Long): Result<String> {
        return deleteCommentInternal(
            commentId = commentId,
            getUserId = { sessionUtil.getCurrentUserId() },
            findComment = { id -> commentRepository.findByIdOrNull(id) },
            deleteComment = { comment -> commentRepository.delete(comment) }
        )
    }

    // 순수 함수
    private fun deleteCommentInternal(
        commentId: Long,
        getUserId: () -> Long,
        findComment: (Long) -> Comment?,
        deleteComment: (Comment) -> Unit
    ): Result<String> {
        return runCatching {
            val userId = getUserId()
            val comment = findComment(commentId)
                ?: throw IllegalArgumentException("댓글을 찾을 수 없습니다.")
            if (userId != comment.user.id) {
                throw IllegalArgumentException("당사자만 삭제할 수 있습니다.")
            }
            deleteComment(comment)
            "댓글 삭제 완료"
        }
    }
}