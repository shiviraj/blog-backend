//package com.blog.service
//
//import com.blog.controller.CodeRequest
//import com.blog.domain.*
//import com.blog.gateway.GithubGateway
//import com.blog.testUtils.assertNextWith
//import io.kotest.matchers.shouldBe
//import io.mockk.clearAllMocks
//import io.mockk.every
//import io.mockk.mockk
//import io.mockk.verify
//import org.junit.jupiter.api.AfterEach
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import reactor.core.publisher.Mono
//
//class OauthServiceTest {
//    private val userService = mockk<UserService>()
//    private val secretService = mockk<SecretService>()
//    private val githubGateway = mockk<GithubGateway>()
//    private val oauthService = OauthService(userService, secretService, githubGateway)
//
//    @BeforeEach
//    fun setUp() {
//        clearAllMocks()
//    }
//
//    @AfterEach
//    fun tearDown() {
//        clearAllMocks()
//    }
//
//    @Test
//    fun `should get clientId`() {
//        every { secretService.getClientId() } returns Mono.just(Secret(SecretKeys.GITHUB_CLIENT_ID, "clientId"))
//
//        val clientId = oauthService.getClientId()
//        assertNextWith(clientId) {
//            it.value shouldBe "clientId"
//        }
//    }
//
//    @Test
//    fun `should signIn user from github oAuth`() {
//        val accessTokenResponse = AccessTokenResponse(access_token = "accessToken", token_type = "bearer")
//        val githubUser = GithubUser(username = "username", id = 0, profile = "profile", source = LoginSource.GITHUB)
//        val githubUserEmail = GithubUserEmail(email = "example@email.com", primary = true, verified = true)
//        every { githubGateway.getAccessTokens(any()) } returns Mono.just(accessTokenResponse)
//        every { githubGateway.getUserProfile(any()) } returns Mono.just(githubUser)
//        every { githubGateway.getUserEmail(any()) } returns Mono.just(githubUserEmail)
////        every { userService.signInUserFromOauth(any(), any()) } returns Mono.just("loggedIn")
//
//        val signIn = oauthService.signIn(CodeRequest("code"))
//
//        assertNextWith(signIn) {
//            it shouldBe "loggedIn"
//            verify(exactly = 1) {
//                githubGateway.getAccessTokens("code")
//                githubGateway.getUserProfile(accessTokenResponse)
//                githubGateway.getUserEmail(accessTokenResponse)
//                userService.signInUserFromOauth(githubUser, githubUserEmail)
//            }
//        }
//    }
//}
