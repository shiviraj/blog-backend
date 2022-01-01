package com.blog.gateway

import com.blog.config.GithubConfig
import com.blog.domain.*
import com.blog.exceptions.error_code.BlogError.*
import com.blog.exceptions.exceptions.DataNotFound
import com.blog.service.SecretKeys
import com.blog.service.SecretService
import com.blog.testUtils.assertErrorWith
import com.blog.testUtils.assertNextWith
import com.blog.webClient.WebClientWrapper
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.util.LinkedMultiValueMap
import reactor.core.publisher.Mono

class GithubGatewayTest {
    private val githubConfig = GithubConfig(
        baseUrl = "baseUrl",
        accessTokenPath = "accessTokenPath",
        userBaseUrl = "userBaseUrl",
        userProfilePath = "userProfilePath",
        userEmailPath = "userEmailPath"
    )
    private val webClientWrapper = mockk<WebClientWrapper>()
    private val secretService = mockk<SecretService>()
    private val githubGateway = GithubGateway(webClientWrapper, githubConfig, secretService)

    @Test
    fun `should give accessToken`() {
        val linkedMultiValueMap = LinkedMultiValueMap<String, String>()
        linkedMultiValueMap.add("code", "code")
        linkedMultiValueMap.add("client_id", "clientId")
        linkedMultiValueMap.add("client_secret", "clientSecret")

        every { secretService.getClientSecret() } returns Mono.just(
            Secret(SecretKeys.GITHUB_CLIENT_SECRET, "clientSecret")
        )
        every { secretService.getClientId() } returns Mono.just(
            Secret(SecretKeys.GITHUB_CLIENT_ID, "clientId")
        )
        val accessTokenResponse = AccessTokenResponse("accessToken")
        every {
            webClientWrapper.post(any(), any(), any(), any<Class<*>>(), any(), any(), any())
        } returns Mono.just(accessTokenResponse)

        val signIn = githubGateway.getAccessTokens("code")

        assertNextWith(signIn) {
            it shouldBe accessTokenResponse
            verify {
                webClientWrapper.post(
                    "baseUrl",
                    "accessTokenPath",
                    "",
                    AccessTokenResponse::class.java,
                    linkedMultiValueMap,
                    emptyMap(),
                    mapOf("accept" to "application/json")
                )
            }
        }
    }

    @Test
    fun `should user profile from github`() {
        val accessTokenResponse = AccessTokenResponse(access_token = "accessToken")
        val githubUser = GithubUser(
            username = "username",
            id = 0,
            profile = "profile",
            source = LoginSource.GITHUB
        )

        every { secretService.getClientSecret() } returns Mono.just(
            Secret(SecretKeys.GITHUB_CLIENT_SECRET, "clientSecret")
        )
        every { secretService.getClientId() } returns Mono.just(
            Secret(SecretKeys.GITHUB_CLIENT_ID, "clientId")
        )
        every {
            webClientWrapper.get(any(), any(), any<Class<*>>(), any(), any(), any())
        } returns Mono.just(githubUser)

        val signIn = githubGateway.getUserProfile(accessTokenResponse)

        assertNextWith(signIn) {
            it shouldBe githubUser
            verify {
                webClientWrapper.get(
                    "userBaseUrl",
                    "userProfilePath",
                    GithubUser::class.java,
                    headers = mapOf(
                        "accept" to "application/json",
                        HttpHeaders.AUTHORIZATION to "token accessToken"
                    )
                )
            }
        }
    }

    @Test
    fun `should give user email from github`() {
        val accessTokenResponse = AccessTokenResponse(access_token = "accessToken")
        val githubUserEmail = GithubUserEmail(email = "example@email.com", primary = true, verified = true)

        every { secretService.getClientSecret() } returns Mono.just(
            Secret(SecretKeys.GITHUB_CLIENT_SECRET, "clientSecret")
        )
        every { secretService.getClientId() } returns Mono.just(
            Secret(SecretKeys.GITHUB_CLIENT_ID, "clientId")
        )
        every {
            webClientWrapper.get(any(), any(), any<Class<*>>(), any(), any(), any())
        } returns Mono.just(githubUserEmail)

        val signIn = githubGateway.getUserEmail(accessTokenResponse)

        assertNextWith(signIn) {
            it shouldBe githubUserEmail
            verify {
                webClientWrapper.get(
                    "userBaseUrl",
                    "userEmailPath",
                    GithubUserEmail::class.java,
                    headers = mapOf(
                        "accept" to "application/json",
                        HttpHeaders.AUTHORIZATION to "token accessToken"
                    )
                )
            }
        }
    }

    @Test
    fun `should give error if accessToken call fails`() {
        val linkedMultiValueMap = LinkedMultiValueMap<String, String>()
        linkedMultiValueMap.add("code", "code")
        linkedMultiValueMap.add("client_id", "clientId")
        linkedMultiValueMap.add("client_secret", "clientSecret")

        every { secretService.getClientSecret() } returns Mono.just(
            Secret(SecretKeys.GITHUB_CLIENT_SECRET, "clientSecret")
        )
        every { secretService.getClientId() } returns Mono.just(
            Secret(SecretKeys.GITHUB_CLIENT_ID, "clientId")
        )
        every {
            webClientWrapper.post(any(), any(), any(), any<Class<*>>(), any(), any(), any())
        } returns Mono.error(Exception())

        val signIn = githubGateway.getAccessTokens("code")

        assertErrorWith(signIn) {
            it shouldBe DataNotFound(BLOG600)
            verify {
                webClientWrapper.post(
                    "baseUrl",
                    "accessTokenPath",
                    "",
                    AccessTokenResponse::class.java,
                    linkedMultiValueMap,
                    emptyMap(),
                    mapOf("accept" to "application/json")
                )
            }
        }
    }

    @Test
    fun `should give error if failed to fetch user profile from github`() {
        val accessTokenResponse = AccessTokenResponse(access_token = "accessToken")

        every { secretService.getClientSecret() } returns Mono.just(
            Secret(SecretKeys.GITHUB_CLIENT_SECRET, "clientSecret")
        )
        every { secretService.getClientId() } returns Mono.just(
            Secret(SecretKeys.GITHUB_CLIENT_ID, "clientId")
        )
        every {
            webClientWrapper.get(any(), any(), any<Class<*>>(), any(), any(), any())
        } returns Mono.error(Exception())

        val signIn = githubGateway.getUserProfile(accessTokenResponse)

        assertErrorWith(signIn) {
            it shouldBe DataNotFound(BLOG601)
            verify {
                webClientWrapper.get(
                    "userBaseUrl",
                    "userProfilePath",
                    GithubUser::class.java,
                    headers = mapOf(
                        "accept" to "application/json",
                        HttpHeaders.AUTHORIZATION to "token accessToken"
                    )
                )
            }
        }
    }

    @Test
    fun `should give error if failed to fetch user email from github`() {
        val accessTokenResponse = AccessTokenResponse(access_token = "accessToken")

        every { secretService.getClientSecret() } returns Mono.just(
            Secret(SecretKeys.GITHUB_CLIENT_SECRET, "clientSecret")
        )
        every { secretService.getClientId() } returns Mono.just(
            Secret(SecretKeys.GITHUB_CLIENT_ID, "clientId")
        )
        every {
            webClientWrapper.get(any(), any(), any<Class<*>>(), any(), any(), any())
        } returns Mono.error(Exception())

        val signIn = githubGateway.getUserEmail(accessTokenResponse)

        assertErrorWith(signIn) {
            it shouldBe DataNotFound(BLOG602)
            verify {
                webClientWrapper.get(
                    "userBaseUrl",
                    "userEmailPath",
                    GithubUserEmail::class.java,
                    headers = mapOf(
                        "accept" to "application/json",
                        HttpHeaders.AUTHORIZATION to "token accessToken"
                    )
                )
            }
        }
    }
}
