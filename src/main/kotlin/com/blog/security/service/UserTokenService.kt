package com.blog.security.service

import com.blog.security.WebToken
import com.blog.security.domain.UserToken
import com.blog.service.UserService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserTokenService(val webToken: WebToken, val userService: UserService) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails? {
        val user = userService.getUserByToken(username).block()
        if (user != null) {
            return User(user.email, "", emptyList())
        }
        return null
    }

    fun extractUser(token: String): UserDetails? {
        return loadUserByUsername(token)
    }

    fun validate(userToken: UserToken): Boolean {
        return userToken.validate()
    }

    fun extractUserToken(token: String): UserToken? {
        return webToken.extractUser(token)
    }
}
