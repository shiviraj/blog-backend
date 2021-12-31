package com.blog.controller

import com.blog.controller.view.UserView
import com.blog.domain.User
import com.blog.security.domain.UserToken
import com.blog.service.UserService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/users")
class UserController(
    val userService: UserService
) {

    @GetMapping("/me")
    fun validateUser(request: HttpServletRequest): Mono<UserView> {
        val userToken = request.getAttribute("user") as UserToken
        return userService.getUserByUserToken(userToken)
            .map { UserView.from(it) }
    }

    @GetMapping
    fun getAllUser(request: HttpServletRequest): Mono<List<UserView>> {
        val userToken = request.getAttribute("user") as UserToken
        return userService.getAllUsers(userToken)
            .map {
                it.map { user -> UserView.from(user) }.reversed()
            }
    }

    @PostMapping
    fun registerUser(@RequestBody user: User): Mono<UserView> {
        return userService.registerUser(user)
            .map { UserView.from(it) }
    }

//    @GetMapping("/logout")
//    fun logoutUser(request: HttpServletRequest): Mono<UserView> {
//        val userToken = request.getAttribute("user") as UserToken
//        return userService.logoutUser(userToken)
//            .map { UserView.from(it) }
//    }
}

