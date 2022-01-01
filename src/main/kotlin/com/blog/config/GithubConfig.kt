package com.blog.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("blog.oauth")
data class GithubConfig(
    val baseUrl: String,
    val accessTokenPath: String,
    val userBaseUrl: String,
    val userProfilePath: String,
    val userEmailPath: String
)
