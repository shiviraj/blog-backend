package com.blog.exceptions.exceptions

import com.blog.exceptions.exceptions.BaseException
import com.blog.exceptions.exceptions.ServiceError

class UnauthorizedException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
