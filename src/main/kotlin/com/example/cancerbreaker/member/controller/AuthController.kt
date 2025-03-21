package com.example.cancerbreaker.member.controller

import com.example.cancerbreaker.member.dto.UserLoginRequest
import com.example.cancerbreaker.member.dto.UserRegisterRequest
import com.example.cancerbreaker.member.service.AuthService
import jakarta.servlet.http.HttpSession
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/register")
    fun register(@RequestBody request: UserRegisterRequest) =
        ResponseEntity.ok("User registered successfully").apply { authService.register(request) }

    @PostMapping("/login")
    fun login(@RequestBody request: UserLoginRequest, session: HttpSession): ResponseEntity<String> {
        session.setAttribute("id", authService.login(request).id)
        return ResponseEntity.ok("Login successful")
    }

    @GetMapping("check-session")
    fun checkSession(session: HttpSession): ResponseEntity<String> {
        return (session.getAttribute("id") as Long?)
            ?.let { ResponseEntity.ok("Session is valid") }
            ?: ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in")
    }

    @PostMapping("/logout")
    fun logout(session: HttpSession) =
        ResponseEntity.ok("Logout successful").apply { session.invalidate() }

    @GetMapping("/me")
    fun me(session: HttpSession): ResponseEntity<Any> {
        return (session.getAttribute("id") as Long?)
            ?.let { ResponseEntity.ok(mapOf("id" to it)) }
            ?: ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in")
    }
}