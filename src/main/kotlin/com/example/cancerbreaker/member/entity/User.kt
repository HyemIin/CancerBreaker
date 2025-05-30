package com.example.cancerbreaker.member.entity

import com.example.cancerbreaker.global.entity.BaseEntity
import com.fasterxml.jackson.annotation.JsonCreator
import jakarta.persistence.Column
import jakarta.persistence.Entity

@Entity
class User private constructor(
    userId: String,
    username: String,
    password: String,
    role: Role
): BaseEntity() {
    @Column(nullable = false)
    var userId: String = userId
        protected set
    @Column(nullable = false)
    var username: String = username
        protected set
    @Column(nullable = false)
    var password: String = password
        protected set
    @Column(nullable = false)
    var role: Role = role
        protected set
    protected constructor() : this("", "", "",  Role.PATIENT) {}
    companion object{
        @JsonCreator
        fun from(
            userId: String,
            username: String,
            password: String,
            role: Role
        ): User {
            return User(userId = userId, username = username, password = password, role = role)
        }


        operator fun invoke(
            userId: String,
            username: String,
            password: String,
            role: Role
        ): User = from(userId, username, password, role)
    }
}
