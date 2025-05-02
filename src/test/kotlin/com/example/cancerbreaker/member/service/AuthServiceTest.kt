package com.example.cancerbreaker.member.service

import com.example.cancerbreaker.global.config.PasswordEncoderConfig
import com.example.cancerbreaker.global.util.SessionUtil
import com.example.cancerbreaker.member.dto.UserLoginRequest
import com.example.cancerbreaker.member.dto.UserRegisterRequest
import com.example.cancerbreaker.member.entity.Role
import com.example.cancerbreaker.member.entity.User
import com.example.cancerbreaker.member.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthServiceTest {
    private lateinit var authService: AuthService
    private val userRepository: UserRepository = mockk()
    private val passwordEncoder: PasswordEncoderConfig = mockk()
    private val sessionUtil: SessionUtil = mockk()

    private val testUser = User(
        id = 1L,
        userId = "testuser",
        username = "Test User",
        password = "hashedPassword",
        role = Role.PATIENT
    )

    @BeforeEach
    fun setUp() {
        authService = AuthService(userRepository, passwordEncoder, sessionUtil)
    }

    @Test
    fun `register should create new user successfully`() {
        // Given
        val request = UserRegisterRequest(
            userId = "testuser",
            username = "Test User",
            password = "password",
            role = Role.PATIENT
        )

        every { userRepository.findByUserId("testuser") } returns null
        every { passwordEncoder.encode("password") } returns "hashedPassword"
        every { userRepository.save(any()) } returns testUser

        // When
        val result = authService.register(request)

        // Then
        assertTrue(result.isSuccess)
        result.getOrNull()?.let { response ->
            assertEquals("testuser", response.userId)
            assertEquals("Test User", response.username)
        }

        verify {
            userRepository.findByUserId("testuser")
            passwordEncoder.encode("password")
            userRepository.save(any())
        }
    }

    @Test
    fun `register should fail when userId already exists`() {
        // Given
        val request = UserRegisterRequest(
            userId = "testuser",
            username = "Test User",
            password = "password",
            role = Role.PATIENT
        )

        every { userRepository.findByUserId("testuser") } returns testUser

        // When
        val exception = assertThrows<IllegalArgumentException> {
            authService.register(request).getOrThrow()
        }

        // Then
        assertEquals("이미 존재하는 아이디입니다.", exception.message)
        verify { userRepository.findByUserId("testuser") }
    }

    @Test
    fun `login should succeed with correct credentials`() {
        // Given
        val request = UserLoginRequest(
            userId = "testuser",
            password = "password"
        )

        every { userRepository.findByUserId("testuser") } returns testUser
        every { passwordEncoder.matches("password", "hashedPassword") } returns true
        every { sessionUtil.setCurrentUserId(1L) } returns Unit

        // When
        val result = authService.login(request)

        // Then
        assertTrue(result.isSuccess)
        result.getOrNull()?.let { response ->
            assertEquals(1L, response.id)
            assertEquals("testuser", response.userId)
            assertEquals("Test User", response.username)
        }

        verify {
            userRepository.findByUserId("testuser")
            passwordEncoder.matches("password", "hashedPassword")
            sessionUtil.setCurrentUserId(1L)
        }
    }

    @Test
    fun `login should fail with invalid userId`() {
        // Given
        val request = UserLoginRequest(
            userId = "wronguser",
            password = "password"
        )

        every { userRepository.findByUserId("wronguser") } returns null

        // When
        val exception = assertThrows<IllegalArgumentException> {
            authService.login(request).getOrThrow()
        }

        // Then
        assertEquals("Invalid username", exception.message)
        verify { userRepository.findByUserId("wronguser") }
    }

    @Test
    fun `login should fail with invalid password`() {
        // Given
        val request = UserLoginRequest(
            userId = "testuser",
            password = "wrongpassword"
        )

        every { userRepository.findByUserId("testuser") } returns testUser
        every { passwordEncoder.matches("wrongpassword", "hashedPassword") } returns false

        // When
        val exception = assertThrows<IllegalArgumentException> {
            authService.login(request).getOrThrow()
        }

        // Then
        assertEquals("Invalid password", exception.message)
        verify {
            userRepository.findByUserId("testuser")
            passwordEncoder.matches("wrongpassword", "hashedPassword")
        }
    }

    @Test
    fun `checkSession should return success when user is logged in`() {
        // Given
        every { sessionUtil.getCurrentUserId() } returns 1L

        // When
        val result = authService.checkSession()

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Session is valid", result.getOrNull())
        verify { sessionUtil.getCurrentUserId() }
    }

    @Test
    fun `checkSession should fail when user is not logged in`() {
        // Given
        every { sessionUtil.getCurrentUserId() } returns 1L

        // When
        val exception = assertThrows<IllegalArgumentException> {
            authService.checkSession().getOrThrow()
        }

        // Then
        assertEquals("Not logged in", exception.message)
        verify { sessionUtil.getCurrentUserId() }
    }

    @Test
    fun `logout should succeed`() {
        // Given
        every { sessionUtil.invalidateSession() } returns Unit

        // When
        val result = authService.logout()

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Logout successful", result.getOrNull())
        verify { sessionUtil.invalidateSession() }
    }

    @Test
    fun `me should return user id when logged in`() {
        // Given
        every { sessionUtil.getCurrentUserId() } returns 1L

        // When
        val result = authService.me()

        // Then
        assertTrue(result.isSuccess)
        result.getOrNull()?.let { response ->
            assertEquals(1L, response["id"])
        }
        verify { sessionUtil.getCurrentUserId() }
    }

    @Test
    fun `me should fail when not logged in`() {
        // Given
        every { sessionUtil.getCurrentUserId() } returns 1L

        // When
        val exception = assertThrows<IllegalArgumentException> {
            authService.me().getOrThrow()
        }

        // Then
        assertEquals("Not logged in", exception.message)
        verify { sessionUtil.getCurrentUserId() }
    }
}