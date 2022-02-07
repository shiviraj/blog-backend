package com.blog.repository

import com.blog.domain.Token
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface TokenRepository : ReactiveCrudRepository<Token, String> {
    fun findByValue(value: String): Mono<Token>
    fun deleteByValue(value: String): Mono<Token>
}
