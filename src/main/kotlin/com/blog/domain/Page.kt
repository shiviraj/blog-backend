package com.blog.domain

import com.blog.controller.view.PageView
import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

const val PAGE_COLLECTION = "pages"

@TypeAlias("Page")
@Document(PAGE_COLLECTION)
data class Page(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val pageId: String,
    var url: String = "page",
    var content: Content = Content(),
    var publishedContent: Content = Content(),
    var title: String = "page",
    var published: Boolean = false,
    val author: Author
) {
    @Indexed
    @CreatedDate
    private lateinit var createdAt: LocalDateTime
    fun getCreatedAt() = createdAt
    fun setCreatedDate(createdDate: LocalDateTime) {
        if (!this::createdAt.isInitialized) {
            this.createdAt = createdDate
        }
    }

    @LastModifiedDate
    private lateinit var lastModifiedAt: LocalDateTime
    fun getLastModifiedAt() = lastModifiedAt
    fun update(pageView: PageView): Page {
        url = pageView.url
        title = pageView.title
        content = pageView.content
        if (pageView.published) {
            published = pageView.published
            publishedContent = content
        }
        return this
    }
}
