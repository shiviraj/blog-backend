package com.blog.domain

import com.blog.security.UserId
import java.time.LocalDateTime
import java.time.ZoneOffset

data class Content(
    val time: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
    val blocks: List<Block> = emptyList()
)

data class Block(
    val id: String,
    val type: String,
    val data: Map<String, Any> = emptyMap()
)

data class Author(val userId: String, val displayName: String) {
    companion object {
        fun from(userId: UserId): Author {
            return Author(userId = userId.userId, displayName = userId.username)
        }
    }
}

