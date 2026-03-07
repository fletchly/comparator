/*
 * This file is part of comparator, licensed under the Apache License 2.0
 *
 * Copyright (c) 2026 fletchly <https://github.com/fletchly>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.fletchly.comparator.util

import io.ktor.client.call.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import io.ktor.client.HttpClient as KtorClient

class HttpClientTest {
    private fun createTestClient(handler: MockRequestHandler) =
        KtorClient(MockEngine.Companion { handler(it) }, HttpClients.defaultConfig)

    private suspend fun assertThrowsClientError(status: HttpStatusCode) {
        val client = createTestClient { respond("Error", status) }
        assertFailsWith<ClientRequestException> { client.get("https://example.com") }
    }

    private suspend fun assertThrowsServerError(status: HttpStatusCode) {
        val client = createTestClient { respond("Error", status) }
        assertFailsWith<ServerResponseException> { client.get("https://example.com") }
    }

    @Test
    fun `4xx responses throw ClientRequestException`() = runTest {
        listOf(
            HttpStatusCode.BadRequest,
            HttpStatusCode.Unauthorized,
            HttpStatusCode.Forbidden,
            HttpStatusCode.NotFound,
            HttpStatusCode.UnprocessableEntity,
        ).forEach { assertThrowsClientError(it) }
    }

    @Test
    fun `5xx responses throw ServerResponseException`() = runTest {
        listOf(
            HttpStatusCode.InternalServerError,
            HttpStatusCode.NotImplemented,
            HttpStatusCode.BadGateway,
        ).forEach { assertThrowsServerError(it) }
    }


    @Test
    fun `2xx responses do not throw`() = runTest {
        val client = createTestClient { respond("OK", HttpStatusCode.OK) }

        val response = client.get("https://example.com")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `retries on 503 ServiceUnavailable`() = runTest {
        var callCount = 0
        val client = createTestClient {
            callCount++
            if (callCount < 3) respond("Unavailable", HttpStatusCode.ServiceUnavailable)
            else respond("OK", HttpStatusCode.OK)
        }

        client.get("https://example.com")
        assertEquals(3, callCount)
    }

    @Test
    fun `retries on 504 GatewayTimeout`() = runTest {
        var callCount = 0
        val client = createTestClient {
            callCount++
            if (callCount < 3) respond("Gateway Timeout", HttpStatusCode.GatewayTimeout)
            else respond("OK", HttpStatusCode.OK)
        }

        client.get("https://example.com")
        assertEquals(3, callCount)
    }

    @Test
    fun `retries on 429 TooManyRequests`() = runTest {
        var callCount = 0
        val client = createTestClient {
            callCount++
            if (callCount < 3) respond("Too Many Requests", HttpStatusCode.TooManyRequests)
            else respond("OK", HttpStatusCode.OK)
        }

        client.get("https://example.com")
        assertEquals(3, callCount)
    }

    @Test
    fun `does not retry on 500 InternalServerError`() = runTest {
        var callCount = 0
        val client = createTestClient {
            callCount++
            respond("Internal Server Error", HttpStatusCode.InternalServerError)
        }

        assertFailsWith<ServerResponseException> {
            client.get("https://example.com")
        }
        assertEquals(1, callCount)
    }

    @Test
    fun `does not retry on 4xx`() = runTest {
        var callCount = 0
        val client = createTestClient {
            callCount++
            respond("Bad Request", HttpStatusCode.BadRequest)
        }

        assertFailsWith<ClientRequestException> {
            client.get("https://example.com")
        }
        assertEquals(1, callCount)
    }

    @Test
    fun `retries on IOException`() = runTest {
        var callCount = 0
        val client = createTestClient {
            callCount++
            if (callCount < 3) throw IOException("Network failure")
            else respond("OK", HttpStatusCode.OK)
        }

        client.get("https://example.com")
        assertEquals(3, callCount)
    }

    @Test
    fun `ignores unknown JSON keys`() = runTest {
        @Serializable
        data class User(val id: Int)

        val client = createTestClient {
            respond(
                content = """{"id":1,"unknownField":"value"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        // Would throw SerializationException without ignoreUnknownKeys = true
        val user = client.get("https://example.com").body<User>()
        assertEquals(1, user.id)
    }
}