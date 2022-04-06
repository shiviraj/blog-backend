package com.blog.controller

import com.blog.service.MessageService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/messages")
class MessageController(
    val messageService: MessageService
) {

    @PostMapping
    fun sendMessage(@RequestBody messageRequest: MessageRequest): Mono<Map<String, Boolean>> {
        return messageService.sendMessage(messageRequest).map { mapOf("success" to true) }
    }
}

data class MessageRequest(val name: String, val email: String, val subject: String = "", val message: String)

