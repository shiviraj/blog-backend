package com.blog.repository

import com.blog.domain.AuthorId
import com.blog.domain.Post
import com.blog.domain.PostId
import com.blog.domain.PostStatus
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface PostRepository : ReactiveCrudRepository<Post, String> {
    fun findByPostIdAndAuthorId(pageId: String, authorId: AuthorId): Mono<Post>
    fun findByAuthorIdAndPostIdOrUrl(authorId: AuthorId, postIdOrUrl: String): Mono<Post>
    fun findAllByAuthorIdOrderByPostIdAsc(authorId: AuthorId, pageable: Pageable): Flux<Post>
    fun countAllByAuthorId(authorId: AuthorId): Mono<Long>
    fun findByUrlAndPostStatus(url: String, status: PostStatus = PostStatus.PUBLISH): Mono<Post>
    fun findByPostId(postId: PostId): Mono<Post>
}
