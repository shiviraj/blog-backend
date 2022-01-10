package com.blog.domain

import java.time.LocalDateTime
import java.time.ZoneOffset

data class Content(
    val time: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
    var blocks: List<Block> = emptyList()
) {
    fun update(content: Content): Content {
        blocks = content.blocks
        return this
    }
}

data class Block(
    val id: String,
    val type: String,
    val data: Map<String, Any> = emptyMap()
)
