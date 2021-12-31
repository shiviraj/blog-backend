package com.blog.webClient

import com.blog.testUtils.assertErrorWith
import com.blog.testUtils.assertNextWith
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.instanceOf
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.WebClient

class WebClientWrapperTest {
    private val server = MockWebServer()
    private val webClient = WebClient.builder().build()

    @BeforeEach
    fun setUp() {
        server.start(7000)
    }

    @AfterEach
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `should call get api with given headers`() {
        server.responseForNextRequest("{msg:hello}")
        val baseUrl = server.getBaseUrl()
        val responseMono = WebClientWrapper(webClient)
            .get(
                baseUrl = baseUrl,
                path = "",
                returnType = String::class.java,
                headers = mapOf("non-default-header" to "non-default-value"),
            )

        assertNextWith(responseMono) {
            server.takeRequest().headers.get("non-default-header") shouldBe "non-default-value"
        }
    }

    @Test
    fun `should make a get call to given uri`() {
        val uri = "login/customer/"
        server.responseForNextRequest("{msg:hello}")
        val baseUrl = server.getBaseUrl()

        val responseMono = WebClientWrapper(webClient)
            .get(
                baseUrl = baseUrl,
                path = uri,
                returnType = String::class.java,
            )

        assertNextWith(responseMono) {
            val recordedRequest = server.takeRequest()
            val expectedUri = "$baseUrl$uri"
            recordedRequest.requestUrl.toString() shouldBe expectedUri
        }
    }

    @Test
    fun `should make get call with query params`() {
        server.responseForNextRequest("{msg:hello}")
        val baseUrl = server.getBaseUrl()
        val queryParams = LinkedMultiValueMap<String, String>()
        queryParams.add("key1", "value1")
        queryParams.add("key2", "value2")

        val responseMono = WebClientWrapper(webClient)
            .get(
                baseUrl = baseUrl,
                path = "",
                returnType = String::class.java,
                queryParams = queryParams,
            )

        assertNextWith(responseMono) {
            val recordedRequest = server.takeRequest()
            recordedRequest.requestUrl.toString() shouldBe "$baseUrl?key1=value1&key2=value2"
        }
    }

    @Test
    fun `should successfully get api response`() {
        server.responseForNextRequest("{msg:hello}")

        val baseUrl = server.getBaseUrl()


        val responseMono = WebClientWrapper(webClient)
            .get(
                baseUrl,
                "{product}",
                String::class.java,
                uriVariables = mapOf("product" to "PERSONAL"),
                headers = mapOf("non-default-header" to "non-default-value"),
            )

        assertNextWith(responseMono) {
            it shouldBe "{msg:hello}"
        }
    }

    @Test
    fun `should fail get api when server is not reachable`() {
        val baseUrl = "http://localhost:12345"

        val responseMono = WebClientWrapper(webClient)
            .get(
                baseUrl = baseUrl,
                path = "",
                returnType = String::class.java,
            )

        assertErrorWith(responseMono) {
            it shouldBe instanceOf(Throwable::class)
        }
    }

    @Test
    fun `should fail get api when server returns error`() {
        server.responseForNextRequest("{msg:hello}", 500)
        val baseUrl = server.getBaseUrl()

        val responseMono = WebClientWrapper(webClient)
            .get(
                baseUrl = baseUrl,
                path = "",
                returnType = String::class.java,
            )

        assertErrorWith(responseMono) {
            it shouldBe instanceOf(Exception::class)
        }
    }

    @Test
    fun `should return internal server error for get api for any other failure`() {
        server.responseForNextRequest("{msg:hello}", 200)
        val baseUrl = server.getBaseUrl()

        val responseMono = WebClientWrapper(webClient)
            .get(
                baseUrl = baseUrl,
                path = "",
                returnType = Int::class.java,
            )

        assertErrorWith(responseMono) {
            it shouldBe instanceOf(Throwable::class)
        }
    }

    @Test
    fun `should call post api with given headers`() {
        server.responseForNextRequest("{msg:hello}")
        val baseUrl = server.getBaseUrl()

        val responseMono = WebClientWrapper(webClient)
            .post(
                baseUrl = baseUrl,
                path = "",
                body = "",
                returnType = String::class.java,
                headers = mapOf("non-default-header" to "non-default-value"),
            )

        assertNextWith(responseMono) {
            server.takeRequest().headers.get("non-default-header") shouldBe "non-default-value"
        }
    }

    @Test
    fun `should make a post call to given uri`() {
        val uri = "login/customer/{product}"
        server.responseForNextRequest("{msg:hello}")
        val baseUrl = server.getBaseUrl()

        val responseMono = WebClientWrapper(webClient)
            .post(
                baseUrl = baseUrl,
                path = uri,
                body = "",
                returnType = String::class.java,
                uriVariables = mapOf("product" to "four_wheeler"),
            )

        assertNextWith(responseMono) {
            val recordedRequest = server.takeRequest()
            val expectedUri = "${baseUrl}login/customer/four_wheeler"
            recordedRequest.requestUrl.toString() shouldBe expectedUri
        }
    }

    @Test
    fun `should make post api call with query params`() {
        server.responseForNextRequest("{msg:hello}")
        val baseUrl = server.getBaseUrl()
        val queryParams = LinkedMultiValueMap<String, String>()
        queryParams.add("key1", "value1")
        queryParams.add("key2", "value2")

        val responseMono = WebClientWrapper(webClient)
            .post<String>(baseUrl, "", "", String::class.java, queryParams = queryParams)

        assertNextWith(responseMono) {
            val recordedRequest = server.takeRequest()
            recordedRequest.requestUrl.toString() shouldBe "$baseUrl?key1=value1&key2=value2"
        }
    }

    @Test
    fun `should post api response successfully`() {
        server.responseForNextRequest("{msg:hello}")
        val baseUrl = server.getBaseUrl()

        val responseMono = WebClientWrapper(webClient)
            .post<String>(
                baseUrl = baseUrl,
                path = "",
                body = """{"message":"hello"}""",
                returnType = String::class.java,
                headers = mapOf("non-default-header" to "non-default-value"),
            )

        assertNextWith(responseMono) {
            it shouldBe "{msg:hello}"
        }
    }

    @Test
    fun `should make post api call with given json string body`() {
        server.responseForNextRequest("{msg:hello}")
        val baseUrl = server.getBaseUrl()

        val responseMono = WebClientWrapper(webClient)
            .post(
                baseUrl = baseUrl,
                path = "",
                body = """{"message":"hello"}""",
                returnType = String::class.java,
            )

        assertNextWith(responseMono) {
            val recordedRequest = server.takeRequest()
            recordedRequest.body.toString() shouldBe "[text=${"""{"message":"hello"}"""}]"
        }
    }

    @Test
    fun `should make post api call with form data with json string`() {
        server.responseForNextRequest("{msg:hello}")
        val baseUrl = server.getBaseUrl()

        val formData: MultiValueMap<String, String> = LinkedMultiValueMap();
        formData.add("product_code", "2889")
        formData.add("qData", ObjectMapper().writeValueAsString(Dummy("John")))

        val responseMono = WebClientWrapper(webClient)
            .post(
                baseUrl = baseUrl,
                path = "",
                body = formData,
                returnType = String::class.java,
            )

        assertNextWith(responseMono) {
            val recordedRequest = server.takeRequest()
            recordedRequest.body.toString() shouldBe "[text=product_code=2889&qData=%7B%22name%22%3A%22John%22%7D]"
        }
    }

    @Test
    fun `should make post api call with custom body object`() {
        server.responseForNextRequest("{msg:hello}")
        val baseUrl = server.getBaseUrl()

        val responseMono = WebClientWrapper(webClient)
            .post(
                baseUrl = baseUrl,
                path = "",
                body = Dummy("John"),
                returnType = String::class.java,
            )

        assertNextWith(responseMono) {
            val recordedRequest = server.takeRequest()
            recordedRequest.body.toString() shouldBe """[text={"name":"John"}]"""
        }
    }

    @Test
    fun `should fail post api when server is not reachable`() {
        val baseUrl = "http://localhost:12345"

        val responseMono = WebClientWrapper(webClient)
            .post(
                baseUrl = baseUrl,
                path = "",
                body = "",
                returnType = String::class.java,
            )

        assertErrorWith(responseMono) {
            it shouldBe instanceOf(Throwable::class)
        }
    }

    @Test
    fun `should fail post api when server returns internal server error`() {
        server.responseForNextRequest(responseCode = 500)
        val baseUrl = server.getBaseUrl()

        val responseMono = WebClientWrapper(webClient)
            .post(
                baseUrl = baseUrl,
                path = "",
                body = "",
                returnType = String::class.java,
            )

        assertErrorWith(responseMono) {
            it shouldBe instanceOf(Exception::class)
        }
    }

    @Test
    fun `should return internal server error for post api for any other failure`() {
        server.responseForNextRequest("{msg:hello}", 200)
        val baseUrl = server.getBaseUrl()

        val responseMono = WebClientWrapper(webClient)
            .post(
                baseUrl = baseUrl,
                path = "",
                body = "",
                returnType = Int::class.java,
            )

        assertErrorWith(responseMono) {
            it shouldBe instanceOf(Throwable::class)
        }
    }

    @Test
    fun `should log url on successful get request`() {
        server.responseForNextRequest("{msg:hello}")
        val baseUrl = server.getBaseUrl()

        val logs = captureStandardOut {
            WebClientWrapper(webClient)
                .get(
                    baseUrl = baseUrl,
                    path = "hello/{path}",
                    returnType = String::class.java,
                    headers = mapOf("non-default-header" to "non-default-value"),
                    queryParams = LinkedMultiValueMap<String, String>().apply {
                        put("ping", listOf("pong"))
                        put("ding", listOf("dong"))
                    },
                    uriVariables = mapOf(
                        "path" to "world"
                    ),

                    ).block()
        }

        logs shouldContain "http://localhost:7000/hello/world?ping=pong&ding=dong"
    }

    @Test
    fun `should log url on successful post request`() {
        server.responseForNextRequest("{msg:hello}")
        val baseUrl = server.getBaseUrl()

        val logs = captureStandardOut {
            WebClientWrapper(webClient)
                .post(
                    baseUrl = baseUrl,
                    path = "hello/{path}",
                    returnType = String::class.java,
                    headers = mapOf("non-default-header" to "non-default-value"),
                    queryParams = LinkedMultiValueMap<String, String>().apply {
                        put("ping", listOf("pong"))
                        put("ding", listOf("dong"))
                    },
                    uriVariables = mapOf(
                        "path" to "world"
                    ),
                    body = """{
                     "message" : "hello world"   
                    }""".trimIndent(),

                    ).block()
        }

        logs shouldContain "http://localhost:7000/hello/world?ping=pong&ding=dong"
    }

    @Test
    fun `should log url on failed post request`() {
        server.responseForNextRequest("{msg:hello}", 400)
        val baseUrl = server.getBaseUrl()

        val logs = captureStandardOut {
            kotlin.runCatching {
                WebClientWrapper(webClient)
                    .post(
                        baseUrl = baseUrl,
                        path = "hello/{path}",
                        returnType = String::class.java,
                        headers = mapOf("non-default-header" to "non-default-value"),
                        queryParams = LinkedMultiValueMap<String, String>().apply {
                            put("ping", listOf("pong"))
                            put("ding", listOf("dong"))
                        },
                        uriVariables = mapOf(
                            "path" to "world"
                        ),
                        body = """{
                            "message" : "hello world"   
                        }""".trimIndent(),

                        ).block()
            }
        }

        logs shouldContain "http://localhost:7000/hello/world?ping=pong&ding=dong"
    }
}

fun MockWebServer.responseForNextRequest(body: Any? = null, responseCode: Int = 200) {
    val response = MockResponse()
        .setResponseCode(responseCode)
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    body?.let {
        val bodyString = body as? String ?: jacksonObjectMapper().writeValueAsString(body)
        response.setBody(bodyString)
    }
    this.enqueue(response)
}

fun MockWebServer.getBaseUrl() = this.url("").toString()

data class Dummy(val name: String)
