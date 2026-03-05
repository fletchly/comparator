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

import io.fletchly.comparator.adapter.ollama.dto.*
import io.fletchly.comparator.model.message.Conversation
import io.fletchly.comparator.model.message.Message
import io.fletchly.comparator.model.message.MessageResult
import io.fletchly.comparator.model.message.ToolCall
import io.fletchly.comparator.model.options.OllamaOptions
import io.fletchly.comparator.model.tool.Parameter
import io.fletchly.comparator.model.tool.Tool
import io.fletchly.comparator.port.`in`.ToolExecutor
import io.fletchly.comparator.port.out.AIPort
import io.fletchly.comparator.port.out.LogPort
import io.fletchly.comparator.util.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.io.IOException

/**
 * An implementation of the [AIPort] interface that integrates with the Ollama API to generate responses
 * in a conversational system.
 *
 * This class handles communication with the Ollama REST API for chat functionalities, including sending
 * chat requests, handling errors, and processing responses. It supports configuration through [OllamaOptions]
 * and utilizes tools managed by a [ToolExecutor].
 *
 * @constructor Creates an instance of the [OllamaAIProvider] with the specified configuration, logging,
 * tool registry, and HTTP client.
 *
 * @property config The configuration for the Ollama API, including the base URL, API key, and model identifier.
 * @property log A logging interface for recording information and warnings during API interactions.
 * @property toolExecutor A registry for managing tools that the system can use during message processing.
 * @property client The HTTP client used for making requests to the Ollama API. Defaults to [HttpClients.KtorCIO].
 *
 * @see AIPort
 */
class OllamaAIProvider(
    private val config: OllamaOptions,
    private val log: LogPort,
    private val toolExecutor: ToolExecutor,
    private val client: HttpClient = HttpClients.KtorCIO
) : AIPort {
    override suspend fun generateResponse(
        systemPrompt: String,
        conversation: Conversation
    ): MessageResult<Message.Assistant> {
        val chatRequest = buildChatRequest(systemPrompt, conversation)
        val url = URLBuilder(config.baseUrl).apply { path("api", "chat") }.build()

        val chatResponse: ChatResponse = runCatching {
            client.post(url) {
                if (!config.apiKey.isNullOrBlank()) bearerAuth(config.apiKey)
                contentType(ContentType.Application.Json)
                setBody(chatRequest)
            }.body<ChatResponse>()
        }.getOrElse { ex ->
            log.warn(ex.stackTraceToString(), this::class.simpleName)
            return when (ex) {
                is ClientRequestException -> {
                    if (ex.response.status == HttpStatusCode.Unauthorized)
                        log.warn("Got 'unauthorized' response from Ollama server! Is your API key set?")
                    MessageResult.Failure("Client error")
                }

                is ServerResponseException -> MessageResult.Failure("Server error")
                is HttpRequestTimeoutException -> MessageResult.Failure("Request timed out")
                is IOException -> MessageResult.Failure("Network error")
                else -> MessageResult.Failure("Unknown error")
            }
        }

        return MessageResult.Success(chatResponse.message.toAssistantMessage())
    }

    private fun buildChatRequest(systemPrompt: String, conversation: Conversation): ChatRequest {
        val systemMessage = ChatMessage.System(systemPrompt)
        val conversationMessages = conversation.messages
            .map { it.toChatMessage() }

        val messages = buildList {
            add(systemMessage)
            addAll(conversationMessages)
        }

        val tools: List<ChatTool>? = toolExecutor.getTools()
            .map { it.toChatTool() }
            .ifEmpty { null }

        val options = ChatOptions(
            temperature = TEMPERATURE,
            topK = TOP_K,
            topP = TOP_P,
            numPredict = NUM_PREDICT
        )

        return ChatRequest(
            config.model,
            messages,
            tools,
            options
        )
    }

    private fun Message.toChatMessage(): ChatMessage {
        return when (this) {
            is Message.User -> ChatMessage.User(this.content)
            is Message.Tool -> ChatMessage.Tool(this.content)
            is Message.Assistant -> ChatMessage.Assistant(
                this.content,
                this.toolCalls
                    ?.map { it.toChatToolCall() }
            )
        }
    }

    private fun ChatMessage.Assistant.toAssistantMessage() =
        Message.Assistant(
            content = this.content,
            toolCalls = this.toolCalls?.map { it.toToolCall() }
        )

    private fun ToolCall.toChatToolCall() = ChatToolCall(
        function = ToolCallFunction(
            name = this.name,
            arguments = this.arguments.toJsonObject()
        )
    )

    private fun ChatToolCall.toToolCall() = ToolCall(
        name = this.function.name,
        arguments = this.function.arguments.toMap()
    )

    private fun Tool.toChatTool() = ChatTool(
        function = ChatToolFunction(
            name = this.name,
            parameters = this.parameters.toJsonSchema()
        )
    )

    private fun List<Parameter>.toJsonSchema(): JsonSchema {
        val properties = this.associate { param ->
            param.name to JsonProperty(
                type = param.type.name.lowercase(),
                description = param.description
            )
        }

        val required = this
            .filter { it.required }
            .map { it.name }

        return JsonSchema(
            "object",
            properties,
            required
        )
    }

    companion object {
        private const val TEMPERATURE = 0.5F
        private const val TOP_K = 40
        private const val TOP_P = 0.85F
        private const val NUM_PREDICT = 400
    }
}