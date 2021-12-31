package com.blog.security

import com.blog.security.domain.UserToken
import com.blog.security.service.UserTokenService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Service
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class WebTokenFilter(
    val userTokenService: UserTokenService,
) :
    OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = request.getHeader("authorization")
        userFilterChain(token.substringAfter(" "), request, filterChain, response)
    }

    private fun userFilterChain(
        token: String,
        request: HttpServletRequest,
        filterChain: FilterChain,
        response: HttpServletResponse
    ) {
        val user = userTokenService.extractUser(token)
        val userToken = userTokenService.extractUserToken(token)
        if (userToken != null && user != null && isValidUser(userToken)) {
            val authenticationToken = UsernamePasswordAuthenticationToken(user.username, null, emptyList())
            authenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = authenticationToken
            request.setAttribute("user", userToken)
            logger.info("Successfully validate user")
        } else {
            SecurityContextHolder.getContext().authentication = null
            logger.error("Failed to validate user")
        }
        filterChain.doFilter(request, response)
    }

    private fun isValidUser(userToken: UserToken): Boolean {
        val isAuthenticateNull = SecurityContextHolder.getContext().authentication == null
        return isAuthenticateNull && userTokenService.validate(userToken)
    }
}
