package com.blog.domain

import com.blog.service.SecretKeys
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

const val SECRETS_COLLECTION = "secrets"

@Document(SECRETS_COLLECTION)
data class Secret(
    @Indexed(unique = true)
    val key: SecretKeys,
    var value: String,
    var encrypted: Boolean = true
) {

    fun decryptValue(decryptedValue: String): Secret {
        if (encrypted) {
            value = decryptedValue
            encrypted = false
        }
        return this
    }
}

