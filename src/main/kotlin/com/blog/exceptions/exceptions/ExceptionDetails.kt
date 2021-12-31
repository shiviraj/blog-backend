package com.blog.exceptions.exceptions

object ExceptionDetails {

    fun createFromThrowables(throwables: List<Throwable>): Map<String, Any> {
        return createFromBaseExceptions(
            throwables.map {
                when (it) {
                    is Exception -> {
                        ErrorResponse(errorCode = "", message = it.message
                            ?: "").toServiceError().toBaseException()
                    }
                    else -> {
                        it
                    }
                }
            }.filterIsInstance(BaseException::class.java)
        )
    }

    fun createFromBaseExceptions(baseExceptions: List<BaseException>): Map<String, Any> {
        return when {
            baseExceptions.size > 1 -> {
                mapOf("errors" to baseExceptions.distinct().map { it.toMap() })
            }
            baseExceptions.size == 1 -> {
                baseExceptions[0].toMap()
            }
            else -> emptyMap()
        }
    }

    fun createFromErrorResponse(errors: List<ErrorResponse>): Map<String, Any> {
        return createFromBaseExceptions(errors.map { it.toServiceError().toBaseException() })
    }
}
