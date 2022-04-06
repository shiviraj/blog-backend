package com.blog.service

import com.blog.domain.Message
import com.blog.webClient.WebClientWrapper
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class Notifier(val webClientWrapper: WebClientWrapper, private val secretService: SecretService) {

    fun notify(message: Message): Mono<String> {
        return Mono.zip(secretService.getNotifierBot(), secretService.getNotifierChatId())
            .flatMap {
                val bot = it.t1
                val chatId = it.t2
                webClientWrapper.post(
                    baseUrl = "https://api.telegram.org",
                    path = "/bot${bot.value}/sendMessage",
                    body = mapOf(
                        "chat_id" to chatId.value,
                        "parse_mode" to "HTML",
                        "text" to createMessage(message)
                    ),
                    returnType = String::class.java,
                )
            }
            .logOnSuccess("Successfully send notification")
            .logOnError("Failed to  send notification")
    }

    private fun createMessage(message: Message): String {
        return """
            <b>Received message from Contact us: Shiviraj.com</b>
            <p>Name: ${message.name}</p>
            <p>Email: ${message.email}</p>
            <p>Subject: ${message.subject}</p>
            <p>Message: ${message.message}</p>
            Please get in touch your customer soon!!
            """
    }
}
