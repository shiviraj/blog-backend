package com.blog.repository

import com.blog.domain.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserRepository : ReactiveCrudRepository<User, String> {
    fun findByUserId(userId: String): Mono<User>
    fun findByUsername(username: String): Mono<User>
}
