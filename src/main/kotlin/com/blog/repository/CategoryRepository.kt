package com.blog.repository

import com.blog.domain.Category
import com.blog.domain.CategoryId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface CategoryRepository : ReactiveCrudRepository<Category, String> {
    fun findAllByCategoryIdIn(categoryIds: List<CategoryId>): Flux<Category>
    fun findByName(name: String): Mono<Category>
    fun findByCategoryId(categoryId: CategoryId): Mono<Category>
    fun existsByCategoryId(categoryId: CategoryId?): Mono<Boolean>
}
