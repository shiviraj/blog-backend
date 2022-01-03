package com.blog.controller

import com.blog.controller.view.PageView
import com.blog.security.UserId
import com.blog.service.PageService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/pages")
class PageController(
    val pageService: PageService
) {

    @PostMapping
    fun addNewPage(userId: UserId): Mono<PageView> {
        return pageService.addNewPage(userId)
            .map { PageView.from(it) }
    }

    @GetMapping("/{pageId}")
    fun getPage(@PathVariable pageId: String, userId: UserId): Mono<PageView> {
        return pageService.getPage(pageId, userId)
            .map { PageView.from(it) }
    }

    @PutMapping("/{pageId}")
    fun updatePage(@PathVariable pageId: String, @RequestBody pageView: PageView, userId: UserId): Mono<PageView> {
        return pageService.updatePage(pageId, pageView, userId)
            .map { PageView.from(it) }
    }
}

