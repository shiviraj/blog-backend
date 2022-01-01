package com.blog.controller

import com.blog.annotation.IntegrationTest
import com.blog.controller.view.AuthenticationResponse
import com.blog.domain.Secret
import com.blog.repository.SecretRepository
import com.blog.security.crypto.Crypto
import com.blog.service.SecretKeys
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.WebTestClient

@IntegrationTest
class OauthControllerIntegrationTest(
    @Autowired private val webTestClient: WebTestClient,
    @Autowired private val secretRepository: SecretRepository,
    @Autowired private val crypto: Crypto
) {
    @BeforeEach
    fun setUp() {
        secretRepository.deleteAll().block()
        clearAllMocks()

    }

    @AfterEach
    fun tearDown() {
        secretRepository.deleteAll().block()
        clearAllMocks()
    }

    @Test
    fun `should get clientId`() {
        val secret = Secret(SecretKeys.GITHUB_CLIENT_ID, crypto.encrypt("clientId"))
        secretRepository.save(secret).block()

        webTestClient.get()
            .uri("/oauth/client-id")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(String::class.java)
            .returnResult()
            .responseBody!! shouldBe """{"key":"GITHUB_CLIENT_ID","value":"clientId"}"""
    }

    @Test
    fun `should sign in user from github oauth`() {
        val clientId = Secret(SecretKeys.GITHUB_CLIENT_ID, crypto.encrypt("clientId"))
        val clientSecret = Secret(SecretKeys.GITHUB_CLIENT_SECRET, crypto.encrypt("clientSecret"))
        secretRepository.saveAll(listOf(clientId, clientSecret)).blockLast()

        webTestClient.post()
            .uri("/oauth/sign-in/code")
            .bodyValue(CodeRequest("code"))
            .exchange()
            .expectStatus().isOk
            .expectBody(AuthenticationResponse::class.java)
            .returnResult()
            .responseBody!! shouldBe AuthenticationResponse("token")
    }
}
