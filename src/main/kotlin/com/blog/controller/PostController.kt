package com.blog.controller

import com.blog.controller.view.PostDetailsView
import com.blog.controller.view.PostSummaryView
import com.blog.domain.Author
import com.blog.domain.PostId
import com.blog.domain.User
import com.blog.service.PostService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/posts")
class PostController(
    val postService: PostService
) {

    @PostMapping
    fun addNewPost(author: Author): Mono<PostDetailsView> {
        return postService.addNewPost(author).map { PostDetailsView.from(it) }
    }

    @GetMapping("/{postId}")
    fun getPost(@PathVariable postId: PostId, author: Author): Mono<PostDetailsView> {
        return postService.getPostDetails(postId, author).map { PostDetailsView.from(it) }
    }

    @GetMapping("/{postId}/url-available/{url}")
    fun isUrlAvailable(@PathVariable postId: PostId, @PathVariable url: String, author: Author): Mono<Boolean> {
        return postService.isUrlAvailable(postId, url, author)
    }

    @PutMapping("/{postId}")
    fun updatePost(
        @PathVariable postId: String,
        @RequestBody postDetailsView: PostDetailsView,
        author: Author
    ): Mono<PostDetailsView> {
        return postService.updatePost(postId, postDetailsView, author).map { PostDetailsView.from(it) }
    }

    @GetMapping("/my-posts/page/{page}/limit/{limit}")
    fun getMyPost(@PathVariable limit: Int, @PathVariable page: Int, author: Author): Flux<PostSummaryView> {
        return postService.getMyAllPosts(page, limit, author).map {
            PostSummaryView.from(it.t1, it.t2, it.t3, it.t4, it.t5)
        }
    }

    @GetMapping("/my-posts/count")
    fun getMyPostCount(author: Author): Mono<Long> {
        return postService.getMyPostsCount(author)
    }

    @GetMapping("/{url}/published")
    fun getPost(@PathVariable url: String): Mono<PostDetailsView> {
        return postService.getPostDetails(url).map { PostDetailsView.from(it) }
    }

    @PutMapping("/{postId}/like-or-dislike")
    fun addLikeOrDislikeOnComment(
        @PathVariable postId: PostId,
        @RequestBody likeOrDislikeRequest: LikeOrDislikeRequest,
        user: User
    ): Mono<PostDetailsView> {
        return postService.likeOrDislikeOnComment(postId, likeOrDislikeRequest, user).map { PostDetailsView.from(it) }
    }

    @GetMapping("/page/{page}")
    fun getPosts(@PathVariable page: Int): Flux<PostSummaryView> {
        return postService.getAllPosts(page).map {
            PostSummaryView.from(it.t1, it.t2, it.t3, it.t4, it.t5)
        }
    }

    @GetMapping("/count")
    fun getSidebar(): Mono<Long> {
        return postService.getPostsCount()
    }

}

