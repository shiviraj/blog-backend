package com.blog.service

import com.blog.domain.Role
import com.blog.domain.Token
import com.blog.domain.User
import com.blog.repository.UserRepository
import com.blog.security.WebToken
import com.blog.security.domain.UserToken
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class UserService(
    val userRepository: UserRepository,
    val idGeneratorService: IdGeneratorService,
    val webToken: WebToken
) {

    fun getUserByToken(token: String): Mono<User> {
        return userRepository.findByTokensToken(token)
    }

    fun getUserByUserToken(userToken: UserToken): Mono<User> {
        return userRepository.findByUsername(userToken.username)
    }

    fun signInUserFromOauth(githubUser: GithubUser, githubUserEmail: GithubUserEmail): Mono<String> {
        return userRepository.findByUsername(githubUser.username)
            .flatMap {
                if (it.userId.isEmpty() && it.role === Role.OWNER) {
                    registerNewUser(it, githubUser, githubUserEmail)
                } else Mono.just(it)
            }
            .switchIfEmpty(
                Mono.just(User(username = githubUser.username))
                    .flatMap {
                        registerNewUser(it, githubUser, githubUserEmail)
                    }
            )
            .flatMap {
                val token = webToken.generateToken(it.username, it.userId)
                it.tokens.add(Token(token))
                userRepository.save(it)
                    .map {
                        token
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
            .logOnSuccess("Successfully registered a new user", mapOf("user" to user))
            .logOnError("Failed to register a new user")
    }

    fun registerUser(user: User): Mono<User> {
        return userRepository.findByUsername(user.username)
            .switchIfEmpty {
                userRepository.save(user)
            }
    }

    fun logoutUser(userToken: UserToken): Mono<User> {
        val token = webToken.generateToken(userToken)
        return userRepository.findByUsername(userToken.username)
            .flatMap { user ->
                user.tokens.removeIf {
                    it.token == token
                }
                userRepository.save(user)
            }
            .logOnSuccess("Successfully logout user", mapOf("userToken" to userToken))
            .logOnError("Failed to logout user", mapOf("userToken" to userToken))
    }

    fun getAllUsers(userToken: UserToken): Mono<List<User>> {
        return userRepository.findByUsername(userToken.username)
            .flatMapMany {
                if (it.role == Role.ADMIN) userRepository.findAllByRole(Role.USER)
                else userRepository.findAllByRoleIn(listOf(Role.USER, Role.ADMIN))
            }
            .collectList()
            .map {
                it.sortedBy { user -> user.role }
            }
    }

}

