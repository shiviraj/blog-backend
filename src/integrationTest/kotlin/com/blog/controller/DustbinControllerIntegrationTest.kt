package com.blog.controller

import com.blog.annotation.IntegrationTest
import com.blog.builder.DustbinBuilder
import com.blog.builder.LocationBuilder
import com.blog.domain.Dustbin
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime

@IntegrationTest
class DustbinControllerIntegrationTest(
    @Autowired private var webTestClient: WebTestClient,
    @Autowired val dustbinRepository: DustbinRepository
) {

    @BeforeEach
    fun setUp() {
        dustbinRepository.deleteAll().block()
    }

    @AfterEach
    fun tearDown() {
        dustbinRepository.deleteAll().block()
    }

    @Test
    fun `should register new dustbin to the system`() {
        val dustbin = DustbinBuilder(dustbinId = 1, createdAt = LocalDateTime.of(2021, 1, 1, 1, 0)).build()
        val dustbin1 = DustbinBuilder(dustbinId = 2, createdAt = LocalDateTime.of(2021, 1, 1, 1, 1)).build()
        dustbinRepository.saveAll(listOf(dustbin, dustbin1)).blockLast()

        val location = LocationBuilder().build()
        val responseBody = webTestClient.post()
            .uri("/dustbins")
            .bodyValue(location)
            .exchange()
            .expectStatus().isOk
            .expectBody(Dustbin::class.java)
            .returnResult()
            .responseBody!!
        assertSoftly {
            responseBody.dustbinId shouldBe 3L
        }
    }
}
