package com.blog.service

import com.blog.domain.CommentDetails
import com.blog.domain.PostId
import com.blog.repository.CommentRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CommentService(
    val idGeneratorService: IdGeneratorService,
    val commentRepository: CommentRepository,
    val userService: UserService
) {
//    fun addNewPost(author: Author): Mono<Post> {
//        return idGeneratorService.generateId(IdType.PostId)
//            .flatMap { postId ->
//                save(Post(postId = postId, authorId = author.userId))
//            }
//            .flatMap {
//                getPostDetails(it, author)
//            }
//            .logOnSuccess("Successfully added new post", mapOf("author" to author.username))
//            .logOnError("failed to add new post", mapOf("author" to author.username))
//    }

    fun getAllComments(postId: PostId): Mono<List<CommentDetails>> {
        return commentRepository.findAllByPostIdOrderByCommentedOnAsc(postId)
            .flatMap {
                userService.getUserByUserId(it.userId)
                    .map { user -> CommentDetails.from(it, user) }
            }.collectList()
            .switchIfEmpty(Mono.just(emptyList()))
    }

    fun getCommentsCount(postId: PostId): Mono<Int> {
        return commentRepository.countAllByPostId(postId)
            .switchIfEmpty(Mono.just(0))
    }
}

