package com.blog.domain

import com.blog.controller.view.PostDetailsView
import com.blog.domain.PostStatus.DRAFT
import com.blog.domain.PostStatus.PUBLISH
import com.blog.domain.Visibility.PUBLIC
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

const val POST_COLLECTION = "posts"

@TypeAlias("Post")
@Document(POST_COLLECTION)
data class Post(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val postId: PostId,
    @Indexed(unique = true)
    var url: String = postId,
    var title: String = "Add new post",
    val content: Content = Content(),
    val publishedContent: Content = content,
    val postDate: PostDate = PostDate(),
    val authorId: AuthorId,
    val visibility: Visibility = PUBLIC,
    var postStatus: PostStatus = DRAFT,
    var commentsAllowed: Boolean = true,
    val categories: MutableList<CategoryId> = mutableListOf(),
    val tags: MutableList<TagId> = mutableListOf(),
    val likes: MutableList<UserId> = mutableListOf(),
    val disLikes: MutableList<UserId> = mutableListOf()
) {
    fun update(postDetailsView: PostDetailsView): Post {
        url = postDetailsView.url
        title = postDetailsView.title
        commentsAllowed = postDetailsView.commentsAllowed
        content.update(postDetailsView.content)
        tags.addAllUniq(postDetailsView.tags)
        categories.addAllUniq(postDetailsView.categories)
        if (postDetailsView.postStatus == PUBLISH) {
            postStatus = PUBLISH
            postDate.publish(postStatus)
            publishedContent.update(content)
        }
        return this
    }
}

private fun <E> MutableList<E>.addAllUniq(list: List<E>): MutableList<E> {
    list.map { item ->
        if (!this.contains(item)) this.add(item)
    }
    return this
}

data class PostDate(
    var publishedOn: LocalDateTime? = null,
    var lastUpdateOn: LocalDateTime = LocalDateTime.now(),
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    fun publish(postStatus: PostStatus) {
        if (postStatus == DRAFT) {
            publishedOn = LocalDateTime.now()
        }
        lastUpdateOn = LocalDateTime.now()
    }
}

enum class PostStatus {
    DRAFT,
    PUBLISH,
    TRASH
}

enum class Visibility {
    PUBLIC,
    PRIVATE
}

typealias PostId = String
