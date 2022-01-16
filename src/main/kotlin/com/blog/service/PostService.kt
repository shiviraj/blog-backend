package com.blog.service

import com.blog.controller.view.PostDetailsView
import com.blog.domain.Author
import com.blog.domain.Post
import com.blog.repository.PostRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class PostService(
    val idGeneratorService: IdGeneratorService,
    val postRepository: PostRepository
) {
    fun addNewPost(author: Author): Mono<Post> {
        return idGeneratorService.generateId(IdType.PostId)
            .flatMap { postId -> save(Post(postId = postId, authorId = author.userId)) }
            .logOnSuccess("Successfully added new post", mapOf("author" to author.username))
            .logOnError("failed to add new post", mapOf("author" to author.username))
    }

    fun getPostDetails(postIdOrUrl: String, author: Author): Mono<Post> {
        return getPostByPostIdOrUrl(postIdOrUrl, author)
    }

    fun updatePost(postId: String, postDetailsView: PostDetailsView, author: Author): Mono<Post> {
        return getPost(postId, author)
            .flatMap {
                save(it.update(postDetailsView))
            }
            .logOnSuccess("Successfully updated post", mapOf("author" to author.username, "postId" to postId))
            .logOnError("failed to update post", mapOf("author" to author.username, "postId" to postId))
    }


    fun getMyAllPosts(page: Int, limit: Int, author: Author): Flux<Post> {
        return postRepository.findAllByAuthorIdOrderByPostIdAsc(author.userId, PageRequest.of(page, limit))
    }

    fun getMyPostsCount(author: Author): Mono<Long> {
        return postRepository.countAllByAuthorId(author.userId)
            .logOnSuccess("Successfully get post count of author", mapOf("authorId" to author.userId))
            .logOnError("Failed to get post count of author", mapOf("authorId" to author.userId))
    }

    fun getPostDetails(url: String): Mono<Post> {
        return postRepository.findByUrlAndPostStatus(url)
    }

    private fun getPost(postId: String, author: Author) = postRepository.findByPostIdAndAuthorId(postId, author.userId)
    private fun getPostByPostIdOrUrl(postIdOrUrl: String, author: Author): Mono<Post> {
        return postRepository.findByAuthorIdAndPostIdOrUrl(author.userId, postIdOrUrl)
    }

    private fun save(post: Post) = postRepository.save(post)
        .logOnSuccess("Successfully update post in db", mapOf("postId" to post.postId))
        .logOnError("Failed to update post in db", mapOf("postId" to post.postId))
}

