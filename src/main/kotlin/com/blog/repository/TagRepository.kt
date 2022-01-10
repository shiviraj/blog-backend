package com.blog.repository

import com.blog.domain.Tag
import com.blog.domain.TagId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface TagRepository : ReactiveCrudRepository<Tag, String> {
    fun findAllByTagIdIn(tags: List<TagId>): Flux<Tag>
    fun findByName(name: String): Mono<Tag>
}
