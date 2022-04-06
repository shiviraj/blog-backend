package com.blog.service

import com.blog.controller.MessageRequest
import com.blog.domain.Message
import com.blog.exceptions.error_code.BlogError
import com.blog.exceptions.exceptions.BaseException
import com.blog.repository.MessageRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class MessageService(
    val idGeneratorService: IdGeneratorService,
    val messageRepository: MessageRepository,
    val notifier: Notifier
) {
    fun sendMessage(messageRequest: MessageRequest): Mono<Message> {
        return idGeneratorService.generateId(IdType.MessageId)
            .flatMap {
                messageRepository.save(
                    Message(
                        messageId = it,
                        name = messageRequest.name,
                        email = messageRequest.email,
                        subject = messageRequest.subject,
                        message = messageRequest.message,
                    )
                )
            }.flatMap { message ->
                notifier.notify(message)
                    .flatMap {
                        message.alertSent = true
                        messageRepository.save(message)
                    }
                    .onErrorResume {
                        messageRepository.delete(message)
                            .map {
                                throw BaseException(BlogError.BLOG604)
                            }
                    }
            }
    }
}

