package com.blog.security

import com.blog.domain.Role
import com.blog.domain.User
import java.time.LocalDateTime
import java.time.ZoneOffset

data class UserId(
    val userId: String,
    val uniqueId: String,
    val username: String,
    val issuedAt: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
    val expiredAt: Long = LocalDateTime.now().plusDays(7).toEpochSecond(ZoneOffset.UTC),
    val role: Role = Role.USER,
    val token: String = ""
) {
    companion object {
        fun from(user: User): UserId {
            return UserId(
                userId = user.userId,
                uniqueId = user.uniqueId,
                username = user.username,
                role = user.role
            )
        }
    }

    fun isValid(): Boolean {
        val currentTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        return currentTime in issuedAt until expiredAt
    }
}
