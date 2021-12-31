package com.blog.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("blog.crypto")
data class CryptoConfig(var secretKey: String) {
    init {
        secretKey = secretKey.padStart(32, '0')
        if (secretKey.length > 32)
            secretKey = secretKey.substring(0, 32)
    }
}
