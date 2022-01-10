package com.blog.service

import com.blog.controller.view.PageView
import com.blog.domain.Author
import com.blog.domain.Page
import com.blog.repository.PageRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class PageService(
    val pageRepository: PageRepository,
    val idGeneratorService: IdGeneratorService
) {
    fun addNewPage(author: Author): Mono<Page> {
        return idGeneratorService.generateId(IdType.PageId)
            .flatMap { pageId ->
                pageRepository.save(Page(pageId = pageId, author = author))
            }
            .logOnSuccess("Successfully added new page", mapOf("user" to author.username))
            .logOnError("failed to add new page", mapOf("user" to author.username))
    }

    fun getPage(pageId: String, author: Author): Mono<Page> {
        return pageRepository.findByPageIdAndAndAuthor(pageId, author)
    }

    fun updatePage(pageId: String, pageView: PageView, author: Author): Mono<Page> {
        return getPage(pageId, author)
            .flatMap {
                pageRepository.save(it.update(pageView))
            }
            .logOnSuccess("Successfully updated page", mapOf("user" to author.username, "pageId" to pageId))
            .logOnError("failed to update page", mapOf("user" to author.username, "pageId" to pageId))
    }
}

