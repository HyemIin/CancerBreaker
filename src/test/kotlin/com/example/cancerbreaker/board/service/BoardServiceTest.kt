package com.example.cancerbreaker.board.service

import com.example.cancerbreaker.board.dto.request.BoardCreateRequest
import com.example.cancerbreaker.board.dto.request.BoardEditRequest
import com.example.cancerbreaker.board.entity.Board
import com.example.cancerbreaker.board.entity.BoardCategory
import com.example.cancerbreaker.board.repository.BoardRepository
import com.example.cancerbreaker.global.util.SessionUtil
import com.example.cancerbreaker.gpt.service.ChatGptService
import com.example.cancerbreaker.member.entity.Role
import com.example.cancerbreaker.member.entity.User
import com.example.cancerbreaker.member.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import reactor.core.publisher.Mono

@ExtendWith(MockitoExtension::class)
class BoardServiceTest {

    @Mock
    private lateinit var boardRepository: BoardRepository

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var chatGptService: ChatGptService

    @Mock
    private lateinit var sessionUtil: SessionUtil

    @InjectMocks
    private lateinit var boardService: BoardService

    private lateinit var user: User
    private lateinit var user2: User
    private lateinit var board: Board

    @BeforeEach
    fun setUp() {
        user = User(id = 1L, userId = "testUserId", username = "testUser", password = "password", role = Role.PATIENT)
        user2 = User(id = 2L, userId = "testUserId2", username = "testUser2", password = "password", role = Role.FAMILIY)
        println("user: $user")
        board = Board(
            title = "Test Title",
            content = "Test Content",
            category = BoardCategory.ANTI_CANCER,
            user = user,
            comments = emptyList()
        )
    }

    @Test
    fun `executeCreateBoard - 성공 케이스`() {
        // Given
        val request = BoardCreateRequest("Test Title", "Test Content", BoardCategory.ANTI_CANCER)
        `when`(sessionUtil.getCurrentUserId()).thenReturn(1L)
        `when`(userRepository.findByIdOrNull(1L)).thenReturn(user)
        `when`(boardRepository.save(any(Board::class.java))).thenReturn(board)

        // When
        val result = boardService.executeCreateBoard(request)

        // Then
        println(result)
        assertTrue(result.isSuccess)
        val response = result.getOrNull()
        assertEquals("Test Title", response?.title)
        assertEquals("Test Content", response?.content)
        assertEquals(BoardCategory.ANTI_CANCER, response?.category)

    }

    @Test
    fun `executeCreateBoard - 사용자 없음 실패 케이스`() {
        // Given
        val request = BoardCreateRequest("Test Title", "Test Content", BoardCategory.ANTI_CANCER)
        `when`(sessionUtil.getCurrentUserId()).thenReturn(1L)
        `when`(userRepository.findByIdOrNull(1L)).thenReturn(null)

        // When
        val result = boardService.executeCreateBoard(request)

        // Then
        assertTrue(result.isFailure)
        assertEquals("사용자를 찾을 수 없습니다.", result.exceptionOrNull()?.message)
    }

    @Test
    fun `excuteGetAllBoardList - 성공 케이스`() {
        // Given
        val boardList = listOf(board)
        `when`(boardRepository.findAll()).thenReturn(boardList)

        // When
        val result = boardService.excuteGetAllBoardList()

        // Then
        assertTrue(result.isSuccess)
        val responseList = result.getOrNull()
        assertEquals(1, responseList?.size)
        assertEquals("Test Title", responseList?.first()?.title)
    }

    @Test
    fun `excuteGetAllBoardList - 빈 리스트 실패 케이스`() {
        // Given
        `when`(boardRepository.findAll()).thenReturn(emptyList())

        // When
        val result = boardService.excuteGetAllBoardList()

        // Then
        assertTrue(result.isFailure)
        assertEquals("게시글 목록이 비어 있습니다.", result.exceptionOrNull()?.message)
    }

    @Test
    fun `executeGetBoardByBoardId - 성공 케이스`() {
        // Given
        `when`(boardRepository.findByIdOrNull(1L)).thenReturn(board)

        // When
        val result = boardService.executeGetBoardByBoardId(1L)

        // Then
        assertTrue(result.isSuccess)
        val response = result.getOrNull()
        assertEquals("Test Title", response?.title)
    }

    @Test
    fun `editBoardByBoardId - 성공 케이스`() {
        // Given
        val editRequest = BoardEditRequest("Updated Title", "Updated Content", BoardCategory.ANTI_CANCER)
        `when`(sessionUtil.getCurrentUserId()).thenReturn(1L)
        `when`(boardRepository.findByIdOrNull(1L)).thenReturn(board)

        // When
        val result = boardService.editBoardByBoardId(1L, editRequest)

        // Then
        assertTrue(result.isSuccess)
        val updatedBoard = result.getOrNull()
        assertEquals("Updated Title", updatedBoard?.title)
        assertEquals("Updated Content", updatedBoard?.content)
    }

    @Test
    fun `editBoardByBoardId - 수정 시 권한 없음 실패 케이스`() {
        // Given
        val editRequest = BoardEditRequest("Updated Title", "Updated Content", BoardCategory.ANTI_CANCER)
        `when`(sessionUtil.getCurrentUserId()).thenReturn(2L) // 다른 사용자
        `when`(boardRepository.findByIdOrNull(1L)).thenReturn(board)

        // When
        val result = boardService.editBoardByBoardId(1L, editRequest)

        // Then
        assertTrue(result.isFailure)
        assertEquals("당사자만 수정할 수 있습니다.", result.exceptionOrNull()?.message)
    }

    @Test
    fun `deleteBoardByBoardId - 성공 케이스`() {
        // Given
        `when`(sessionUtil.getCurrentUserId()).thenReturn(1L)
        `when`(boardRepository.findByIdOrNull(1L)).thenReturn(board)
        doNothing().`when`(boardRepository).delete(board)

        // When
        val result = boardService.deleteBoardByBoardId(1L)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Deletion completed", result.getOrNull())
    }

    @Test
    fun `deleteBoardByBoardId - 삭제 시 권한 없음 실패 케이스`() {
        // Given
        `when`(sessionUtil.getCurrentUserId()).thenReturn(2L) // 다른 사용자
        `when`(boardRepository.findByIdOrNull(1L)).thenReturn(board)

        // When
        val result = boardService.deleteBoardByBoardId(1L)

        // Then
        assertTrue(result.isFailure)
        assertEquals("당사자만 삭제할 수 있습니다.", result.exceptionOrNull()?.message)
    }

    @Test
    fun `searchBoard - 성공 케이스`() {
        // Given
        val keyword = "test"
        `when`(boardRepository.search("test*")).thenReturn(listOf(board))

        // When
        val result = boardService.searchBoard(keyword)

        // Then
        assertTrue(result.isSuccess)
        val boards = result.getOrNull()
        assertEquals(1, boards?.size)
        assertEquals("Test Title", boards?.first()?.title)
    }

    @Test
    fun `boardSummary - 성공 케이스`() {
        // Given
        `when`(boardRepository.findByIdOrNull(1L)).thenReturn(board)
        `when`(chatGptService.askGPT("아래 글 요약해줘: Test Content")).thenReturn(Mono.just("Summary"))

        // When
        val result = boardService.boardSummary(1L)

        // Then
        assertTrue(result.isSuccess)
        val summaryMono = result.getOrNull()
        val summary = summaryMono?.block()
        assertEquals("Summary", summary)
    }
}