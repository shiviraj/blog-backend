package com.blog.controller.view

import com.blog.domain.*
import java.time.LocalDateTime

data class PostDetailsView(
    val postId: PostId,
    val url: String,
    val title: String,
    val content: Content,
    val createdAt: LocalDateTime,
    val lastUpdatedAt: LocalDateTime,
    val publishedOn: LocalDateTime?,
    val featuredImage: String?,
    val author: AuthorId,
    val postStatus: PostStatus,
    val visibility: Visibility,
    val commentsAllowed: Boolean,
    val categories: Set<CategoryId>,
    val tags: Set<TagId>,
    val likes: Set<UserId>,
    val dislikes: Set<UserId>,
) {
    companion object {
        fun from(post: Post, isPublished: Boolean = false): PostDetailsView {
            return PostDetailsView(
                postId = post.postId,
                url = post.getUrl(),
                content = if (isPublished) post.publishedContent else post.content,
                title = post.getTitle(),
                author = post.authorId,
                tags = post.tags,
                categories = post.categories,
                postStatus = post.getStatus(),
                visibility = post.getVisibility(),
                likes = post.likes,
                dislikes = post.dislikes,
                commentsAllowed = post.isCommentsAllowed(),
                createdAt = post.createdAt,
                lastUpdatedAt = post.getLastUpdateOn(),
                publishedOn = post.getPublishedOn(),
                featuredImage = post.getFeaturedImage()
            )
        }
    }
}
