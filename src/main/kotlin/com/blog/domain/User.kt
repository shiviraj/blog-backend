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
    var uniqueId: String = "",
    var name: String = "",
    var userId: String = "",
    var email: String = "",
    var emailVerified: Boolean = false,
    var profile: String = "",
    var location: String? = null,
    var source: LoginSource? = null,
    val registeredAt: LocalDateTime = LocalDateTime.now(),
    val role: Role = Role.USER,
    val tokens: MutableSet<Token> = mutableSetOf(),
    val username: String
) {
    fun updateUser(userId: String, githubUser: GithubUser, githubUserEmail: GithubUserEmail) {
        this.uniqueId = githubUser.id.toString()
        this.userId = userId
        this.name = if (githubUser.name.isNullOrEmpty()) githubUser.username else githubUser.name
        this.profile = githubUser.profile
        this.email = githubUserEmail.email
        this.emailVerified = githubUserEmail.verified
        this.location = githubUser.location
        this.source = githubUser.source
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
