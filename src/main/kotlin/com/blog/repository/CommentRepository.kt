package com.blog.repository

import com.blog.domain.Comment
import com.blog.domain.CommentId
import com.blog.domain.CommentStatus
import com.blog.domain.PostId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface CommentRepository : ReactiveCrudRepository<Comment, String> {
    fun findAllByPostIdAndStatusOrderByCommentedOnAsc(
        postId: PostId,
        status: CommentStatus = CommentStatus.APPROVED
    ): Flux<Comment>

    fun countAllByPostId(postId: PostId): Mono<Long>

    fun findByCommentIdAndStatus(commentId: CommentId, status: CommentStatus = CommentStatus.APPROVED): Mono<Comment>
}
