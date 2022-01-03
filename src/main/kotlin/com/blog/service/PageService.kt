package com.blog.service

import com.blog.controller.view.PageView
import com.blog.domain.Author
import com.blog.domain.Page
import com.blog.repository.PageRepository
import com.blog.security.UserId
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class PageService(
    val pageRepository: PageRepository,
    val idGeneratorService: IdGeneratorService
) {
    fun addNewPage(userId: UserId): Mono<Page> {
        return idGeneratorService.generateId(IdType.PageId)
            .flatMap { pageId ->
                pageRepository.save(Page(pageId = pageId, author = Author.from(userId)))
            }
            .logOnSuccess("Successfully added new page", mapOf("user" to userId.username))
            .logOnError("failed to add new page", mapOf("user" to userId.username))
    }

    fun getPage(pageId: String, userId: UserId): Mono<Page> {
        return pageRepository.findByPageIdAndAndAuthor(pageId, Author.from(userId))
    }

    fun updatePage(pageId: String, pageView: PageView, userId: UserId): Mono<Page> {
        return getPage(pageId, userId)
            .flatMap {
                pageRepository.save(it.update(pageView))
            }
            .logOnSuccess("Successfully updated page", mapOf("user" to userId.username, "pageId" to pageId))
            .logOnError("failed to update page", mapOf("user" to userId.username, "pageId" to pageId))
    }
}

