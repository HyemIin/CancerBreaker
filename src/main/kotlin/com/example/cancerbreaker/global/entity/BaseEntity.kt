package com.example.cancerbreaker.global.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@MappedSuperclass
abstract class BaseEntity {

    @Column(updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    var updatedAt: LocalDateTime? = null

    @PreUpdate
    fun setUpdatedAt() {
        updatedAt = LocalDateTime.now()
    }
}