package com.blog.domain

data class PostDetails(
    val postId: PostId,
    val url: String,
    val title: String,
    val content: Content,
    val publishedContent: Content,
    val postDate: PostDate,
    val author: Author,
    val postStatus: PostStatus,
    val visibility: Visibility,
    val commentsAllowed: Boolean,
    val categories: List<Category>,
    val tags: List<Tag>,
    val likes: Int,
    val disLikes: Int,
    val comments: List<CommentDetails>
) {
    companion object {
        fun from(
            post: Post,
            author: Author,
            categories: List<Category>,
            tags: List<Tag>,
            comments: List<CommentDetails>
        ):
            PostDetails {
            return PostDetails(
                postId = post.postId,
                url = post.url,
                title = post.title,
                content = post.content,
                publishedContent = post.publishedContent,
                postDate = post.postDate,
                author = author,
                postStatus = post.postStatus,
                visibility = post.visibility,
                categories = categories,
                tags = tags,
                likes = post.likes.size,
                disLikes = post.disLikes.size,
                comments = comments,
                commentsAllowed = post.commentsAllowed
            )
        }
    }
}
