package com.blog.controller.view

import com.blog.domain.*

data class PublishedPostDetailsView(
    val postId: PostId,
    val url: String,
    val title: String,
    val content: Content,
    val postDate: PostDate,
    val author: UserView,
    val postStatus: PostStatus,
    val visibility: Visibility,
    val commentsAllowed: Boolean,
    val categories: List<CategoryView>,
    val tags: List<TagView>,
    val likes: Int,
    val disLikes: Int
) {
    companion object {
        fun from(postDetails: PostDetails): PublishedPostDetailsView {
            return PublishedPostDetailsView(
                postId = postDetails.postId,
                url = postDetails.url,
                content = postDetails.publishedContent,
                title = postDetails.title,
                postDate = postDetails.postDate,
                author = UserView.from(postDetails.author),
                tags = postDetails.tags.map { TagView.from(it) },
                categories = postDetails.categories.map { CategoryView.from(it) },
                postStatus = postDetails.postStatus,
                visibility = postDetails.visibility,
                likes = postDetails.likes,
                disLikes = postDetails.disLikes,
                commentsAllowed = postDetails.commentsAllowed,
            )
        }
    }
}
