package com.blog.controller.view

import com.blog.domain.Author
import com.blog.domain.Content
import com.blog.domain.Page

data class PageView(
    val pageId: String,
    val url: String,
    val content: Content,
    val title: String,
    val published: Boolean,
    val author: Author
) {
    companion object {
        fun from(page: Page): PageView {
            return PageView(
                pageId = page.pageId,
                url = page.url,
                content = page.content,
                title = page.title,
                published = page.published,
                author = page.author
            )
        }
    }
}
