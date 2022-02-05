package com.blog.service

import com.blog.controller.LikeOrDislikeRequest
import com.blog.controller.view.PostDetailsView
import com.blog.domain.*
import com.blog.repository.PostRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.function.Tuple5

@Service
class PostService(
    val idGeneratorService: IdGeneratorService,
    val postRepository: PostRepository,
    val tagService: TagService,
    val commentService: CommentService,
    val categoryService: CategoryService,
    val authorService: AuthorService
) {
    fun addNewPost(author: Author): Mono<Post> {
        return idGeneratorService.generateId(IdType.PostId)
            .flatMap { postId -> save(Post(postId = postId, authorId = author.userId)) }
            .logOnSuccess("Successfully added new post", mapOf("author" to author.username))
            .logOnError("failed to add new post", mapOf("author" to author.username))
    }

    fun getPostDetails(postId: PostId, author: Author): Mono<Post> {
        return getPostByPostId(postId, author)
    }

    fun updatePost(postId: String, postDetailsView: PostDetailsView, author: Author): Mono<Post> {
        return getPost(postId, author)
            .flatMap {
                save(it.update(postDetailsView))
            }
            .logOnSuccess("Successfully updated post", mapOf("author" to author.username, "postId" to postId))
            .logOnError("failed to update post", mapOf("author" to author.username, "postId" to postId))
    }


    fun getMyAllPosts(
        page: Int,
        limit: Int,
        author: Author
    ): Flux<Tuple5<Post, List<Tag>, List<Category>, Author, Long>> {
        return postRepository.findAllByAuthorIdOrderByPostIdAsc(author.userId, PageRequest.of(page, limit))
            .flatMap { getPostDetails(it) }
    }


    fun getMyPostsCount(author: Author): Mono<Long> {
        return postRepository.countAllByAuthorId(author.userId)
            .logOnSuccess("Successfully get post count of author", mapOf("authorId" to author.userId))
            .logOnError("Failed to get post count of author", mapOf("authorId" to author.userId))
    }

    fun getPostDetails(url: String): Mono<Post> {
        return postRepository.findByUrlAndPostStatus(url)
    }

    fun likeOrDislikeOnComment(postId: PostId, likeOrDislikeRequest: LikeOrDislikeRequest, user: User): Mono<Post> {
        return getPost(postId)
            .flatMap {
                save(it.updateLikeOrDislike(likeOrDislikeRequest, user.userId))
            }
    }

    fun isUrlAvailable(postId: PostId, url: String, author: Author): Mono<Boolean> {
        return getPostByUrl(url, author).map { it.postId == postId }
            .switchIfEmpty(Mono.just(true))
    }

    private fun getPost(postId: PostId, author: Author) = postRepository.findByPostIdAndAuthorId(postId, author.userId)

    private fun getPost(postId: PostId) = postRepository.findByPostId(postId)

    private fun getPostByUrl(postUrl: String, author: Author): Mono<Post> {
        return postRepository.findByAuthorIdAndUrl(author.userId, postUrl)
    }

    private fun getPostByPostId(postId: PostId, author: Author): Mono<Post> {
        return postRepository.findByPostIdAndAuthorId(postId, author.userId)
    }

    private fun save(post: Post) = postRepository.save(post)
        .logOnSuccess("Successfully update post in db", mapOf("postId" to post.postId))
        .logOnError("Failed to update post in db", mapOf("postId" to post.postId))

    fun getAllPosts(page: Int): Flux<Tuple5<Post, List<Tag>, List<Category>, Author, Long>> {
        return postRepository.findAllByPostStatusOrderByLastUpdateOnDesc(
            PostStatus.PUBLISH,
            PageRequest.of(page - 1, 10)
        ).flatMapSequential { getPostDetails(it) }
    }

    private fun getPostDetails(
        post: Post
    ): Mono<Tuple5<Post, List<Tag>, List<Category>, Author, Long>> {
        return Mono.zip(
            Mono.just(post),
            tagService.getAllTags(post.tags.toList()).collectList(),
            categoryService.getAllCategories(post.categories.toList()).collectList(),
            authorService.getUserByUserId(post.authorId),
            commentService.countAllComments(post.postId)
        )
    }

    fun getPostsCount() = postRepository.countAllByPostStatus()
    fun getAllPostsByCategory(categoryUrl: String): Mono<Long> {
        return categoryService.findByUrl(categoryUrl).flatMap {
            postRepository.countAllByCategoriesAndPostStatus(it.categoryId)
        }
    }

    fun getAllPostsByCategories(
        categoryUrl: String,
        page: Int
    ): Flux<Tuple5<Post, List<Tag>, List<Category>, Author, Long>> {
        return categoryService.findByUrl(categoryUrl).flatMapMany {
            postRepository.findAllByCategoriesAndPostStatusOrderByLastUpdateOnDesc(
                it.categoryId,
                PostStatus.PUBLISH,
                PageRequest.of(page - 1, 10)
            )
        }.flatMap { post -> getPostDetails(post) }
    }
}

