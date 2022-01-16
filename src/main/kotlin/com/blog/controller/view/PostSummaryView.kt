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
    val categories: List<CategoryId>,
    val tags: List<TagId>,
    val likes: List<UserId>,
    val disLikes: List<UserId>,
) {
    companion object {
        fun from(post: Post): PostSummaryView {
            return PostSummaryView(
                postId = post.postId,
                url = post.url,
                title = post.title,
                postDate = post.postDate,
                author = post.authorId,
                tags = post.tags,
                categories = post.categories,
                postStatus = post.postStatus,
                visibility = post.visibility,
                likes = post.likes,
                disLikes = post.disLikes,
            )
        }
    }
}
