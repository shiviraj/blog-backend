package com.blog.service

import com.blog.controller.TagRequest
import com.blog.domain.Author
import com.blog.domain.Tag
import com.blog.domain.TagId
import com.blog.exceptions.error_code.BlogError
import com.blog.exceptions.exceptions.BadDataException
import com.blog.repository.TagRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class TagService(
    val idGeneratorService: IdGeneratorService,
    val tagRepository: TagRepository
) {

    fun getAllTags(tags: List<TagId>): Flux<Tag> {
        return tagRepository.findAllByTagIdIn(tags)
    }

    fun addNewTag(tagRequest: TagRequest, author: Author): Mono<Tag> {
        return tagRepository.findByName(tagRequest.name)
            .switchIfEmpty(registerNewTag(tagRequest, author))
    }

    private fun registerNewTag(tagRequest: TagRequest, author: Author): Mono<Tag> {
        return idGeneratorService.generateId(IdType.TagId)
            .flatMap { tagId ->
                if (tagRequest.name.trim().isEmpty()) {
                    Mono.error(BadDataException(BlogError.BLOG603))
                } else {
                    save(Tag(tagId = tagId, name = tagRequest.name.trim(), authorId = author.userId))
                }
            }
            .logOnSuccess("Successfully added new post", mapOf("author" to author.username))
            .logOnError("failed to add new post", mapOf("author" to author.username))


    }

    private fun save(tag: Tag) = tagRepository.save(tag)
        .logOnSuccess("Successfully updated tag in db", mapOf("tagId" to tag.tagId))
        .logOnError("failed to update tag in db", mapOf("tagId" to tag.tagId))

    fun getAllTagsByTagName(tagName: String): Flux<Tag> {
        return tagRepository.findTagsByTagName(tagName)
    }
}

