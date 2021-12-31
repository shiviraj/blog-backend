package com.blog.security.domain

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.time.LocalDateTime
import java.time.ZoneOffset

data class UserToken(
    val username: String,
    val sub: String,
    val issuedAt: Long,
    val expiredAt: Long,
) {
    override fun toString(): String {
        return ObjectMapperCache.objectMapper.writeValueAsString(this)
    }

    fun validate(): Boolean {
        val now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        return now in (issuedAt) until expiredAt
    }

    companion object {
        fun from(user: String): UserToken {
            return try {
                ObjectMapperCache.objectMapper.readValue(user, UserToken::class.java)
            } catch (e: Exception) {
                UserToken(username = "", sub = "", issuedAt = 0, expiredAt = 0)
            }
        }
    }
}


object ObjectMapperCache {
    val objectMapper = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)!!
}




