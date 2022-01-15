package com.blog.service

import com.blog.controller.view.PostDetailsView
import com.blog.domain.*
import com.blog.repository.PostRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class PostService(
    val idGeneratorService: IdGeneratorService,
    val postRepository: PostRepository,
    val categoryService: CategoryService,
    val tagService: TagService,
    val commentService: CommentService
) {
    fun addNewPost(author: Author): Mono<PostDetails> {
        return idGeneratorService.generateId(IdType.PostId)
            .flatMap { postId -> save(Post(postId = postId, authorId = author.userId)) }
            .flatMap { getPostDetails(it, author) }
            .logOnSuccess("Successfully added new post", mapOf("author" to author.username))
            .logOnError("failed to add new post", mapOf("author" to author.username))
    }

    fun getPostDetails(postId: PostId, author: Author): Mono<PostDetails> {
        return getPost(postId, author)
            .flatMap { post -> getPostDetails(post, author) }
    }

    fun updatePost(postId: String, postDetailsView: PostDetailsView, author: Author): Mono<Post> {
        return getPost(postId, author)
            .flatMap {
                save(it.update(postDetailsView))
            }
            .logOnSuccess("Successfully updated post", mapOf("author" to author.username, "postId" to postId))
            .logOnError("failed to update post", mapOf("author" to author.username, "postId" to postId))
    }


    fun getMyAllPosts(page: Int, limit: Int, author: Author): Flux<PostSummary> {
        return postRepository.findAllByAuthorIdOrderByPostIdAsc(author.userId, PageRequest.of(page, limit))
            .flatMap { post -> getPostSummary(post, author) }
    }

    fun getMyPostsCount(author: Author): Mono<Long> {
        return postRepository.countAllByAuthorId(author.userId)
            .logOnSuccess("Successfully get post count of author", mapOf("authorId" to author.userId))
            .logOnError("Failed to get post count of author", mapOf("authorId" to author.userId))
    }

    private fun getPost(postId: String, author: Author) = postRepository.findByPostIdAndAuthorId(postId, author.userId)
    private fun save(post: Post) = postRepository.save(post)
        .logOnSuccess("Successfully update post in db", mapOf("postId" to post.postId))
        .logOnError("Failed to update post in db", mapOf("postId" to post.postId))

    private fun getPostDetails(post: Post, author: Author): Mono<PostDetails> {
        return Mono.zip(
            categoryService.getAllCategories(post.categories),
            tagService.getAllTags(post.tags),
        ).map {
            PostDetails.from(post, author, it.t1, it.t2)
        }.logOnSuccess("Successfully get post details", mapOf("postId" to post.postId))
            .logOnError("Failed to get post details", mapOf("postId" to post.postId))

    }

    private fun getPostSummary(post: Post, author: Author): Mono<PostSummary> {
        return Mono.zip(
            categoryService.getAllCategories(post.categories),
            tagService.getAllTags(post.tags),
            commentService.getCommentsCount(post.postId)
        ).map {
            PostSummary.from(post, author, it.t1, it.t2, it.t3)
        }.logOnSuccess("Successfully get post summary", mapOf("postId" to post.postId))
            .logOnError("Failed to get post summary", mapOf("postId" to post.postId))
    }
}

