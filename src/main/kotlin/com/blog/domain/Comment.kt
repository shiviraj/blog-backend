package com.blog.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

const val COMMENT_COLLECTION = "comments"

@TypeAlias("Comment")
@Document(COMMENT_COLLECTION)
data class Comment(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val commentId: CommentId,
    val userId: UserId,
    val postId: PostId,
    val message: String,
    val status: CommentStatus = CommentStatus.UNAPPROVED,
    val commentedOn: LocalDateTime = LocalDateTime.now(),
    val parentComment: CommentId? = null,
    val likes: List<UserId> = emptyList(),
    val dislikes: List<UserId> = emptyList(),
    val pinned: Boolean = false
)

enum class CommentStatus {
    UNAPPROVED,
    APPROVED,
    TRASH
}

data class CommentDetails(
    val commentId: CommentId,
    val user: User,
    val postId: PostId,
    val message: String,
    val status: CommentStatus,
    val commentedOn: LocalDateTime,
    val parentComment: CommentId?,
    val likes: Int,
    val dislikes: Int,
    val pinned: Boolean
) {
    companion object {
        fun from(comment: Comment, user: User): CommentDetails {
            return CommentDetails(
                commentId = comment.commentId,
                user = user,
                postId = comment.postId,
                message = comment.message,
                status = comment.status,
                commentedOn = comment.commentedOn,
                parentComment = comment.parentComment,
                likes = comment.likes.size,
                dislikes = comment.dislikes.size,
                pinned = comment.pinned
            )
        }
    }
}

typealias CommentId = String
