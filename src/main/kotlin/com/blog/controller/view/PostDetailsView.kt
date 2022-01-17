package com.blog.controller.view

import com.blog.domain.*

data class PostDetailsView(
    val postId: PostId,
    val url: String,
    val title: String,
    val content: Content,
    val postDate: PostDate,
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
                url = post.url,
                content = if (isPublished) post.publishedContent else post.content,
                title = post.title,
                postDate = post.postDate,
                author = post.authorId,
                tags = post.tags,
                categories = post.categories,
                postStatus = post.postStatus,
                visibility = post.visibility,
                likes = post.likes,
                dislikes = post.dislikes,
                commentsAllowed = post.commentsAllowed
            )
        }
    }
}
