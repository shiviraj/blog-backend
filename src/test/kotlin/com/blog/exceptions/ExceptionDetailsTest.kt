package com.blog.exceptions

import com.blog.exceptions.exceptions.ErrorResponse
import com.blog.exceptions.exceptions.ExceptionDetails
import com.blog.exceptions.exceptions.toBaseException
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class ExceptionDetailsTest {

    @Test
    fun `should return map of error code and message give Exception detail`() {
        val errorResponse = ErrorResponse(errorCode = "THAN-3001", message = "Quote is failing")

        errorResponse.toServiceError().toBaseException().toMap() shouldBe mapOf(
            "errorCode" to "THAN-3001",
            "message" to "Quote is failing",
            "details" to emptyMap<String, Any>()
        )
    }

    @Test
    fun `should create error details from multiple error responses`() {
        val errorResponses = listOf(
            ErrorResponse(errorCode = "THAN-3001", message = "Quote is failing"),
            ErrorResponse(errorCode = "THAN-3002", message = "Proposal is failing")
        )
        val details = ExceptionDetails.createFromErrorResponse(errorResponses)

        details["errors"] shouldBe arrayOf(
            mapOf("errorCode" to "THAN-3001", "message" to "Quote is failing", "details" to emptyMap<String, Any>()),
            mapOf("errorCode" to "THAN-3002", "message" to "Proposal is failing", "details" to emptyMap<String, Any>())
        )
    }

    @Test
    fun `should create error details from single error response`() {
        val errorResponses = listOf(
            ErrorResponse(errorCode = "THAN-3001", message = "Quote is failing"),
        )
        val details = ExceptionDetails.createFromErrorResponse(errorResponses)

        details shouldBe mapOf(
            "errorCode" to "THAN-3001",
            "message" to "Quote is failing",
            "details" to emptyMap<String, Any>()
        )
    }

    @Test
    fun `should create error details from multiple base exceptions`() {
        val baseExceptions = listOf(
            ErrorResponse(errorCode = "THAN-3001", message = "Quote is failing").toServiceError().toBaseException(),
            ErrorResponse(errorCode = "THAN-3002", message = "Proposal is failing").toServiceError().toBaseException()
        )
        val details = ExceptionDetails.createFromBaseExceptions(baseExceptions)

        details["errors"] shouldBe arrayOf(
            mapOf("errorCode" to "THAN-3001", "message" to "Quote is failing", "details" to emptyMap<String, Any>()),
            mapOf("errorCode" to "THAN-3002", "message" to "Proposal is failing", "details" to emptyMap<String, Any>())
        )
    }

    @Test
    fun `should create error details from single base exceptions`() {
        val baseExceptions = listOf(
            ErrorResponse(errorCode = "THAN-3001", message = "Quote is failing").toServiceError().toBaseException(),
        )
        val details = ExceptionDetails.createFromBaseExceptions(baseExceptions)

        details shouldBe mapOf(
            "errorCode" to "THAN-3001",
            "message" to "Quote is failing",
            "details" to emptyMap<String, Any>()
        )
    }

    @Test
    fun `should create error details from multiple throwables`() {
        val throwables = listOf(
            ErrorResponse(errorCode = "THAN-3001", message = "Quote is failing").toServiceError().toBaseException(),
            RuntimeException("Proposal is failing")
        )
        val details = ExceptionDetails.createFromThrowables(throwables)

        details["errors"] shouldBe arrayOf(
            mapOf("errorCode" to "THAN-3001", "message" to "Quote is failing", "details" to emptyMap<String, Any>()),
            mapOf("errorCode" to "", "message" to "Proposal is failing", "details" to emptyMap<String, Any>())
        )
    }

    @Test
    fun `should create error details from single throwable`() {
        val throwables = listOf(
            RuntimeException("Proposal is failing")
        )
        val details = ExceptionDetails.createFromThrowables(throwables)

        details shouldBe mapOf(
            "errorCode" to "",
            "message" to "Proposal is failing",
            "details" to emptyMap<String, Any>()
        )
    }
}
