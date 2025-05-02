package com.example.cancerbreaker.member.entity

import com.example.cancerbreaker.global.entity.BaseEntity
import com.fasterxml.jackson.annotation.JsonCreator
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class User private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    var userId: String = "",
    var username: String = "",
    var password: String = "",
    var role: Role = Role.PATIENT
): BaseEntity() {
    protected constructor() : this(null, "", "", "", Role.PATIENT) {}
//    init {
//        check(userId.isNotBlank() && username.isNotBlank() && password.isNotBlank()) {
//            "아이디, 이름, 패스워드는 빈 값일 수 없습니다."
//        }
//    }
    companion object{
        @JsonCreator
        fun from(
            id : Long?,
            userId: String,
            username: String,
            password: String,
            role: Role
        ): User {
            return User(id = id,userId = userId, username = username, password = password, role = role)
        }


        operator fun invoke(
            id: Long?,
            userId: String,
            username: String,
            password: String,
            role: Role
        ): User = from(id,userId, username, password, role)
    }
}
