package com.blog.repository

import com.blog.domain.Message
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository : ReactiveCrudRepository<Message, String>
