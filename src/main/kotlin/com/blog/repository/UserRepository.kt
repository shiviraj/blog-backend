package com.blog.repository

import com.blog.domain.Role
import com.blog.domain.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface UserRepository : ReactiveCrudRepository<User, String> {
    fun findByTokensToken(token: String): Mono<User>
    fun findAllByRole(role: Role): Flux<User>
    fun findAllByRoleIn(roles: List<Role>): Flux<User>
    fun findByUsername(username: String): Mono<User>
}
