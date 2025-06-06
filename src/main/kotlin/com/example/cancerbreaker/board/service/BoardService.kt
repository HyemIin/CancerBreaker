package com.example.cancerbreaker.board.service

import com.example.cancerbreaker.board.dto.request.BoardCreateRequest
import com.example.cancerbreaker.board.dto.request.BoardEditRequest
import com.example.cancerbreaker.board.dto.response.BoardCreateResponse
import com.example.cancerbreaker.board.dto.response.BoardEditResponse
import com.example.cancerbreaker.board.dto.response.BoardGetResponse
import com.example.cancerbreaker.board.entity.Board
import com.example.cancerbreaker.board.entity.BoardCategory
import com.example.cancerbreaker.board.repository.BoardRepository
import com.example.cancerbreaker.global.util.SessionUtil
import com.example.cancerbreaker.gpt.service.ChatGptService
import com.example.cancerbreaker.member.entity.User
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
    // 게시글 생성 (부수효과 포함 로직)
    @Transactional
    fun executeCreateBoard(request: BoardCreateRequest): Result<BoardCreateResponse> {
        return createBoard(
            request,
            getUserId = { sessionUtil.getCurrentUserId() },
            findUser = { userId -> userRepository.findByIdOrNull(userId) },
            saveBoard = { board -> boardRepository.save(board) }
        )
    }
    // 게시글 생성(순수함수)
    private fun createBoard(
        request: BoardCreateRequest,
        getUserId: () -> Long,
        findUser: (Long) -> User?,
        saveBoard: (Board) -> Board
    ): Result<BoardCreateResponse> {
        return runCatching {
            val userId = getUserId()
            val user = findUser(userId) ?: throw IllegalArgumentException("사용자를 찾을 수 없습니다.")
            val board = createBoardEntity(request, user)
            val savedBoard = saveBoard(board)
            BoardCreateResponse(title = savedBoard.title, content = savedBoard.content, category = savedBoard.category)
        }
    }
    // BoardEntity 생성/(순수함수)
    private fun createBoardEntity(boardCreateRequest: BoardCreateRequest,user: User): Board {
        return Board(
            title = boardCreateRequest.title,
            content = boardCreateRequest.content,
            category = boardCreateRequest.category,
            user = user,
            comments = mutableListOf()
        )
    }
    // 게시글 전체 조회(부수효과 포함 로직)
    @Transactional(readOnly = true)
    fun excuteGetAllBoardList(): Result<List<BoardGetResponse>> {
        return getAllBoards(
            findAllBoards = { boardRepository.findAll() }
        )
    }
    // 게시글 전체 조회 (순수함수)
    private fun getAllBoards(
        findAllBoards: () -> List<Board>
    ): Result<List<BoardGetResponse>> {
        return runCatching {
            val boards = findAllBoards()
            check (boards.isNotEmpty()) {
                throw IllegalStateException("게시글 목록이 비어 있습니다.")
            }
            boards.map { BoardGetResponse.fromEntity(it) }
        }
    }

    // 카테고리별 게시글 리스트 조회
    @Transactional(readOnly = true)
    fun executeGetBoardListByCategory(category: BoardCategory): Result<List<BoardGetResponse>> {
        return getBoardListByCategory(
            category,
            findBoardsByCategory = { cat -> boardRepository.findByCategory(cat) }
        )
    }
    // 카테고리별 게시글 목록 조회 (비즈니스 로직, 순수 고차 함수)
    private fun getBoardListByCategory(
        category: BoardCategory,
        findBoardsByCategory: (BoardCategory) -> List<Board>?
    ): Result<List<BoardGetResponse>> {
        return runCatching {
            val boards = findBoardsByCategory(category)
                ?: throw IllegalStateException("No boards found for category: $category")
            boards.map { BoardGetResponse.fromEntity(it) }
        }
    }

    // 특정 게시글 조회
    @Transactional(readOnly = true)
    fun executeGetBoardByBoardId(boardId: Long): Result<BoardGetResponse> {
        return getBoardByBoardId(
            boardId,
            findBoard = { id -> boardRepository.findByIdOrNull(id) }
        )
    }

    // 특정 게시글 조회(비즈니스 로직, 순수 고차 함수)
    private fun getBoardByBoardId(
        boardId: Long,
        findBoard: (Long) -> Board?
    ): Result<BoardGetResponse> {
        return runCatching {
            val foundBoard = findBoard(boardId)
                ?: throw IllegalArgumentException("게시글을 찾을 수 없습니다.")
            BoardGetResponse.fromEntity(foundBoard)
        }
    }

    // 게시글 수정
    @Transactional
    fun editBoardByBoardId(boardId: Long, boardEditRequest: BoardEditRequest): Result<BoardEditResponse> {
        return editBoard(
            boardId = boardId,
            request = boardEditRequest,
            getUserId = { sessionUtil.getCurrentUserId() },
            findBoard = { id -> boardRepository.findByIdOrNull(id) }
        )
    }

    // 게시글 수정(비즈니스 로직, 순수 고차 함수)
    private fun editBoard(
        boardId: Long,
        request: BoardEditRequest,
        getUserId: () -> Long,
        findBoard: (Long) -> Board?
    ): Result<BoardEditResponse> {
        return runCatching {
            val userId = getUserId()
            val board = findBoard(boardId)
                ?: throw IllegalArgumentException("게시글을 찾을 수 없습니다.")
            require (userId == board.user.id) {
                throw IllegalArgumentException("당사자만 수정할 수 있습니다.")
            }
            board.updateBoard(request)
            BoardEditResponse.fromEntity(board)
        }
    }

    //게시글 삭제
    @Transactional
    fun deleteBoardByBoardId(boardId: Long): Result<String> {
        return deleteBoard(
            boardId = boardId,
            getUserId = { sessionUtil.getCurrentUserId() },
            findBoard = { id -> boardRepository.findByIdOrNull(id) },
            deleteBoard = { board -> boardRepository.delete(board) }
        )
    }

    // 게시글 삭제(비즈니스 로직, 순수 고차 함수)
    private fun deleteBoard(
        boardId: Long,
        getUserId: () -> Long,
        findBoard: (Long) -> Board?,
        deleteBoard: (Board) -> Unit
    ): Result<String> {
        return runCatching {
            val userId = getUserId()
            val board = findBoard(boardId)
                ?: throw IllegalArgumentException("게시글을 찾을 수 없습니다.")
            require (userId == board.user.id) {
                throw IllegalArgumentException("당사자만 삭제할 수 있습니다.")
            }
            deleteBoard(board)
            "Deletion completed"
        }
    }

    // 게시글 검색
    @Transactional(readOnly = true)
    fun searchBoard(keyword: String): Result<List<Board>> {
        return searchBoards(
            keyword = keyword,
            searchBoards = { modifiedKeyword -> boardRepository.search(modifiedKeyword) }
        )
    }

    // 게시글 검색(비즈니스 로직, 순수 고차 함수)
    private fun searchBoards(
        keyword: String,
        searchBoards: (String) -> List<Board>
    ): Result<List<Board>> {
        return runCatching {
            val modifiedKeyword = "$keyword*" // 와일드카드 추가
            val boards = searchBoards(modifiedKeyword)
            require (boards.isNotEmpty()) {
                throw IllegalArgumentException("No boards found for keyword: $keyword")
            }
            boards
        }
    }
    // 게시글 요약(GPT)
    @Transactional(readOnly = true)
    suspend fun boardSummary(boardId: Long): Result<String> {
        return getBoardSummary(
            boardId = boardId,
            findBoard = { id -> boardRepository.findByIdOrNull(id) },
            summarize = { content -> chatGptService.askGPT("아래 글 요약해줘: $content") }
        )
    }

    private suspend fun getBoardSummary(
        boardId: Long,
        findBoard: suspend (Long) -> Board?,
        summarize: suspend (String) -> String
    ): Result<String> {
        return runCatching {
            val board = findBoard(boardId)
                ?: throw IllegalArgumentException("게시글을 찾을 수 없습니다.")
            summarize(board.content)
        }
    }

    //동기 방식(테스트용)
    @Transactional(readOnly = true)
    fun synBoardSummary(boardId: Long): Result<String> {
        return synGetBoardSummary(
            boardId = boardId,
            findBoard = { id -> boardRepository.findByIdOrNull(id) },
            summarize = { content -> chatGptService.synAskGPT("아래 글 요약해줘: $content") }
        )
    }

    private fun synGetBoardSummary(
        boardId: Long,
        findBoard: (Long) -> Board?,
        summarize: (String) -> String
    ): Result<String> {
        return runCatching {
            val board = findBoard(boardId)
                ?: throw IllegalArgumentException("게시글을 찾을 수 없습니다.")
            summarize(board.content)
        }
    }
}