package com.example.cancerbreaker.member.entity

import com.example.cancerbreaker.global.entity.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val userId: String = "",
    val username: String = "",
    val password: String = "",
    val role: Role = Role.PATIENT
): BaseEntity()
