package com.blog.domain

data class PostSummary(
    val postId: PostId,
    val url: String,
    val title: String,
    val postDate: PostDate,
    val author: Author,
    val postStatus: PostStatus,
    val visibility: Visibility,
    val categories: List<Category>,
    val tags: List<Tag>,
    val likes: Int,
    val disLikes: Int,
    val comments: Int
) {
    companion object {
        fun from(
            post: Post,
            author: Author,
            categories: List<Category>,
            tags: List<Tag>,
            comments: Int
        ):
            PostSummary {
            return PostSummary(
                postId = post.postId,
                url = post.url,
                title = post.title,
                postDate = post.postDate,
                author = author,
                postStatus = post.postStatus,
                visibility = post.visibility,
                categories = categories,
                tags = tags,
                likes = post.likes.size,
                disLikes = post.disLikes.size,
                comments = comments
            )
        }
    }
}
