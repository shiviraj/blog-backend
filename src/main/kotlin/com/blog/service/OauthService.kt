package com.blog.service

import com.blog.config.OAuthConfig
import com.blog.controller.CodeRequest
import com.blog.domain.Secret
import com.blog.exceptions.error_code.BlogError.*
import com.blog.exceptions.exceptions.DataNotFound
import com.blog.webClient.WebClientWrapper
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import reactor.core.publisher.Mono


@Service
class OauthService(
    val webClientWrapper: WebClientWrapper,
    val userService: UserService,
    val secretService: SecretService,
    val oAuthConfig: OAuthConfig
) {
    fun getClientId(): Mono<Secret> {
        return secretService.getClientId()
            .logOnSuccess("Successfully get client Id")
            .logOnError("Failed to get client id")
    }

    fun signIn(code: CodeRequest): Mono<String> {
        return getAccessTokens(code.code)
            .flatMap {
                Mono.zip(
                    getUserProfile(it),
                    getUserEmail(it)
                )
            }
            .flatMap {
                userService.signInUserFromOauth(it.t1, it.t2)
            }
    }

    private fun getUserProfile(accessTokenResponse: AccessTokenResponse): Mono<GithubUser> {
        return webClientWrapper.get(
            baseUrl = oAuthConfig.userBaseUrl,
            path = oAuthConfig.userProfilePath,
            returnType = GithubUser::class.java,
            headers = mapOf(
                HttpHeaders.ACCEPT to "application/json",
                HttpHeaders.AUTHORIZATION to "token ${accessTokenResponse.access_token}"
            )
        )
            .onErrorMap { DataNotFound(BLOG601) }
            .logOnSuccess("Successfully fetched user profile from github")
            .logOnError("Failed to fetch user profile from github")
    }

    private fun getUserEmail(accessTokenResponse: AccessTokenResponse): Mono<GithubUserEmail> {
        return webClientWrapper.get(
            baseUrl = oAuthConfig.userBaseUrl,
            path = oAuthConfig.userEmailPath,
            returnType = Array<GithubUserEmail>::class.java,
            headers = mapOf(
                HttpHeaders.ACCEPT to "application/json",
                HttpHeaders.AUTHORIZATION to "token ${accessTokenResponse.access_token}"
            )
        )
            .onErrorMap { DataNotFound(BLOG602) }
            .map { githubUserEmails ->
                githubUserEmails.findLast { it.primary }!!
            }
            .logOnSuccess("Successfully fetched user email from github")
            .logOnError("Failed to fetch user email from github")
    }

    private fun getAccessTokens(code: String): Mono<AccessTokenResponse> {
        return createBodyForGetAccessToken(code)
            .flatMap { queryParams ->
                webClientWrapper.post(
                    baseUrl = oAuthConfig.baseUrl,
                    path = oAuthConfig.accessTokenPath,
                    body = "",
                    queryParams = queryParams,
                    returnType = AccessTokenResponse::class.java,
                    headers = mapOf("accept" to "application/json")
                )
            }
            .onErrorMap { DataNotFound(BLOG600) }
            .logOnSuccess("Successfully fetched access token from github")
            .logOnError("Failed to fetch access token from github")

    }

    private fun createBodyForGetAccessToken(code: String): Mono<LinkedMultiValueMap<String, String>> {
        return Mono.zip(getClientId(), secretService.getClientSecret())
            .map {
                val linkedMultiValueMap = LinkedMultiValueMap<String, String>()
                linkedMultiValueMap.add("code", code)
                linkedMultiValueMap.add("client_id", it.t1.value)
                linkedMultiValueMap.add("client_secret", it.t2.value)
                linkedMultiValueMap
            }
    }
}

data class AccessTokenResponse(val access_token: String = "", val token_type: String = "")

data class GithubUser(
    @JsonProperty("login")
    val username: String,
    val id: Long,
    @JsonProperty("avatar_url")
    val profile: String,
    val name: String? = null,
    val email: String? = null,
    val location: String? = null,
    val source: LoginSource = LoginSource.GITHUB
)

enum class LoginSource {
    GITHUB
}

data class GithubUserEmail(
    val email: String,
    val primary: Boolean = false,
    val verified: Boolean = false,
    val visibility: String? = null
)
