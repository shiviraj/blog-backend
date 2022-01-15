package com.blog.service

import com.blog.domain.GithubUser
import com.blog.domain.GithubUserEmail
import com.blog.domain.Token
import com.blog.domain.User
import com.blog.repository.UserRepository
import com.blog.security.WebToken
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserService(
    val userRepository: UserRepository,
    val idGeneratorService: IdGeneratorService,
    val webToken: WebToken
) {

    fun signInUserFromOauth(githubUser: GithubUser, githubUserEmail: GithubUserEmail): Mono<Pair<Token, User>> {
        return userRepository.findByUsername(githubUser.username)
            .switchIfEmpty(registerNewUser(githubUser, githubUserEmail))
            .flatMap {
                val token = webToken.generateToken(it)
                save(it.addToken(token))
                    .map { user ->
                        Pair(token, user)
                    }
                    .logOnSuccess("Successfully login user", mapOf("username" to githubUser.username))
            }
    }

    private fun registerNewUser(githubUser: GithubUser, githubUserEmail: GithubUserEmail): Mono<User> {
        return idGeneratorService.generateId(IdType.UserId)
            .flatMap { userId ->
                save(
                    User(
                        uniqueId = githubUser.id.toString(),
                        name = githubUser.name ?: githubUser.username,
                        userId = userId,
                        email = githubUserEmail.email,
                        emailVerified = githubUserEmail.verified,
                        profile = githubUser.profile,
                        location = githubUser.location,
                        source = githubUser.source,
                        username = githubUser.username
                    )
                )
            }
            .logOnSuccess("Successfully registered a new user", mapOf("user" to githubUser.username))
            .logOnError("Failed to register a new user", mapOf("user" to githubUser.username))
    }

    fun logoutUser(user: User, token: String): Mono<User> {
        return save(user.removeToken(Token(token)))
            .logOnSuccess("Successfully logout user", mapOf("user" to user.userId))
            .logOnError("Failed to logout user", mapOf("user" to user.userId))
    }

    fun getUserByUserId(userId: String): Mono<User> {
        return userRepository.findByUserId(userId)
            .logOnSuccess("Successfully fetched user from db", mapOf("user" to userId))
            .logOnError("Failed to fetch user from db", mapOf("user" to userId))
    }

    private fun save(user: User) = userRepository.save(user)
}

typealias AuthorService = UserService
