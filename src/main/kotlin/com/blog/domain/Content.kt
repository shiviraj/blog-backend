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
        val content = blocks.filter { it.type == "paragraph" }
            .map { it.data["text"] as String }
            .flatMap { it.split("<br>") }
            .subList(0, blocks.size.coerceAtMost(7))
            .joinToString("<br/>")
        return content.substring(0, content.length.coerceAtMost(500))
    }
}

data class Block(
    val id: String,
    val type: String,
    val data: Map<String, Any> = emptyMap()
)
