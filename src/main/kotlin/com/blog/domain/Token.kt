package com.blog.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

const val TOKEN_COLLECTION = "tokens"

@TypeAlias("Token")
@Document(TOKEN_COLLECTION)
data class Token(
    @Id
    var id: ObjectId? = null,
    @Indexed(name = "sessionExpiryIndex", expireAfterSeconds = 604800)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val userId: String,
    val userType: UserType,
    private var value: String,
) {
    companion object {
        fun from(user: User): Token {
            return Token(
                userId = user.userId,
                value = "",
                userType = if (user.role == Role.DUMMY) UserType.DUMMY else UserType.USER
            )
        }
    }

    fun getValue() = this.value
    fun updateValue(value: String): Token {
        this.value = value
        return this
    }
}

enum class UserType {
    USER,
    DUMMY;

    fun isDummy() = this === DUMMY
}
