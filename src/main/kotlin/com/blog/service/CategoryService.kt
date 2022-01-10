package com.blog.service

import com.blog.controller.CategoryRequest
import com.blog.domain.Author
import com.blog.domain.Category
import com.blog.domain.CategoryId
import com.blog.repository.CategoryRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class CategoryService(
    val idGeneratorService: IdGeneratorService,
    val categoryRepository: CategoryRepository
) {
    fun addNewCategory(categoryRequest: CategoryRequest, author: Author): Mono<Category> {
        return categoryRepository.findByName(categoryRequest.name)
            .switchIfEmpty(registerNewCategory(categoryRequest, author))
    }

    fun getAllCategories(): Flux<Category> {
        return categoryRepository.findAll()
    }

    fun getAllCategories(categories: List<CategoryId>): Mono<List<Category>> {
        return categoryRepository.findAllByCategoryIdIn(categories).collectList()
    }

    private fun registerNewCategory(categoryRequest: CategoryRequest, author: Author): Mono<Category> {
        return idGeneratorService.generateId(IdType.CategoryId)
            .flatMap { categoryId ->
                categoryRepository.existsByCategoryId(categoryRequest.parentCategory)
                    .flatMap { exist ->
                        save(
                            Category(
                                categoryId = categoryId,
                                name = categoryRequest.name.trim(),
                                url = categoryRequest.name.trim().replace(" ", "-").lowercase(Locale.getDefault()),
                                parentCategory = if (exist) categoryRequest.parentCategory else null,
                                authorId = author.userId
                            )
                        )
                    }
            }.logOnSuccess("Successfully added new category", mapOf("category" to categoryRequest.name))
            .logOnError("failed to add new category", mapOf("category" to categoryRequest.name))
    }

    private fun save(category: Category) = categoryRepository.save(category)
        .logOnSuccess("Successfully updated category in db", mapOf("categoryId" to category.categoryId))
        .logOnError("failed to update category in db", mapOf("categoryId" to category.categoryId))
}

