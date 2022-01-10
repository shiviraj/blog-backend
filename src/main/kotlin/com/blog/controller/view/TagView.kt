package com.blog.controller.view

import com.blog.domain.Tag
import com.blog.domain.TagId

data class TagView(
    val tagId: TagId,
    val name: String,
    val url: String,
) {
    companion object {
        fun from(tag: Tag): TagView {
            return TagView(
                tagId = tag.tagId,
                name = tag.name,
                url = tag.url
            )
        }
    }
}
