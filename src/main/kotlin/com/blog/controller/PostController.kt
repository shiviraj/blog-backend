package com.blog.controller

import com.blog.controller.view.PostDetailsView
import com.blog.controller.view.PostSummaryView
import com.blog.domain.Author
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
    fun getPost(@PathVariable postId: String, author: Author): Mono<PostDetailsView> {
        return postService.getPostDetails(postId, author).map { PostDetailsView.from(it) }
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
        return postService.getMyAllPosts(page, limit, author).map { PostSummaryView.from(it) }
    }

    @GetMapping("/my-posts/count")
    fun getMyPostCount(author: Author): Mono<Long> {
        return postService.getMyPostsCount(author)
    }

    @GetMapping("/{url}/published")
    fun getPost(@PathVariable url: String): Mono<PostDetailsView> {
        return postService.getPostDetails(url).map { PostDetailsView.from(it) }
    }
}

