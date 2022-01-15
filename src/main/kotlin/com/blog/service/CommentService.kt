package com.blog.service

import com.blog.controller.CommentRequest
import com.blog.domain.Comment
import com.blog.domain.CommentDetails
import com.blog.domain.CommentStatus
import com.blog.domain.PostId
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
    fun getCommentsCount(postId: PostId): Mono<Int> {
        return commentRepository.countAllByPostId(postId)
            .switchIfEmpty(Mono.just(0))
    }

    fun getAllApprovedComments(postId: PostId): Flux<CommentDetails> {
        return commentRepository.findAllByPostIdAndStatusOrderByCommentedOnAsc(postId)
            .flatMap {
                userService.getUserByUserId(it.userId)
                    .map { user -> CommentDetails.from(it, user) }
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

    private fun save(comment: Comment) = commentRepository.save(comment)
        .logOnSuccess("Successfully save comment in db", mapOf("commentId" to comment.commentId))
        .logOnError("Failed to save comment in db", mapOf("commentId" to comment.commentId))
}

