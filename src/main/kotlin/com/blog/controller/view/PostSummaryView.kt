package com.blog.controller.view

import com.blog.domain.*

data class PostSummaryView(
    val postId: PostId,
    val url: String,
    val title: String,
    val postDate: PostDate = PostDate(),
    val author: AuthorView,
    val postStatus: PostStatus,
    val visibility: Visibility = Visibility.PUBLIC,
    val categories: List<CategoryView>,
    val tags: List<TagView>,
    val likes: Int,
    val disLikes: Int,
    val comments: Int
) {
    companion object {
        fun from(postSummary: PostSummary): PostSummaryView {
            return PostSummaryView(
                postId = postSummary.postId,
                url = postSummary.url,
                title = postSummary.title,
                postDate = postSummary.postDate,
                author = AuthorView.from(postSummary.author),
                tags = postSummary.tags.map { TagView.from(it) },
                categories = postSummary.categories.map { CategoryView.from(it) },
                postStatus = postSummary.postStatus,
                visibility = postSummary.visibility,
                likes = postSummary.likes,
                disLikes = postSummary.disLikes,
                comments = postSummary.comments
            )
        }
    }
}
