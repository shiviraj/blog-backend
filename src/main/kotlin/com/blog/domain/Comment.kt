package com.blog.domain

import com.blog.controller.LikeOrDislikeRequest
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
    val likes: MutableSet<UserId> = mutableSetOf(),
    val dislikes: MutableSet<UserId> = mutableSetOf(),
    val pinned: Boolean = false
) {
    fun updateLikeOrDislike(likeOrDislikeRequest: LikeOrDislikeRequest, userId: UserId): Comment {
        when (likeOrDislikeRequest.action) {
            Action.ADD_LIKE -> {
                likes.add(userId)
                dislikes.removeIf { it == userId }
            }
            Action.ADD_DISLIKE -> {
                dislikes.add(userId)
                likes.removeIf { it == userId }
            }
            Action.REMOVE_LIKE -> likes.removeIf { it == userId }
            Action.REMOVE_DISLIKE -> dislikes.removeIf { it == userId }
        }
        return this
    }
}

enum class CommentStatus {
    UNAPPROVED,
    APPROVED,
    TRASH
}

enum class Action {
    ADD_LIKE,
    ADD_DISLIKE,
    REMOVE_LIKE,
    REMOVE_DISLIKE
}

data class CommentDetails(
    val commentId: CommentId,
    val user: User,
    val postId: PostId,
    val message: String,
    val status: CommentStatus,
    val commentedOn: LocalDateTime,
    val parentComment: CommentId?,
    val likes: Set<UserId>,
    val dislikes: Set<UserId>,
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
                likes = comment.likes,
                dislikes = comment.dislikes,
                pinned = comment.pinned
            )
        }
    }
}

typealias CommentId = String
