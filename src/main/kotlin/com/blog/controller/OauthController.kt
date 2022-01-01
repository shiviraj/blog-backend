package com.blog.controller

import com.blog.controller.view.AuthenticationResponse
import com.blog.controller.view.UserView
import com.blog.domain.Secret
import com.blog.service.OauthService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/oauth")
class OauthController(
    val oauthService: OauthService
) {

    @GetMapping("/client-id")
    fun getClientId(): Mono<Secret> {
        return oauthService.getClientId()
    }

    @PostMapping("/sign-in/code")
    fun signIn(@RequestBody code: CodeRequest): Mono<AuthenticationResponse> {
        return oauthService.signIn(code)
            .map {
                AuthenticationResponse(it.first, UserView.from(it.second))
            }
    }
}


data class CodeRequest(val code: String)
