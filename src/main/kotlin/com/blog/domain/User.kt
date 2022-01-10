package com.blog.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

const val USER_COLLECTION = "users"

@TypeAlias("User")
@Document(USER_COLLECTION)
data class User(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val uniqueId: String,
    val name: String,
    val userId: String,
    val email: String,
    val emailVerified: Boolean,
    val profile: String,
    val location: String?,
    val source: LoginSource,
    val registeredAt: LocalDateTime = LocalDateTime.now(),
    val role: Role = Role.USER,
    val username: String,
    val tokens: MutableSet<Token> = mutableSetOf()
) {
    fun addToken(token: Token): User {
        tokens.add(token)
        return this
    }

    fun removeToken(token: Token): User {
        tokens.removeIf { it == token }
        return this
    }
}

enum class Role {
    USER,
    ADMIN,
    OWNER
}

enum class LoginSource {
    GITHUB
}

data class Token(val token: String)

typealias Author = User
typealias AuthorId = String
typealias UserId = String
