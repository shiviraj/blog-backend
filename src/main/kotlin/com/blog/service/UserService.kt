package com.blog.service

import com.blog.domain.*
import com.blog.repository.UserRepository
import com.blog.security.UserId
import com.blog.security.WebToken
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class UserService(
    val userRepository: UserRepository,
    val idGeneratorService: IdGeneratorService,
    val webToken: WebToken
) {

    fun signInUserFromOauth(githubUser: GithubUser, githubUserEmail: GithubUserEmail): Mono<Pair<String, User>> {
        return userRepository.findByUsername(githubUser.username)
            .switchIfEmpty(
                Mono.just(User(username = githubUser.username))
                    .flatMap {
                        registerNewUser(it, githubUser, githubUserEmail)
                    }
            )
            .flatMap {
                val token = webToken.generateToken(it)
                it.tokens.add(Token(token))
                userRepository.save(it)
                    .map { user ->
                        Pair(token, user)
                    }
                    .logOnSuccess("Successfully login user", mapOf("username" to githubUser.username))
            }
    }

    private fun registerNewUser(user: User, githubUser: GithubUser, githubUserEmail: GithubUserEmail): Mono<User> {
        return idGeneratorService.generateId(IdType.UserId)
            .flatMap { userId ->
                user.updateUser(userId, githubUser, githubUserEmail)
                userRepository.save(user)
            }
            .logOnSuccess("Successfully registered a new user", mapOf("user" to user.username))
            .logOnError("Failed to register a new user", mapOf("user" to user.username))
    }

    fun logoutUser(userId: UserId): Mono<User> {
        return userRepository.findByUsername(userId.username)
            .flatMap { user ->
                user.tokens.removeIf {
                    it.token == userId.token
                }
                userRepository.save(user)
            }
            .logOnSuccess("Successfully logout user", mapOf("user" to userId.userId))
            .logOnError("Failed to logout user", mapOf("user" to userId.userId))
    }

    fun getAllUsers(userId: UserId): Mono<List<User>> {
        return Mono.just(userId)
            .flatMapMany {
                if (userId.role == Role.ADMIN) userRepository.findAllByRole(Role.USER)
                else userRepository.findAllByRoleIn(listOf(Role.USER, Role.ADMIN))
            }
            .collectList()
            .map {
                it.sortedBy { user -> user.role }
            }
            .logOnSuccess("Successfully fetched all user from db", mapOf("user" to userId.userId))
            .logOnError("Failed to fetch user all from db", mapOf("user" to userId.userId))
    }

    fun getUser(userId: UserId): Mono<User> {
        return userRepository.findByUsername(userId.username)
            .logOnSuccess("Successfully fetched user from db", mapOf("user" to userId.userId))
            .logOnError("Failed to fetch user from db", mapOf("user" to userId.userId))
    }
}

