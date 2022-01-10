package com.blog.controller

import com.blog.controller.view.PageView
import com.blog.domain.User
import com.blog.service.PageService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/pages")
class PageController(
    val pageService: PageService
) {

    @PostMapping
    fun addNewPage(user: User): Mono<PageView> {
        return pageService.addNewPage(user)
            .map { PageView.from(it) }
    }

    @GetMapping("/{pageId}")
    fun getPage(@PathVariable pageId: String, user: User): Mono<PageView> {
        return pageService.getPage(pageId, user)
            .map { PageView.from(it) }
    }

    @PutMapping("/{pageId}")
    fun updatePage(@PathVariable pageId: String, @RequestBody pageView: PageView, user: User): Mono<PageView> {
        return pageService.updatePage(pageId, pageView, user)
            .map { PageView.from(it) }
    }
}

