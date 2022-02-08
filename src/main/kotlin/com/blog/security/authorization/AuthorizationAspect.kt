package com.blog.security.authorization


import com.blog.domain.User
import com.blog.exceptions.error_code.BlogError
import com.blog.exceptions.exceptions.UnauthorizedException
import com.blog.service.UserService
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Aspect
@Component
class AuthorizationAspect(
    @Autowired val userService: UserService
) {

    @Around("@annotation(authorization)")
    fun performAction(proceedingJoinPoint: ProceedingJoinPoint, authorization: Authorization): Mono<Any> {
        return Mono.deferContextual { context ->
            if (context.isEmpty) getUnauthorizedError()
            else {
                val headers = context.get("headers") as HttpHeaders
                val token = headers[HttpHeaders.AUTHORIZATION]!!.first().substringAfter(" ")
                userService.extractUser(token)
            }
        }.flatMap {
            if (it.role.isJunior(authorization.allowedRole)) getUnauthorizedError()
            else proceedingJoinPoint.proceed() as Mono<*>
        }
    }

    private fun getUnauthorizedError(): Mono<User> {
        return Mono.error(UnauthorizedException(BlogError.BLOG606))
    }
}


