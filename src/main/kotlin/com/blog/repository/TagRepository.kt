package com.blog.repository

import com.blog.domain.Tag
import com.blog.domain.TagId
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface TagRepository : ReactiveCrudRepository<Tag, String> {
    fun findAllByTagIdIn(tags: List<TagId>): Flux<Tag>
    fun findByName(name: String): Mono<Tag>

    @Query("{ 'name':{\$regex: ?0, \$options:'i'}}")
    fun findTagsByTagName(tagName: String, pageable: Pageable = Pageable.ofSize(10)): Flux<Tag>
}
