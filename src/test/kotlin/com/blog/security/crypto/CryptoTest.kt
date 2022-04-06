package com.blog.security.crypto

import com.blog.config.CryptoConfig
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class CryptoTest {
    private val crypto = Crypto(CryptoConfig("adfsd"))

    @Test
    fun `should encrypt the string and decrypt the string`() {
        val encrypt = crypto.encrypt("")
        println(encrypt)
        val decrypt = crypto.decrypt(encrypt)
        decrypt shouldBe "{\"name\":\"hello world\"}"
    }
}
