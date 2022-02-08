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
) {
    companion object {
        fun createDummy(userId: UserId): User {
            return User(
                uniqueId = "unique",
                name = "Dummy",
                userId = userId,
                email = "email",
                emailVerified = false,
                profile = "",
                location = null,
                source = LoginSource.DUMMY,
                role = Role.DUMMY,
                username = "dummy"
            )
        }
    }
}

enum class Role(private val sequenceId: Int) {
    DUMMY(0),
    USER(1),
    ADMIN(2),
    OWNER(3);

    fun isJunior(allowedRole: Role) = this.sequenceId < allowedRole.sequenceId
}

enum class LoginSource {
    DUMMY,
    GITHUB
}

typealias Author = User
typealias AuthorId = String
typealias UserId = String
