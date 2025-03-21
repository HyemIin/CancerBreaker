package com.example.cancerbreaker.member.service

import com.example.cancerbreaker.global.config.PasswordEncoderConfig
import com.example.cancerbreaker.member.dto.UserLoginRequest
import com.example.cancerbreaker.member.dto.UserRegisterRequest
import com.example.cancerbreaker.member.entity.User
import com.example.cancerbreaker.member.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class AuthService(private val userRepository: UserRepository, private val passwordEncoder: PasswordEncoderConfig) {

    fun register(request: UserRegisterRequest): User {
        if (userRepository.findByUserId(request.userId) != null) {
            throw IllegalArgumentException("Username already exists")
        }
        val hashedPassword = passwordEncoder.encode(request.password)
        val user = User(userId = request.userId, username = request.username, password = hashedPassword, role = request.role)
        return userRepository.save(user)
    }

    fun login(request: UserLoginRequest): User {
        val user = userRepository.findByUserId(request.userId)
            ?: throw IllegalArgumentException("Invalid username or password")

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw IllegalArgumentException("Invalid username or password")
        }
        return user
    }
}