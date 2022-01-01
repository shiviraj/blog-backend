package com.blog.security

import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class AuthenticationDataResolverConfigurer(
    private val authenticationTokenDataResolver: AuthenticationTokenDataResolver
) : WebMvcConfigurer {
    override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver>) {
        argumentResolvers.add(authenticationTokenDataResolver)
    }
}
