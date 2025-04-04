package com.example.cancerbreaker.board.service

import com.example.cancerbreaker.board.dto.request.BoardCreateRequest
import com.example.cancerbreaker.board.dto.request.BoardEditRequest
import com.example.cancerbreaker.board.dto.response.BoardCreateResponse
import com.example.cancerbreaker.board.dto.response.BoardGetResponse
import com.example.cancerbreaker.board.entity.Board
import com.example.cancerbreaker.board.entity.BoardCategory
import com.example.cancerbreaker.board.repository.BoardRepository
import com.example.cancerbreaker.global.util.ApiResponse
import com.example.cancerbreaker.global.util.Error
import com.example.cancerbreaker.global.util.SessionUtil
import com.example.cancerbreaker.global.util.Success
import com.example.cancerbreaker.gpt.service.ChatGptService
import com.example.cancerbreaker.member.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
class BoardService(
    private val boardRepository: BoardRepository,
    private val userRepository: UserRepository,
    private val chatGptService: ChatGptService,
    private val sessionUtil: SessionUtil
) {

    @Transactional
    fun createBoard(boardCreateRequest: BoardCreateRequest): ApiResponse<BoardCreateResponse> {
        val userId = sessionUtil.getCurrentUserId()
        val user = userRepository.findByIdOrNull(userId)
            ?: return Error("사용자를 찾을 수 없습니다.")

        val board = Board(boardCreateRequest.title, boardCreateRequest.content, boardCreateRequest.category, user)
        val savedBoard = boardRepository.save(board)

        return Success(BoardCreateResponse().fromEntity(savedBoard), "게시글 생성 완료")
    }

    @Transactional(readOnly = true)
    fun getBoardListByCategory(category: BoardCategory): ApiResponse<List<BoardGetResponse>> {
        val boards = boardRepository.findByCategory(category)
        return boards?.map { BoardGetResponse().fromEntity(it) }?.let {
            Success(it, "Board list retrieved successfully for category: $category")
        } ?: Error("No boards found for category: $category")
    }


    @Transactional(readOnly = true)
    fun getBoardByBoardId(boardId: Long) : BoardGetResponse? {
        val board = boardRepository.findByIdOrNull(boardId)
        return BoardGetResponse().fromEntity(board!!)
    }

    @Transactional
    fun editBoardByBoardId(boardId: Long,boardEditRequest: BoardEditRequest) : Board? {
        val userId = sessionUtil.getCurrentUserId()
        val board = boardRepository.findByIdOrNull(boardId)
        when (userId) {
            board!!.user.id -> board.updateBoard(boardEditRequest)
            else -> throw IllegalArgumentException("당사자만 수정할 수 있습니다.")
        }
        return board
    }
    @Transactional
    fun deleteBoardByBoardId(boardId : Long) {
        val userId = sessionUtil.getCurrentUserId()
        val board = boardRepository.findByIdOrNull(boardId)
        when (userId) {
            board!!.user.id -> boardRepository.delete(board)
            else -> throw IllegalArgumentException("당사자만 삭제할 수 있습니다.")
        }

    }
    @Transactional(readOnly = true)
    fun getAllBoardList(): ApiResponse<List<BoardGetResponse>> {
        val boards = boardRepository.findAll()
        return Success(boards.map { BoardGetResponse().fromEntity(it) }, "게시글 전체 리스트 조회 완료")
    }

    @Transactional(readOnly = true)
    fun searchBoard(keyword:String) : List<Board> {
        val modifiedKeyword = "$keyword*"  // 와일드카드 추가
        return boardRepository.search(modifiedKeyword)
    }

    @Transactional
    fun boardSummary(boardId: Long) : Mono<String> {
        val boardContent = boardRepository.findById(boardId)
        return chatGptService.askGPT("아래 글 요약해줘"+ boardContent.get().content)
    }
}