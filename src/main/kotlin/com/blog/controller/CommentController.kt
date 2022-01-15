package com.blog.controller

import com.blog.controller.view.CommentView
import com.blog.domain.CommentId
import com.blog.domain.PostId
import com.blog.domain.UserId
import com.blog.service.CommentService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/posts/comments")
class CommentController(
    val commentService: CommentService
) {

    @GetMapping("/{postId}")
    fun getPostComments(@PathVariable postId: PostId): Flux<CommentView> {
        return commentService.getAllApprovedComments(postId)
            .map { CommentView.from(it) }
    }

    @PostMapping("/{postId}")
    fun addComments(@PathVariable postId: PostId, @RequestBody commentRequest: CommentRequest): Mono<CommentView> {
        return commentService.addComment(postId, commentRequest)
            .map { CommentView.from(it) }
    }
}

data class CommentRequest(val userId: UserId, val message: String, val parentComment: CommentId? = null)
