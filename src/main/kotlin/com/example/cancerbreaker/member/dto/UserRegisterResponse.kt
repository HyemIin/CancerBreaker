package com.example.cancerbreaker.member.dto

import com.example.cancerbreaker.member.entity.Role
import com.example.cancerbreaker.member.entity.User

data class UserRegisterResponse(
    val userId: String,
    val username: String,
    val password: String,
    val role: Role
) {
    fun fromEntity(user: User): User =
        User(userId = user.userId, username = user.username, password = user.password, role = user.role)

}
