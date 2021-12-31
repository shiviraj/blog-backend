package com.blog.exceptions

import com.blog.exceptions.exceptions.ErrorResponse
import com.blog.exceptions.exceptions.ServiceError
import com.blog.exceptions.exceptions.ValidationErrorDetails
import com.blog.exceptions.exceptions.ValidationException
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class ValidationExceptionTest {

    @Test
    fun `should get all error codes in validation exception concatenated with comma`() {
        val validationException = ValidationException(
            ValidationErrorDetails(
                listOf(
                    ErrorResponse(Error("code1", "message1")),
                    ErrorResponse(Error("code2", "message2"))
                )
            ),
            "message"
        )

        validationException.errorCodes shouldBe "code1, code2"
    }
}

class Error(override val errorCode: String, override val message: String) : ServiceError
