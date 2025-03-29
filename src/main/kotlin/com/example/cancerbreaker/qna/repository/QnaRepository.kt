package com.example.cancerbreaker.qna.repository

import com.example.cancerbreaker.qna.entity.Qna
import org.springframework.data.jpa.repository.JpaRepository

interface QnaRepository : JpaRepository<Qna,Long> {
}