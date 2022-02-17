package com.blog.controller

import com.blog.controller.view.AuthorView
import com.blog.controller.view.UserView
import com.blog.domain.Role
import com.blog.domain.User
import com.blog.domain.UserId
import com.blog.security.authorization.Authorization
import com.blog.service.TokenService
import com.blog.service.UserService
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/users")
class UserController(
    val userService: UserService,
    val tokenService: TokenService
) {
    @Authorization(Role.USER)
    @GetMapping("/me")
    fun validateUser(user: User): Mono<AuthorView> {
        return Mono.just(AuthorView.from(user))
    }

    @GetMapping("/dummy")
    fun getDummyUser(): Mono<Map<String, String>> {
        return userService.getDummyUser().map { mapOf("token" to it.getValue()) }
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: UserId): Mono<UserView> {
        return userService.getUserByUserId(userId).map { UserView.from(it) }
    }

    @Authorization(Role.USER)
    @GetMapping("/logout")
    fun logoutUser(request: HttpServletRequest, user: User): Mono<UserView> {
        val authorization = request.getHeader(HttpHeaders.AUTHORIZATION) as String
        return tokenService.logoutUser(authorization.substringAfter(" "))
            .map { UserView.from(user) }
    }
}

