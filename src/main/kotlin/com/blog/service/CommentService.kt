package com.blog.service

import com.blog.controller.CommentRequest
import com.blog.controller.LikeOrDislikeRequest
import com.blog.domain.*
import com.blog.repository.CommentRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class CommentService(
    val idGeneratorService: IdGeneratorService,
    val commentRepository: CommentRepository,
    val userService: UserService
) {
    fun likeOrDislikeOnComment(
        commentId: CommentId,
        likeOrDislikeRequest: LikeOrDislikeRequest,
        user: User
    ): Mono<CommentDetails> {
        return commentRepository.findByCommentIdAndStatus(commentId)
            .flatMap { comment ->
                save(comment.updateLikeOrDislike(likeOrDislikeRequest, user.userId))
            }.flatMap {
                userService.getUserByUserId(it.userId)
                    .map { user -> CommentDetails.from(it, user) }
            }
    }

    fun getAllApprovedComments(postId: PostId): Flux<CommentDetails> {
        return commentRepository.findAllByPostIdAndStatusOrderByCommentedOnAsc(postId)
            .flatMap {
                userService.getUserByUserId(it.userId).map { user -> CommentDetails.from(it, user) }
            }
    }

    fun addComment(postId: PostId, commentRequest: CommentRequest): Mono<CommentDetails> {
        return idGeneratorService.generateId(IdType.CommentId)
            .flatMap { commentId ->
                save(
                    Comment(
                        commentId = commentId,
                        userId = commentRequest.userId,
                        postId = postId,
                        message = commentRequest.message,
                        status = CommentStatus.UNAPPROVED,
                        parentComment = commentRequest.parentComment,
                    )
                )
            }.flatMap {
                userService.getUserByUserId(it.userId)
                    .map { user -> CommentDetails.from(it, user) }
            }
    }

    fun countAllComments(postId: PostId) = commentRepository.countAllByPostId(postId)

    private fun save(comment: Comment) = commentRepository.save(comment)
        .logOnSuccess("Successfully save comment in db", mapOf("commentId" to comment.commentId))
        .logOnError("Failed to save comment in db", mapOf("commentId" to comment.commentId))


}

