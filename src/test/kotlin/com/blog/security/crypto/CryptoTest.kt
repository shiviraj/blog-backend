package com.blog.security.crypto

import com.blog.config.CryptoConfig
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class CryptoTest {
    private val crypto = Crypto(CryptoConfig("@@yVNx4AKopKHU@fpcNQ7e6n*E6bmqGv"))

    @Test
    fun `should encrypt the string and decrypt the string`() {
        val encrypt = crypto.encrypt("5809892510a8917ccc96577ebfe511b7cc375016")
        println(encrypt)
        val decrypt = crypto.decrypt(encrypt)
        decrypt shouldBe "{\"name\":\"hello world\"}"
    }
}
