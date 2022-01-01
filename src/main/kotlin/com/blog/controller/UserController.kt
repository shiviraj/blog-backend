package com.blog.controller

import com.blog.controller.view.UserView
import com.blog.security.UserId
import com.blog.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/users")
class UserController(
    val userService: UserService
) {

    @GetMapping("/me")
    fun validateUser(userId: UserId): Mono<UserView> {
        return userService.getUser(userId)
            .map { UserView.from(it) }
    }

    @GetMapping
    fun getAllUser(userId: UserId): Mono<List<UserView>> {
        return userService.getAllUsers(userId)
            .map {
                it.map { user -> UserView.from(user) }.reversed()
            }
    }

    @GetMapping("/logout")
    fun logoutUser(userId: UserId): Mono<UserView> {
        return userService.logoutUser(userId)
            .map { UserView.from(it) }
    }
}

