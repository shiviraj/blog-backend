package com.blog.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

const val TAG_COLLECTION = "tags"

@TypeAlias("Tag")
@Document(TAG_COLLECTION)
data class Tag(
    @Id
    var id: ObjectId? = null,
    val tagId: TagId,
    @Indexed(unique = true)
    val name: String,
    val url: String = name.replace(" ", "-"),
    val authorId: AuthorId,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

typealias TagId = String
