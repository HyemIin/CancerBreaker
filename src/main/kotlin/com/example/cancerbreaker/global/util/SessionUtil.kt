package com.example.cancerbreaker.global.util

import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Component

@Component
class SessionUtil(private val session: HttpSession) {

    fun getCurrentUserId(): Long {
        return session.getAttribute("id") as Long?
            ?: throw IllegalArgumentException("User not logged in")
    }
}