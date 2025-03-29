package com.example.cancerbreaker.global.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Aspect
@Component
class LogAspect {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)
    @Pointcut("within(com.example.cancerbreaker..*)")
    fun logMethod() {}

    @Around("logMethod()")
    fun logExecutionTime(joinPoint: ProceedingJoinPoint): Any? {
        val start = System.currentTimeMillis()
        val request = (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)?.request
        val httpMethod = request?.method ?: "UNKNOWN"
        val requestURI = request?.requestURI ?: "UNKNOWN"
        val queryParams = request?.parameterMap?.map { (key, value) -> "$key=${value.joinToString(",")}" }?.joinToString("&") ?: "NONE"


        log.info("[START] {} 실행 | \n" +
                "HTTP Method: {} | \n" +
                "URI: {} | \n"  +
                "Query Params: {} | \n",
            joinPoint.signature, httpMethod, requestURI, queryParams
        )

        val result = try {
            joinPoint.proceed()
        } catch (e: Throwable) {
            log.error("[ERROR] {} 실행 중 오류 발생: {}", joinPoint.signature, e.message, e)
            throw e
        }

        val end = System.currentTimeMillis()
        log.info("[END] {} 실행 완료 (소요 시간: {}ms)", joinPoint.signature, end - start)


        return result
    }

}