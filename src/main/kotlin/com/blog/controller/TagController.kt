package com.blog.controller

import com.blog.controller.view.TagView
import com.blog.domain.Author
import com.blog.domain.Role
import com.blog.domain.TagId
import com.blog.security.authorization.Authorization
import com.blog.service.TagService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/tags")
class TagController(
    val tagService: TagService
) {
    @Authorization(Role.USER)
    @PostMapping
    fun addNewTag(@RequestBody tagRequest: TagRequest, author: Author): Mono<TagView> {
        return tagService.addNewTag(tagRequest, author).map { TagView.from(it) }
    }

    @PostMapping("/tags")
    fun getAllTag(@RequestBody tags: List<TagId>): Flux<TagView> {
        return tagService.getAllTags(tags).map { TagView.from(it) }
    }

    @GetMapping("/{tagName}")
    fun getTagsByTagName(@PathVariable tagName: String): Flux<TagView> {
        return tagService.getAllTagsByTagName(tagName).map { TagView.from(it) }
    }
}

data class TagRequest(val name: String)

