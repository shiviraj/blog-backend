package com.blog.service

import com.blog.domain.Post
import com.blog.domain.PostDetails
import com.blog.repository.PostRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class PublishedPostService(
    val postRepository: PostRepository,
    val authorService: AuthorService,
    val categoryService: CategoryService,
    val tagService: TagService,
) {
    fun getPostDetails(url: String): Mono<PostDetails> {
        return getPost(url)
            .flatMap { post -> getPostDetails(post) }
    }

    private fun getPost(url: String) = postRepository.findByUrlAndPostStatus(url)

    private fun save(post: Post) = postRepository.save(post)
        .logOnSuccess("Successfully update post in db", mapOf("postId" to post.postId))
        .logOnError("Failed to update post in db", mapOf("postId" to post.postId))

    private fun getPostDetails(post: Post): Mono<PostDetails> {
        return Mono.zip(
            authorService.getUserByUserId(post.authorId),
            categoryService.getAllCategories(post.categories),
            tagService.getAllTags(post.tags),
        ).map {
            PostDetails.from(post, it.t1, it.t2, it.t3)
        }.logOnSuccess("Successfully get post details", mapOf("url" to post.url))
            .logOnError("Failed to get post details", mapOf("url" to post.url))
    }
}

