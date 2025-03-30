package com.example.cancerbreaker.member.dto

import com.example.cancerbreaker.member.entity.User

data class MemberGetResponse(
    val id : Long?,
    val userId : String,
    val username: String
) {
    companion object {
        fun fromEntity(user: User): MemberGetResponse {
            return MemberGetResponse(
                id = user.id,
                userId = user.userId,
                username = user.username
            )
        }
    }
}
