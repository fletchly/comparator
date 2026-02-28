package io.fletchly.comparator.adapter.ollama

import io.fletchly.comparator.adapter.ollama.dto.*
import io.fletchly.comparator.infra.http.HttpClient
import io.fletchly.comparator.model.message.Conversation
import io.fletchly.comparator.model.message.Message
import io.fletchly.comparator.model.message.MessageResult
import io.fletchly.comparator.model.message.ToolCall
import io.fletchly.comparator.model.tool.Tool
import io.fletchly.comparator.model.tool.ToolParameter
import io.fletchly.comparator.port.`in`.ToolRegistry
import io.fletchly.comparator.port.out.AIPort
import io.fletchly.comparator.port.out.LogPort
import io.fletchly.comparator.util.JsonProperty
import io.fletchly.comparator.util.JsonSchema
import io.fletchly.comparator.util.toJsonObject
import io.fletchly.comparator.util.toMap
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.io.IOException

/**
 * An AI provider implementation that handles generating responses using the Ollama API.
 *
 * This class is responsible for integrating with the Ollama API to generate responses
 * in conversational systems. It manages request construction, API communication, and
 * response parsing. It also processes conversations, tools, and logging for system
 * interactions.
 *
 * @param baseUrl The base URL of the Ollama API.
 * @param apiKey The API key required for authentication with the Ollama API. Can be null if no authentication is needed.
 * @param model The name of the AI model to use when generating responses.
 * @param log An implementation of [LogPort] for logging information and warnings.
 * @param toolRegistry An implementation of [ToolRegistry] to manage available tools for tool-based message handling.
 */
class OllamaAIProvider(
    private val baseUrl: Url,
    private val apiKey: String?,
    private val model: String,
    private val log: LogPort,
    private val toolRegistry: ToolRegistry
) : AIPort {
    private val client = HttpClient.ktor

    override suspend fun generateResponse(
        systemPrompt: String,
        conversation: Conversation
    ): MessageResult<Message.Assistant> {
        val chatRequest = buildChatRequest(systemPrompt, conversation)
        val url = URLBuilder(baseUrl).apply { path("api", "chat") }.build()

        val chatResponse: ChatResponse = runCatching {
            client.post(url) {
                if (!apiKey.isNullOrBlank()) bearerAuth(apiKey)
                contentType(ContentType.Application.Json)
                setBody(chatRequest)
            }.body<ChatResponse>()
        }.getOrElse { ex ->
            log.warn("[OllamaAIProvider] ${ex.stackTraceToString()}")
            return when (ex) {
                is ClientRequestException -> MessageResult.Failure("Client error")
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

        val tools: List<ChatTool>? = toolRegistry.tools
            .map { it.toChatTool() }
            .ifEmpty { null }

        val options = ChatOptions(
            temperature = TEMPERATURE,
            topK = TOP_K,
            topP = TOP_P,
            numPredict = NUM_PREDICT
        )

        return ChatRequest(
            model,
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

    private fun List<ToolParameter>.toJsonSchema(): JsonSchema {
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