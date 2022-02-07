package com.blog.service

import com.blog.domain.GithubUser
import com.blog.domain.GithubUserEmail
import com.blog.domain.Token
import com.blog.domain.User
import com.blog.repository.UserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserService(
    val userRepository: UserRepository,
    val idGeneratorService: IdGeneratorService,
    val tokenService: TokenService
) {

    fun signInUserFromOauth(githubUser: GithubUser, githubUserEmail: GithubUserEmail): Mono<User> {
        return userRepository.findByUsername(githubUser.username)
            .switchIfEmpty(registerNewUser(githubUser, githubUserEmail))
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

    fun getUserByUserId(userId: String): Mono<User> {
        return userRepository.findByUserId(userId)
            .logOnSuccess("Successfully fetched user from db", mapOf("user" to userId))
            .logOnError("Failed to fetch user from db", mapOf("user" to userId))
    }

    private fun save(user: User) = userRepository.save(user)
    fun extractUser(token: String): Mono<User> {
        return tokenService.extractToken(token)
            .flatMap {
                if (it.userType.isDummy()) {
                    Mono.just(User.createDummy(it.userId))
                } else {
                    userRepository.findByUserId(it.userId)
                }
            }.logOnSuccess("Successfully fetched user from token")
            .logOnError("Failed to fetch user from token")
    }

    fun getDummyUser(): Mono<Pair<Token, User>> {
        return idGeneratorService.generateId(IdType.DummyUserId).flatMap { userId ->
            val user = User.createDummy(userId)
            tokenService.generateToken(user).map { Pair(it, user) }
        }
    }
}

typealias AuthorService = UserService
