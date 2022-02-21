package com.blog.controller.view

import com.blog.domain.PostId
import com.blog.domain.PostStatus
import com.blog.domain.UserId
import com.blog.domain.Visibility
import com.blog.service.PostSummary
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
            postSummary: PostSummary
        ): PostSummaryView {
            return PostSummaryView(
                postId = postSummary.post.postId,
                url = postSummary.post.getUrl(),
                title = postSummary.post.getTitle(),
                author = AuthorView.from(postSummary.author),
                tags = postSummary.tags.map { TagView.from(it) }.toSet(),
                categories = postSummary.categories.map { CategoryView.from(it) }.toSet(),
                postStatus = postSummary.post.getStatus(),
                visibility = postSummary.post.getVisibility(),
                likes = postSummary.post.likes,
                dislikes = postSummary.post.dislikes,
                comments = postSummary.comments,
                createdAt = postSummary.post.createdAt,
                lastUpdatedAt = postSummary.post.getLastUpdateOn(),
                publishedOn = postSummary.post.getPublishedOn(),
                featuredImage = postSummary.post.getFeaturedImage(),
                content = postSummary.post.publishedContent.getTruncateContent()
            )
        }
    }
}
