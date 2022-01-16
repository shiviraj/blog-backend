package com.blog.controller.view

import com.blog.domain.*
import java.time.LocalDateTime

data class CommentView(
    val commentId: CommentId,
    val user: UserView,
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
        fun from(commentDetails: CommentDetails): CommentView {
            return CommentView(
                commentId = commentDetails.commentId,
                user = UserView.from(commentDetails.user),
                postId = commentDetails.postId,
                message = commentDetails.message,
                status = commentDetails.status,
                commentedOn = commentDetails.commentedOn,
                parentComment = commentDetails.parentComment,
                likes = commentDetails.likes,
                dislikes = commentDetails.dislikes,
                pinned = commentDetails.pinned
            )
        }
    }
}
