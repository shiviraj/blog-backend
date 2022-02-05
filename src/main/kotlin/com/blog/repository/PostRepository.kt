package com.blog.repository

import com.blog.domain.*
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface PostRepository : ReactiveCrudRepository<Post, String> {
    fun findByPostIdAndAuthorId(pageId: String, authorId: AuthorId): Mono<Post>
    fun findByAuthorIdAndUrl(authorId: AuthorId, url: String): Mono<Post>
    fun findAllByAuthorIdOrderByPostIdAsc(authorId: AuthorId, pageable: Pageable): Flux<Post>
    fun countAllByAuthorId(authorId: AuthorId): Mono<Long>
    fun findByUrlAndPostStatus(url: String, status: PostStatus = PostStatus.PUBLISH): Mono<Post>
    fun findByPostId(postId: PostId): Mono<Post>
    fun findAllByPostStatusOrderByLastUpdateOnDesc(status: PostStatus, pageable: Pageable): Flux<Post>
    fun countAllByPostStatus(status: PostStatus = PostStatus.PUBLISH): Mono<Long>
    fun findAllByCategoriesAndPostStatusOrderByLastUpdateOnDesc(
        categoryId: CategoryId,
        status: PostStatus,
        pageable: Pageable
    ): Flux<Post>

    fun countAllByCategoriesAndPostStatus(categoryId: CategoryId, status: PostStatus = PostStatus.PUBLISH): Mono<Long>
}
