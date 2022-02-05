package com.blog.controller.view

import com.blog.domain.*
import java.time.LocalDateTime

data class PostSummaryView(
    val postId: PostId,
    val url: String,
    val title: String,
    val createdAt: LocalDateTime,
    val lastUpdatedAt: LocalDateTime,
    val publishedOn: LocalDateTime?,
    val author: AuthorView,
    val featuredImage: String?,
    val content: String,
    val postStatus: PostStatus,
    val visibility: Visibility,
    val categories: Set<CategoryView>,
    val tags: Set<TagView>,
    val likes: Set<UserId>,
    val dislikes: Set<UserId>,
    val comments: Long
) {
    companion object {
        fun from(
            post: Post,
            tags: List<Tag>,
            categories: List<Category>,
            author: Author,
            comments: Long
        ): PostSummaryView {
            return PostSummaryView(
                postId = post.postId,
                url = post.getUrl(),
                title = post.getTitle(),
                author = AuthorView.from(author),
                tags = tags.map { TagView.from(it) }.toSet(),
                categories = categories.map { CategoryView.from(it) }.toSet(),
                postStatus = post.getStatus(),
                visibility = post.getVisibility(),
                likes = post.likes,
                dislikes = post.dislikes,
                comments = comments,
                createdAt = post.createdAt,
                lastUpdatedAt = post.getLastUpdateOn(),
                publishedOn = post.getPublishedOn(),
                featuredImage = post.getFeaturedImage(),
                content = post.publishedContent.getTruncateContent()
            )
        }
    }
}
