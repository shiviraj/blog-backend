package com.blog.controller.view

import com.blog.domain.Role
import com.blog.domain.User
import java.time.LocalDateTime

data class UserView(
    val username: String,
    val name: String,
    val userId: String,
    val email: String?,
    val profile: String,
    val registeredAt: LocalDateTime,
    val role: Role,
) {
    companion object {
        fun from(user: User): UserView {
            return UserView(
                username = user.username,
                name = user.name,
                userId = user.userId,
                email = user.email,
                profile = user.profile,
                registeredAt = user.registeredAt,
                role = user.role
            )
        }
    }
}
