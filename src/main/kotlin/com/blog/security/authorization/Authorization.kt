package com.blog.security.authorization

import com.blog.domain.Role

@Target(AnnotationTarget.FUNCTION)
annotation class Authorization(
    val allowedRole: Role = Role.DUMMY,
)
