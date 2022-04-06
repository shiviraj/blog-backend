package com.blog.service

import com.blog.domain.Secret
import com.blog.exceptions.error_code.BlogError
import com.blog.exceptions.exceptions.DataNotFound
import com.blog.repository.SecretRepository
import com.blog.security.crypto.Crypto
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class SecretService(private val secretRepository: SecretRepository, private val crypto: Crypto) {
    fun getClientId(): Mono<Secret> {
        return getSecret(SecretKeys.GITHUB_CLIENT_ID)
    }

    fun getClientSecret(): Mono<Secret> {
        return getSecret(SecretKeys.GITHUB_CLIENT_SECRET)
    }

    private fun getSecret(secretKey: SecretKeys): Mono<Secret> {
        return secretRepository.findByKey(secretKey)
            .map {
                it.decryptValue(crypto.decrypt(it.value))
            }
            .switchIfEmpty(Mono.error(DataNotFound(BlogError.BLOG604)))
    }

    fun getNotifierBot(): Mono<Secret> {
        return getSecret(SecretKeys.BOT)
    }

    fun getNotifierChatId(): Mono<Secret> {
        return getSecret(SecretKeys.CHAT_ID)
    }
}

enum class SecretKeys {
    GITHUB_CLIENT_ID,
    GITHUB_CLIENT_SECRET,
    BOT,
    CHAT_ID
}
