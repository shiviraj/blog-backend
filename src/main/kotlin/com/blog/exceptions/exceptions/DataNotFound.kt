package com.blog.exceptions.exceptions

class DataNotFound(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
