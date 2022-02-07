package com.blog.security

import com.blog.service.Logger
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
class WebTokenFilter(val userService: UserService) : OncePerRequestFilter() {
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
        val user = userService.extractUser(token).block()
        if (token.isEmpty() || user == null) {
            SecurityContextHolder.getContext().authentication = null
        } else {
            val authenticationToken = UsernamePasswordAuthenticationToken("username", null, emptyList())
            authenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = authenticationToken
            request.setAttribute("user", user)
            Logger.info("Successfully validate user", mapOf("userId" to user.userId))
        }
        filterChain.doFilter(request, response)
    }
}
