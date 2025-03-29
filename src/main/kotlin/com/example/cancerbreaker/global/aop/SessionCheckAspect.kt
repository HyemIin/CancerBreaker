package com.example.cancerbreaker.global.aop

import jakarta.servlet.http.HttpSession
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class SessionCheck

@Aspect
@Component
class SessionCheckAspect(
    private val session: HttpSession

) {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)
    @Before("@annotation(com.example.cancerbreaker.global.aop.SessionCheck)")
    fun checkSession() {
        val userId = session.getAttribute("id") as Long?
        if (userId == null) {
            log.error("비로그인 상태 입니다. 로그인 해주세요.")
        }
    }
}