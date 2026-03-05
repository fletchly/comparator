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

package io.fletchly.comparator.adapter.ollama

import io.fletchly.comparator.util.HttpClients
import io.fletchly.comparator.model.message.Message
import io.fletchly.comparator.model.message.MessageResult
import io.fletchly.comparator.model.message.ToolCall
import io.fletchly.comparator.model.message.conversationOf
import io.fletchly.comparator.model.tool.Tool
import io.fletchly.comparator.model.scope.ConversationScope
import io.fletchly.comparator.port.`in`.ToolExecutor
import io.fletchly.comparator.port.out.LogPort
import io.fletchly.comparator.model.options.OllamaOptions
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.*
import io.ktor.http.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.*
import io.ktor.client.HttpClient as KtorClient

class OllamaAIProviderTest {
    private val log = mockk<LogPort>(relaxed = true)
    private val toolExecutor = mockk<ToolExecutor> {
        every { tools } returns emptyList()
    }
    private val baseUrl = "https://ollama.example.com"
    private val model = "llama3"

    private fun createProvider(handler: MockRequestHandler) = OllamaAIProvider(
        OllamaOptions(
            baseUrlString = baseUrl,
            apiKey = null,
            model = model
        ),
        log = log,
        toolExecutor = toolExecutor,
        client = KtorClient(MockEngine { handler(it) }, HttpClients.defaultConfig)
    )

    private fun conversation(vararg messages: Message) =
        conversationOf(*messages)

    private fun successResponse(content: String = "Hello!") = """
    {
        "model": "llama3",
        "created_at": "2024-01-01T00:00:00Z",
        "message": {
            "role": "assistant",
            "content": "$content",
            "tool_calls": null
        }
    }
""".trimIndent()

    @Test
    fun `returns Success with assistant message on 200`() = runTest {
        val provider = createProvider {
            respond(
                content = successResponse("success"),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val result = provider.generateResponse("You are helpful", conversation())

        assertIs<MessageResult.Success<Message.Assistant>>(result)
        assertEquals("success", result.message.content)
    }

    @Test
    fun `sends request to correct endpoint`() = runTest {
        var capturedUrl: Url? = null
        val provider = createProvider { request ->
            capturedUrl = request.url
            respond(
                successResponse(),
                HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        provider.generateResponse("prompt", conversation())

        assertEquals("/api/chat", capturedUrl!!.encodedPath)
    }

    @Test
    fun `attaches bearer token when apiKey is provided`() = runTest {
        var capturedAuth: String? = null
        val provider = OllamaAIProvider(
            OllamaOptions(
                baseUrlString = baseUrl,
                apiKey = "my-secret-key",
                model = model
            ),
            log = log,
            toolExecutor = toolExecutor,
            client = KtorClient(MockEngine { request ->
                capturedAuth = request.headers[HttpHeaders.Authorization]
                respond(
                    successResponse(),
                    HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                )
            }, HttpClients.defaultConfig)
        )

        provider.generateResponse("prompt", conversation())

        assertEquals("Bearer my-secret-key", capturedAuth)
    }

    @Test
    fun `omits authorization header when apiKey is null`() = runTest {
        var capturedAuth: String? = "not-null"
        val provider = createProvider { request ->
            capturedAuth = request.headers[HttpHeaders.Authorization]
            respond(
                successResponse(),
                HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        provider.generateResponse("prompt", conversation())

        assertNull(capturedAuth)
    }

    @Test
    fun `returns Failure on 401 Unauthorized`() = runTest {
        val provider = createProvider {
            respond("Unauthorized", HttpStatusCode.Unauthorized)
        }

        val result = provider.generateResponse("prompt", conversation())

        assertIs<MessageResult.Failure>(result)
        assertEquals("Client error", result.error)
    }

    @Test
    fun `logs warning on 401 Unauthorized`() = runTest {
        val provider = createProvider {
            respond("Unauthorized", HttpStatusCode.Unauthorized)
        }

        provider.generateResponse("prompt", conversation())

        verify {
            log.warn(match { it.contains("unauthorized", ignoreCase = true) })
        }
    }

    @Test
    fun `returns Failure on other 4xx`() = runTest {
        val provider = createProvider {
            respond("Not Found", HttpStatusCode.NotFound)
        }

        val result = provider.generateResponse("prompt", conversation())

        assertIs<MessageResult.Failure>(result)
        assertEquals("Client error", result.error)
    }

    @Test
    fun `returns Failure on 5xx`() = runTest {
        val provider = createProvider {
            respond("Internal Server Error", HttpStatusCode.InternalServerError)
        }

        val result = provider.generateResponse("prompt", conversation())

        assertIs<MessageResult.Failure>(result)
        assertEquals("Server error", result.error)
    }

    @Test
    fun `returns Failure on IOException`() = runTest {
        val provider = createProvider {
            throw java.io.IOException("Network failure")
        }

        val result = provider.generateResponse("prompt", conversation())

        assertIs<MessageResult.Failure>(result)
        assertEquals("Network error", result.error)
    }

    @Test
    fun `returns Failure on timeout`() = runTest {
        val provider = createProvider {
            throw HttpRequestTimeoutException("https://ollama.example.com", 30_000L)
        }

        val result = provider.generateResponse("prompt", conversation())

        assertIs<MessageResult.Failure>(result)
        assertEquals("Request timed out", result.error)
    }

    @Test
    fun `logs warning on any error`() = runTest {
        val provider = createProvider {
            respond("Internal Server Error", HttpStatusCode.InternalServerError)
        }

        provider.generateResponse("prompt", conversation())

        verify { log.warn(any(), any()) }
    }

    @Test
    fun `includes system prompt and conversation messages in request`() = runTest {
        var capturedBody: String? = null
        val provider = createProvider { request ->
            capturedBody = request.body.toByteArray().decodeToString()
            respond(
                successResponse(),
                HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val sender = mockk<ConversationScope>(relaxed = true)
        provider.generateResponse(
            systemPrompt = "You are helpful",
            conversation = conversation(Message.User("Hello", sender))
        )

        assertNotNull(capturedBody)
        assertTrue(capturedBody.contains("You are helpful"))
        assertTrue(capturedBody.contains("Hello"))
    }

    @Test
    fun `includes tools in request when registry is non-empty`() = runTest {
        var capturedBody: String? = null
        val tool = mockk<Tool> {
            every { name } returns "my_tool"
            every { parameters } returns emptyList()
        }
        val provider = OllamaAIProvider(
            OllamaOptions(
                baseUrlString = baseUrl,
                apiKey = null,
                model = model
            ),
            log = log,
            toolExecutor = mockk { every { tools } returns listOf(tool) },
            client = KtorClient(MockEngine { request ->
                capturedBody = request.body.toByteArray().decodeToString()
                respond(
                    successResponse(),
                    HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                )
            }, HttpClients.defaultConfig)
        )

        provider.generateResponse("prompt", conversation())

        assertTrue(capturedBody!!.contains("my_tool"))
    }

    @Test
    fun `omits tools field when registry is empty`() = runTest {
        var capturedBody: String? = null
        val provider = createProvider { request ->
            capturedBody = request.body.toByteArray().decodeToString()
            respond(
                successResponse(),
                HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        provider.generateResponse("prompt", conversation())

        assertFalse(capturedBody!!.contains("\"tools\":[]"))
    }

    @Test
    fun `parses tool calls from response`() = runTest {
        val provider = createProvider {
            respond(
                content = """
                {
                    "model": "llama3",
                    "created_at": "2024-01-01T00:00:00Z",
                    "message": {
                        "role": "assistant",
                        "content": "",
                        "tool_calls": [
                            {
                                "function": {
                                    "name": "get_weather",
                                    "arguments": {
                                        "location": "London",
                                        "unit": "celsius"
                                    }
                                }
                            }
                        ]
                    }
                }
            """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val result = provider.generateResponse("prompt", conversation())

        assertIs<MessageResult.Success<Message.Assistant>>(result)
        val toolCalls = result.message.toolCalls
        assertNotNull(toolCalls)
        assertEquals(1, toolCalls.size)
        assertEquals("get_weather", toolCalls[0].name)
        assertEquals("London", toolCalls[0].arguments["location"])
        assertEquals("celsius", toolCalls[0].arguments["unit"])
    }

    @Test
    fun `returns null tool calls when response has none`() = runTest {
        val provider = createProvider {
            respond(
                successResponse(),
                HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val result = provider.generateResponse("prompt", conversation())

        assertIs<MessageResult.Success<Message.Assistant>>(result)
        assertNull(result.message.toolCalls)
    }

    @Test
    fun `sends tool messages from conversation correctly`() = runTest {
        var capturedBody: String? = null
        val provider = createProvider { request ->
            capturedBody = request.body.toByteArray().decodeToString()
            respond(
                successResponse(),
                HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        provider.generateResponse(
            systemPrompt = "prompt",
            conversation = conversation(
                Message.Assistant(
                    content = "",
                    toolCalls = listOf(ToolCall("get_weather", mapOf("location" to "London")))
                ),
                Message.Tool("The weather in London is 15°C")
            )
        )

        assertNotNull(capturedBody)
        assertTrue(capturedBody.contains("get_weather"))
        assertTrue(capturedBody.contains("The weather in London is 15°C"))
    }
}