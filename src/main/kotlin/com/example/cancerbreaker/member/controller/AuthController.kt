package com.example.cancerbreaker.member.controller

import com.example.cancerbreaker.global.util.ApiResponse
import com.example.cancerbreaker.member.dto.UserLoginRequest
import com.example.cancerbreaker.member.dto.UserLoginResponse
import com.example.cancerbreaker.member.dto.UserRegisterRequest
import com.example.cancerbreaker.member.dto.UserRegisterResponse
import com.example.cancerbreaker.member.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/register")
    fun register(@RequestBody request: UserRegisterRequest): ResponseEntity<ApiResponse<UserRegisterResponse>> {
        return authService.register(request)
            .fold(
                onSuccess = { ResponseEntity.ok(ApiResponse.Success(it, "회원가입 성공")) },
                onFailure = { ResponseEntity.badRequest().body(ApiResponse.Error(it.message ?: "알 수 없는 오류")) }
            )
    }

    @PostMapping("/login")
    fun login(@RequestBody request: UserLoginRequest): ResponseEntity<ApiResponse<UserLoginResponse>> {
        return authService.login(request)
            .fold(
                onSuccess = { ResponseEntity.ok(ApiResponse.Success(it, "로그인 성공")) },
                onFailure = { ResponseEntity.badRequest().body(ApiResponse.Error(it.message ?: "알 수 없는 오류")) }
            )
    }

    @GetMapping("/check-session")
    fun checkSession(): ResponseEntity<ApiResponse<String>> {
        return authService.checkSession()
            .fold(
                onSuccess = { ResponseEntity.ok(ApiResponse.Success(it, "세션 유효")) },
                onFailure = { ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.Error(it.message ?: "알 수 없는 오류")) }
            )
    }

    @PostMapping("/logout")
    fun logout(): ResponseEntity<ApiResponse<String>> {
        return authService.logout()
            .fold(
                onSuccess = { ResponseEntity.ok(ApiResponse.Success(it, "로그아웃 성공")) },
                onFailure = { ResponseEntity.badRequest().body(ApiResponse.Error(it.message ?: "알 수 없는 오류")) }
            )
    }

    @GetMapping("/me")
    fun me(): ResponseEntity<ApiResponse<Map<String, Long>>> {
        return authService.me()
            .fold(
                onSuccess = { ResponseEntity.ok(ApiResponse.Success(it, "사용자 정보 조회 성공")) },
                onFailure = { ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.Error(it.message ?: "알 수 없는 오류")) }
            )
    }
}