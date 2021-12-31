package com.blog.exceptions.exceptions

import com.fasterxml.jackson.annotation.JsonProperty

open class ValidationException(
    val validationErrors: ValidationErrorDetails,
    override val message: String
) : Throwable(message = message) {

    val errorCodes = validationErrors.errors.joinToString(separator = ", ") { it.errorCode }
}

class ValidationErrorDetails(@JsonProperty("errors") val errors: List<ErrorResponse>) {
    constructor(error: ErrorResponse) : this(listOf(error))
}
