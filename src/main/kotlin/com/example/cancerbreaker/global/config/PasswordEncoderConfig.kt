package com.example.cancerbreaker.global.config

import org.mindrot.jbcrypt.BCrypt
import org.springframework.stereotype.Component


@Component
class PasswordEncoderConfig {

    // 비밀번호를 암호화하는 메서드
    fun encode(rawPassword: String): String {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt())
    }

    // 비밀번호를 검증하는 메서드
    fun matches(rawPassword: String, encodedPassword: String): Boolean {
        return BCrypt.checkpw(rawPassword, encodedPassword)
    }
}