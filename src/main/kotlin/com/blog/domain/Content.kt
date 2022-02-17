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

    fun getTruncateContent(): String {
        val blocks = blocks.filter { it.type == "paragraph" }
        val content = blocks.subList(0, blocks.size.coerceAtMost(7))
            .joinToString("\n") { it.data["text"] as String }
        return content.substring(0, content.length.coerceAtMost(500))
    }
}

data class Block(
    val id: String,
    val type: String,
    val data: Map<String, Any> = emptyMap()
)
