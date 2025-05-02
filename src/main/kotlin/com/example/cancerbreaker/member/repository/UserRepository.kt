package com.example.cancerbreaker.member.repository

import com.example.cancerbreaker.member.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?

    fun findByUserId(userId: String): User?

//    fun findByIdOrNull(id: Long): User?
}