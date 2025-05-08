package com.example.cancerbreaker.member.dto

import com.example.cancerbreaker.member.entity.Role
import com.example.cancerbreaker.member.entity.User

data class UserGetDTO(
    val userId: String? ="",
    val username: String? ="",
    val password: String? = "",
    val role: Role? = Role.PATIENT
) {
    companion object {
        fun fromEntity(user: User): UserGetDTO =
            UserGetDTO(userId = user.userId, username = user.username, password = user.password, role = user.role)

    }
}
