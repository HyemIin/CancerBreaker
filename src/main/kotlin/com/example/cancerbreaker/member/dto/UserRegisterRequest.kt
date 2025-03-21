package com.example.cancerbreaker.member.dto

import com.example.cancerbreaker.member.entity.Role

data class UserRegisterRequest(
    val userId: String,
    val username: String,
    val password: String,
    val role: Role
)
