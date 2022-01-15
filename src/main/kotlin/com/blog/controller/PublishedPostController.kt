package com.blog.controller

import com.blog.controller.view.PublishedPostDetailsView
import com.blog.service.PublishedPostService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/posts/published")
class PublishedPostController(
    val publishedPostService: PublishedPostService,
) {

    @GetMapping("/{url}")
    fun getPost(@PathVariable url: String): Mono<PublishedPostDetailsView> {
        return publishedPostService.getPostDetails(url)
            .map { PublishedPostDetailsView.from(it) }
    }
}
