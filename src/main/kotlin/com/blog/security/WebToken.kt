package com.blog.security

import com.blog.domain.Role
import com.blog.domain.Token
import com.blog.domain.User
import com.blog.security.crypto.Crypto
import com.blog.utils.ObjectMapperCache
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class WebToken(
    @Autowired val crypto: Crypto
) {
    fun generateToken(user: User): Token {
        val userToken = UserToken.from(user)
        val userAsString = ObjectMapperCache.objectMapper.writeValueAsString(userToken)
        return Token(crypto.encrypt(userAsString))
    }

    fun getUserAuthenticationData(token: String): UserToken {
        val decryptedString = crypto.decrypt(token)
        val user = ObjectMapperCache.objectMapper.readValue<Map<String, Any>>(decryptedString)
        return UserToken(
            userId = user["userId"] as String,
            uniqueId = user["uniqueId"] as String,
            username = user["username"] as String,
            issuedAt = (user["issuedAt"] as Int).toLong(),
            expiredAt = (user["expiredAt"] as Int).toLong(),
            role = Role.valueOf(user["role"] as String),
            token = Token(token)
        )
    }
}

