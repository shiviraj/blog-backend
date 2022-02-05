package com.blog.controller.view

import com.blog.domain.*

data class PostSummaryView(
    val postId: PostId,
    val url: String,
    val title: String,
    val postDate: PostDate,
    val author: AuthorId,
    val postStatus: PostStatus,
    val visibility: Visibility,
    val categories: Set<CategoryId>,
    val tags: Set<TagId>,
    val likes: Set<UserId>,
    val dislikes: Set<UserId>,
) {
    companion object {
        fun from(post: Post): PostSummaryView {
            return PostSummaryView(
                postId = post.postId,
                url = post.getUrl(),
                title = post.getTitle(),
                postDate = post.postDate,
                author = post.authorId,
                tags = post.tags,
                categories = post.categories,
                postStatus = post.getStatus(),
                visibility = post.getVisibility(),
                likes = post.likes,
                dislikes = post.dislikes,
            )
        }
    }
}
