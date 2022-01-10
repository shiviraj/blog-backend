package com.blog.controller

import com.blog.controller.view.AuthorView
import com.blog.controller.view.UserView
import com.blog.domain.User
import com.blog.service.UserService
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/users")
class UserController(
    val userService: UserService
) {

    @GetMapping("/me")
    fun validateUser(user: User): Mono<AuthorView> {
        return Mono.just(AuthorView.from(user))
    }

    @GetMapping("/logout")
    fun logoutUser(request: HttpServletRequest, user: User): Mono<UserView> {
        val token = request.getHeader(HttpHeaders.AUTHORIZATION).substringAfter(" ")
        return userService.logoutUser(user, token)
            .map { UserView.from(it) }
    }
}

