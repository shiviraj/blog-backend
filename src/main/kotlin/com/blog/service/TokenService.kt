package com.blog.service

import com.blog.domain.Token
import com.blog.domain.User
import com.blog.domain.UserType
import com.blog.repository.TokenRepository
import com.blog.security.crypto.Crypto
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import org.springframework.security.core.userdetails.User as User1

@Service
class TokenService(
    val tokenRepository: TokenRepository,
    val crypto: Crypto
) : UserDetailsService {
    fun generateToken(user: User): Mono<Token> {
        return generateToken(Token.from(user))
    }

    fun generateToken(token: Token): Mono<Token> {
        return save(token.updateValue(crypto.encrypt(token.toString())))
            .logOnSuccess("Successfully generate token")
            .logOnError("failed to generate token")
    }

    fun logoutUser(token: String): Mono<Token> {
        return tokenRepository.deleteByValue(token)
    }

    override fun loadUserByUsername(token: String?): UserDetails? {
        if (token.isNullOrEmpty()) return null
        return User1("username", "password", emptyList())
    }

    fun extractToken(token: String) = tokenRepository.findByValue(token).map {
        it.updateValue(crypto.decrypt(it.getValue()))
    }
        .switchIfEmpty(
            Mono.just(
                Token(userId = "userId", userType = UserType.DUMMY, value = "")
            )
        )
        .logOnSuccess("Successfully get token")
        .logOnError("Failed to get token")

    private fun save(token: Token) = tokenRepository.save(token)
}

