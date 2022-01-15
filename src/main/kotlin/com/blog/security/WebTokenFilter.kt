package com.blog.security

import com.blog.service.UserService
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Service
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class WebTokenFilter(val webToken: WebToken, val userService: UserService) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = request.getHeader(HttpHeaders.AUTHORIZATION).orEmpty()
        userFilterChain(token.substringAfter(" "), request, filterChain, response)
    }

    private fun userFilterChain(
        token: String,
        request: HttpServletRequest,
        filterChain: FilterChain,
        response: HttpServletResponse
    ) {
        if (AllowedPath.paths.contains(request.requestURI)) {
            val authenticationToken = UsernamePasswordAuthenticationToken("uniqueId", "password", null)
            authenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = authenticationToken
        } else {
            val userAuthenticationData = webToken.getUserAuthenticationData(token)
            if (SecurityContextHolder.getContext().authentication == null && userAuthenticationData.isValid()) {
                val authenticationToken = UsernamePasswordAuthenticationToken(
                    userAuthenticationData.uniqueId,
                    "password",
                    null
                )
                authenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authenticationToken
                logger.info("Successfully validate user")
                val user = userService.getUserByUserId(userAuthenticationData.userId).block()!!
                request.setAttribute("user", user)
            } else {
                SecurityContextHolder.getContext().authentication = null
                logger.error("Failed to validate user")
            }
        }
        filterChain.doFilter(request, response)
    }
}

object AllowedPath {
    val paths: List<String> = listOf("/oauth/client-id", "/oauth/sign-in/code", "/posts/published/*")
}
