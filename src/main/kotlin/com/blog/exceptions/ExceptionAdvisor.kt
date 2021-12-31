package com.blog.exceptions

import com.blog.exceptions.exceptions.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.server.ServerWebInputException

@ControllerAdvice
class ExceptionAdvisor {

    @ExceptionHandler(ServerWebInputException::class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun invalidWebInput(e: ServerWebInputException): ErrorResponse {
        return ErrorResponse(e.message, "GENERIC_ERROR")
    }

    @ExceptionHandler(BadDataException::class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun badData(e: BadDataException): ErrorResponse {
        return ErrorResponse(e.message, e.errorCode, e.details)
    }

    @ExceptionHandler(DataNotFound::class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    fun noData(e: DataNotFound): ErrorResponse {
        return ErrorResponse(e.message, e.errorCode, e.details)
    }

    @ExceptionHandler(UnprocessableEntityException::class)
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    fun unprocessableEntity(e: UnprocessableEntityException): ErrorResponse {
        return ErrorResponse(e.message, e.errorCode, e.details)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun illegalArgumentException(e: IllegalArgumentException): ErrorResponse {
        return ErrorResponse(e.message.orEmpty(), "GENERIC_ERROR")
    }

    @ExceptionHandler(Throwable::class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun genericThrowable(e: kotlin.Throwable): ErrorResponse {
        return ErrorResponse(e.message.orEmpty(), "GENERIC_ERROR")
    }

    @ExceptionHandler(BaseException::class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun baseException(e: BaseException): ErrorResponse {
        return ErrorResponse(e.message, e.errorCode, e.details)
    }

    @ExceptionHandler(UnauthorizedException::class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ResponseBody
    fun unauthorized(e: UnauthorizedException): ErrorResponse {
        return ErrorResponse(e.message, e.errorCode, e.details)
    }

    @ExceptionHandler(ForbiddenException::class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    @ResponseBody
    fun forbiddenException(e: ForbiddenException): ErrorResponse {
        return ErrorResponse(e.message, e.errorCode, e.details)
    }

    @ExceptionHandler(ValidationException::class)
    @ResponseStatus(value = HttpStatus.EXPECTATION_FAILED)
    @ResponseBody
    fun validationException(e: ValidationException): ValidationErrorDetails {
        return e.validationErrors
    }
}
