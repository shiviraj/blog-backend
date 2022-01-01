package com.blog.security

import com.blog.domain.Role
import com.blog.domain.User
import com.blog.security.crypto.Crypto
import com.blog.utils.ObjectMapperCache
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class WebToken(
    @Autowired val crypto: Crypto,
) {
    fun generateToken(user: User): String {
        val userId = UserId.from(user)
        val userAsString = ObjectMapperCache.objectMapper.writeValueAsString(userId)
        return crypto.encrypt(userAsString)
    }

    fun getUserAuthenticationData(token: String): UserId {
        val decryptedString = crypto.decrypt(token)
        val user = ObjectMapperCache.objectMapper.readValue<Map<String, Any>>(decryptedString)
        return UserId(
            userId = user["userId"] as String,
            uniqueId = user["uniqueId"] as String,
            username = user["username"] as String,
            issuedAt = (user["issuedAt"] as Int).toLong(),
            expiredAt = (user["expiredAt"] as Int).toLong(),
            role = Role.valueOf(user["role"] as String),
            token = token
        )
    }
}

