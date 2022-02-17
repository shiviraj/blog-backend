package com.blog.controller

import com.blog.controller.view.CommentView
import com.blog.domain.*
import com.blog.security.authorization.Authorization
import com.blog.service.CommentService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/comments")
class CommentController(
    val commentService: CommentService
) {

    @GetMapping("/{postId}")
    fun getPostComments(@PathVariable postId: PostId): Flux<CommentView> {
        return commentService.getAllApprovedComments(postId).map { CommentView.from(it) }
    }

    @Authorization(Role.USER)
    @PostMapping("/{postId}")
    fun addComments(
        @PathVariable postId: PostId,
        @RequestBody commentRequest: CommentRequest,
        user: User
    ): Mono<CommentView> {
        return commentService.addComment(postId, commentRequest, user).map { CommentView.from(it) }
    }

    @Authorization(Role.USER)
    @PutMapping("/{commentId}")
    fun addLikeOrDislikeOnComment(
        @PathVariable commentId: CommentId,
        @RequestBody likeOrDislikeRequest: LikeOrDislikeRequest,
        user: User
    ): Mono<CommentView> {
        return commentService.likeOrDislikeOnComment(commentId, likeOrDislikeRequest, user)
            .map { CommentView.from(it) }
    }
}

data class LikeOrDislikeRequest(val action: Action)
data class CommentRequest(val message: String, val parentComment: CommentId? = null)
