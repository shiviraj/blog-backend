package com.blog.repository

import com.blog.domain.Author
import com.blog.domain.Page
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface PageRepository : ReactiveCrudRepository<Page, String> {
    fun findByPageIdAndAndAuthor(pageId: String, author: Author): Mono<Page>
}
