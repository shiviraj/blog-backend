package com.blog.service

import com.blog.config.OAuthConfig
import com.blog.controller.CodeRequest
import com.blog.domain.Secret
import com.blog.exceptions.error_code.BlogError.*
import com.blog.exceptions.exceptions.DataNotFound
import com.blog.testUtils.assertErrorWith
import com.blog.testUtils.assertNextWith
import com.blog.webClient.WebClientWrapper
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.util.LinkedMultiValueMap
import reactor.core.publisher.Mono

class OauthServiceTest {
    private val webClientWrapper = mockk<WebClientWrapper>()
    private val userService = mockk<UserService>()
    private val secretService = mockk<SecretService>()
    private val oAuthConfig = OAuthConfig(
        baseUrl = "baseUrl",
        accessTokenPath = "accessTokenPath",
        userBaseUrl = "userBaseUrl",
        userProfilePath = "userProfilePath",
        userEmailPath = "userEmailPath"
    )
    private val oauthService = OauthService(webClientWrapper, userService, secretService, oAuthConfig)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `should get clientId`() {
        every { secretService.getClientId() } returns Mono.just(Secret(SecretKeys.GITHUB_CLIENT_ID, "clientId"))

        val clientId = oauthService.getClientId()
        assertNextWith(clientId) {
            it.value shouldBe "clientId"
        }
    }

    @Test
    fun `should signIn user from github oAuth`() {
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
        } returns Mono.just(AccessTokenResponse(access_token = "accessToken", token_type = "bearer"))
        val githubUser = GithubUser(username = "username", id = 0, profile = "profile", source = LoginSource.GITHUB)
        every {
            webClientWrapper.get(any(), any(), GithubUser::class.java, any(), any(), any())
        } returns Mono.just(githubUser)
        val githubUserEmail = GithubUserEmail(email = "example@email.com", primary = true, verified = true)
        every {
            webClientWrapper.get(any(), any(), Array<GithubUserEmail>::class.java, any(), any(), any())
        } returns Mono.just(arrayOf(githubUserEmail))
        every { userService.signInUserFromOauth(any(), any()) } returns Mono.just("loggedIn")

        val signIn = oauthService.signIn(CodeRequest("code"))

        assertNextWith(signIn) {
            it shouldBe "loggedIn"
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
                userService.signInUserFromOauth(githubUser, githubUserEmail)
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

        val signIn = oauthService.signIn(CodeRequest("code"))

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
        } returns Mono.just(AccessTokenResponse(access_token = "accessToken", token_type = "bearer"))
        every {
            webClientWrapper.get(any(), any(), GithubUser::class.java, any(), any(), any())
        } returns Mono.error(Exception())
        val githubUserEmail = GithubUserEmail(email = "example@email.com", primary = true, verified = true)
        every {
            webClientWrapper.get(any(), any(), Array<GithubUserEmail>::class.java, any(), any(), any())
        } returns Mono.just(arrayOf(githubUserEmail))

        val signIn = oauthService.signIn(CodeRequest("code"))

        assertErrorWith(signIn) {
            it shouldBe DataNotFound(BLOG601)
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
    fun `should give error if failed to fetch user email from github`() {
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
        } returns Mono.just(AccessTokenResponse(access_token = "accessToken", token_type = "bearer"))
        val githubUser = GithubUser(username = "username", id = 0, profile = "profile", source = LoginSource.GITHUB)
        every {
            webClientWrapper.get(any(), any(), GithubUser::class.java, any(), any(), any())
        } returns Mono.just(githubUser)
        every {
            webClientWrapper.get(any(), any(), Array<GithubUserEmail>::class.java, any(), any(), any())
        } returns Mono.error(Exception())

        val signIn = oauthService.signIn(CodeRequest("code"))

        assertErrorWith(signIn) {
            it shouldBe DataNotFound(BLOG602)
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
}
