package com.blog.gateway

import com.blog.config.GithubConfig
import com.blog.domain.AccessTokenResponse
import com.blog.domain.GithubUser
import com.blog.domain.GithubUserEmail
import com.blog.exceptions.error_code.BlogError
import com.blog.exceptions.exceptions.DataNotFound
import com.blog.service.*
import com.blog.webClient.WebClientWrapper
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import reactor.core.publisher.Mono

@Service
class GithubGateway(
    private val webClientWrapper: WebClientWrapper,
    private val githubConfig: GithubConfig,
    private val secretService: SecretService
) {
    fun getAccessTokens(code: String): Mono<AccessTokenResponse> {
        return createBodyForGetAccessToken(code)
            .flatMap { queryParams ->
                webClientWrapper.post(
                    baseUrl = githubConfig.baseUrl,
                    path = githubConfig.accessTokenPath,
                    body = "",
                    queryParams = queryParams,
                    returnType = AccessTokenResponse::class.java,
                    headers = mapOf("accept" to "application/json")
                )
            }
            .onErrorMap { DataNotFound(BlogError.BLOG600) }
            .logOnSuccess("Successfully fetched access token from github")
            .logOnError("Failed to fetch access token from github")
    }


    fun getUserProfile(accessTokenResponse: AccessTokenResponse): Mono<GithubUser> {
        return webClientWrapper.get(
            baseUrl = githubConfig.userBaseUrl,
            path = githubConfig.userProfilePath,
            returnType = GithubUser::class.java,
            headers = mapOf(
                HttpHeaders.ACCEPT to "application/json",
                HttpHeaders.AUTHORIZATION to "token ${accessTokenResponse.access_token}"
            )
        )
            .onErrorMap { DataNotFound(BlogError.BLOG601) }
            .logOnSuccess("Successfully fetched user profile from github")
            .logOnError("Failed to fetch user profile from github")
    }

    fun getUserEmail(accessTokenResponse: AccessTokenResponse): Mono<GithubUserEmail> {
        return webClientWrapper.get(
            baseUrl = githubConfig.userBaseUrl,
            path = githubConfig.userEmailPath,
            returnType = Array<GithubUserEmail>::class.java,
            headers = mapOf(
                HttpHeaders.ACCEPT to "application/json",
                HttpHeaders.AUTHORIZATION to "token ${accessTokenResponse.access_token}"
            )
        )
            .onErrorMap { DataNotFound(BlogError.BLOG602) }
            .map { githubUserEmails ->
                githubUserEmails.findLast { it.primary }!!
            }
            .logOnSuccess("Successfully fetched user email from github")
            .logOnError("Failed to fetch user email from github")
    }

    private fun createBodyForGetAccessToken(code: String): Mono<LinkedMultiValueMap<String, String>> {
        return Mono.zip(secretService.getClientId(), secretService.getClientSecret())
            .map {
                val linkedMultiValueMap = LinkedMultiValueMap<String, String>()
                linkedMultiValueMap.add("code", code)
                linkedMultiValueMap.add("client_id", it.t1.value)
                linkedMultiValueMap.add("client_secret", it.t2.value)
                linkedMultiValueMap
            }
    }
}
