package com.blog.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

const val CONTACT_MESSAGE = "message"

@TypeAlias("ContactMessage")
@Document(CONTACT_MESSAGE)
data class Message(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val messageId: String,
    val name: String,
    val email: String,
    val subject: String,
    val message: String,
    var alertSent: Boolean = false,
    var replied: Boolean = false
)
