package com.blog.domain

import com.fasterxml.jackson.annotation.JsonProperty

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

data class GithubUserEmail(
    val email: String,
    val primary: Boolean = false,
    val verified: Boolean = false,
    val visibility: String? = null
)

data class AccessTokenResponse(val access_token: String = "", val token_type: String = "")
