package com.blog.security

import com.blog.security.crypto.Crypto
import com.blog.security.domain.UserToken
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class WebToken(
    @Autowired val crypto: Crypto,
) {
    fun generateToken(email: String, sub: String): String {
        val issuedAt = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        val expiredAt = LocalDateTime.now().plusDays(7).toEpochSecond(ZoneOffset.UTC)
        return generateToken(UserToken(email, sub, issuedAt, expiredAt))
    }

    fun generateToken(userToken: UserToken): String {
        return crypto.encrypt(userToken.toString())
    }

    fun extractUser(token: String): UserToken? {
        return UserToken.from(crypto.decrypt(token))
    }
}

