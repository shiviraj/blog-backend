package com.blog.controller

import com.blog.controller.view.PostDetailsView
import com.blog.controller.view.PostSummaryView
import com.blog.domain.*
import com.blog.security.authorization.Authorization
import com.blog.service.PostService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/posts")
class PostController(
    val postService: PostService
) {

    @Authorization(Role.USER)
    @PostMapping
    fun addNewPost(author: Author): Mono<PostDetailsView> {
        return postService.addNewPost(author).map { PostDetailsView.from(it) }
    }

    @Authorization(Role.USER)
    @GetMapping("/{postId}")
    fun getPost(@PathVariable postId: PostId, author: Author): Mono<PostDetailsView> {
        return postService.getPostDetails(postId, author).map { PostDetailsView.from(it) }
    }

    @Authorization(Role.USER)
    @GetMapping("/{postId}/url-available/{url}")
    fun isUrlAvailable(@PathVariable postId: PostId, @PathVariable url: String, author: Author): Mono<Boolean> {
        return postService.isUrlAvailable(postId, url, author)
    }

    @Authorization(Role.USER)
    @PutMapping("/{postId}")
    fun updatePost(
        @PathVariable postId: String,
        @RequestBody postDetailsView: PostDetailsView,
        author: Author
    ): Mono<PostDetailsView> {
        return postService.updatePost(postId, postDetailsView, author).map { PostDetailsView.from(it) }
    }

    @Authorization(Role.USER)
    @GetMapping("/my-posts/page/{page}/limit/{limit}")
    fun getMyPost(@PathVariable limit: Int, @PathVariable page: Int, author: Author): Flux<PostSummaryView> {
        return postService.getMyAllPosts(page, limit, author).map {
            PostSummaryView.from(it)
        }
    }

    @Authorization(Role.USER)
    @GetMapping("/my-posts/count")
    fun getMyPostCount(author: Author): Mono<Long> {
        return postService.getMyPostsCount(author)
    }

    @GetMapping("/{url}/published")
    fun getPost(@PathVariable url: String): Mono<PostDetailsView> {
        return postService.getPostDetails(url).map { PostDetailsView.from(it) }
    }

    @Authorization(Role.USER)
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
            PostSummaryView.from(it)
        }
    }

    @GetMapping("/count")
    fun getPublishedPostsCount(): Mono<Long> {
        return postService.getPostsCount()
    }

    @GetMapping("/author/{authorId}")
    fun getPostsOf(@PathVariable authorId: AuthorId): Flux<PostSummaryView> {
        return postService.getPostsOf(authorId).map { PostSummaryView.from(it) }
    }
}

