package com.example.cancerbreaker.member.service

import com.example.cancerbreaker.global.config.PasswordEncoderConfig
import com.example.cancerbreaker.global.util.SessionUtil
import com.example.cancerbreaker.member.dto.UserLoginRequest
import com.example.cancerbreaker.member.dto.UserLoginResponse
import com.example.cancerbreaker.member.dto.UserRegisterRequest
import com.example.cancerbreaker.member.dto.UserRegisterResponse
import com.example.cancerbreaker.member.entity.User
import com.example.cancerbreaker.member.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoderConfig,
    private val sessionUtil: SessionUtil
) {

    @Transactional
    fun register(request: UserRegisterRequest): Result<UserRegisterResponse> {
        return registerInternal(
            request = request,
            findByUserId = { userId -> userRepository.findByUserId(userId) },
            encodePassword = { password -> passwordEncoder.encode(password) },
            saveUser = { user -> userRepository.save(user) }
        )
    }

    private fun registerInternal(
        request: UserRegisterRequest,
        findByUserId: (String) -> User?,
        encodePassword: (String) -> String,
        saveUser: (User) -> User
    ): Result<UserRegisterResponse> {
        return runCatching {
            if (findByUserId(request.userId) != null) {
                throw IllegalArgumentException("이미 존재하는 아이디입니다.")
            }
            val hashedPassword = encodePassword(request.password)
            val user = User(
                userId = request.userId,
                username = request.username,
                password = hashedPassword,
                role = request.role
            )
            val savedUser = saveUser(user)
            UserRegisterResponse().fromEntity(savedUser)
        }
    }

    @Transactional(readOnly = true)
    fun login(request: UserLoginRequest): Result<UserLoginResponse> {
        return loginInternal(
            request = request,
            findByUserId = { userId -> userRepository.findByUserId(userId) },
            matchesPassword = { raw, encoded -> passwordEncoder.matches(raw, encoded) },
            setSession = { userId -> sessionUtil.setCurrentUserId(userId) }
        )
    }

    private fun loginInternal(
        request: UserLoginRequest,
        findByUserId: (String) -> User?,
        matchesPassword: (String, String) -> Boolean,
        setSession: (Long) -> Unit
    ): Result<UserLoginResponse> {
        return runCatching {
            val user = findByUserId(request.userId)
                ?: throw IllegalArgumentException("Invalid username or password")
            if (!matchesPassword(request.password, user.password)) {
                throw IllegalArgumentException("Invalid username or password")
            }
            setSession(user.id!!)
            UserLoginResponse(user.id, user.userId, user.username)
        }
    }

    fun checkSession(): Result<String> {
        return checkSessionInternal(
            getUserId = { sessionUtil.getCurrentUserId() }
        )
    }

    private fun checkSessionInternal(
        getUserId: () -> Long?
    ): Result<String> {
        return runCatching {
            getUserId() ?: throw IllegalArgumentException("Not logged in")
            "Session is valid"
        }
    }

    fun logout(): Result<String> {
        return logoutInternal(
            invalidateSession = { sessionUtil.invalidateSession() }
        )
    }

    private fun logoutInternal(
        invalidateSession: () -> Unit
    ): Result<String> {
        return runCatching {
            invalidateSession()
            "Logout successful"
        }
    }

    fun me(): Result<Map<String, Long>> {
        return meInternal(
            getUserId = { sessionUtil.getCurrentUserId() }
        )
    }

    private fun meInternal(
        getUserId: () -> Long?
    ): Result<Map<String, Long>> {
        return runCatching {
            val userId = getUserId() ?: throw IllegalArgumentException("Not logged in")
            mapOf("id" to userId)
        }
    }
}