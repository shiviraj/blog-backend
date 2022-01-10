package com.blog.controller.view

import com.blog.domain.Category
import com.blog.domain.CategoryId

data class CategoryView(
    val categoryId: CategoryId,
    val name: String,
    val url: String,
    val parentCategory: CategoryId?,
) {
    companion object {
        fun from(category: Category): CategoryView {
            return CategoryView(
                categoryId = category.categoryId,
                name = category.name,
                url = category.url,
                parentCategory = category.parentCategory
            )
        }
    }
}
