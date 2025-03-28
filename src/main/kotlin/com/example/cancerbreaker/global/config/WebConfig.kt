package com.example.cancerbreaker.global.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**") // 모든 경로에 대해 CORS 허용
            .allowedOrigins("http://example.com", "http://localhost:3000") // 허용할 도메인 설정
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드 설정
            .allowedHeaders("*") // 허용할 헤더 설정
            .allowCredentials(true) // 자격 증명(쿠키 등) 허용
    }
}