package com.blog.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

const val CATEGORY_COLLECTION = "categories"

@TypeAlias("Category")
@Document(CATEGORY_COLLECTION)
data class Category(
    @Id
    var id: ObjectId? = null,
    val categoryId: CategoryId,
    @Indexed(unique = true)
    val name: String,
    val url: String = name.trim().replace(" ", "-"),
    val parentCategory: CategoryId? = null,
    val authorId: AuthorId,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

typealias CategoryId = String
