package com.blog.controller

import com.blog.controller.view.CategoryView
import com.blog.controller.view.PostSummaryView
import com.blog.domain.Author
import com.blog.domain.CategoryId
import com.blog.service.CategoryService
import com.blog.service.PostService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/categories")
class CategoryController(
    val categoryService: CategoryService,
    val postService: PostService
) {

    @GetMapping
    fun getAllCategory(): Flux<CategoryView> {
        return categoryService.getAllCategories()
            .map { CategoryView.from(it) }
    }

    @GetMapping("/{categoryUrl}/posts/page/{page}")
    fun getAllPosts(@PathVariable categoryUrl: String, @PathVariable page: Int): Flux<PostSummaryView> {
        return postService.getAllPostsByCategories(categoryUrl, page)
            .map { PostSummaryView.from(it.t1, it.t2, it.t3, it.t4, it.t5) }
    }

    @GetMapping("/{categoryUrl}/posts/count")
    fun countAllPosts(@PathVariable categoryUrl: String): Mono<Long> {
        return postService.getAllPostsByCategory(categoryUrl)
    }

    @PostMapping
    fun addNewCategory(@RequestBody categoryRequest: CategoryRequest, author: Author): Mono<CategoryView> {
        return categoryService.addNewCategory(categoryRequest, author)
            .map { CategoryView.from(it) }
    }

    @PostMapping("/categories")
    fun getCategory(@RequestBody categories: List<CategoryId>): Flux<CategoryView> {
        return categoryService.getAllCategories(categories).map { CategoryView.from(it) }
    }
}

data class CategoryRequest(val name: String, val parentCategory: CategoryId? = null)

