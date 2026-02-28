package io.fletchly.comparator.adapter.ollama

import io.fletchly.comparator.adapter.ollama.dto.ChatMessage
import io.fletchly.comparator.adapter.ollama.dto.ChatOptions
import io.fletchly.comparator.adapter.ollama.dto.ChatRequest
import io.fletchly.comparator.adapter.ollama.dto.ChatResponse
import io.fletchly.comparator.adapter.ollama.dto.ChatTool
import io.fletchly.comparator.adapter.ollama.dto.ChatToolCall
import io.fletchly.comparator.adapter.ollama.dto.ToolCallFunction
import io.fletchly.comparator.infra.http.HttpClient
import io.fletchly.comparator.model.message.Conversation
import io.fletchly.comparator.model.message.Message
import io.fletchly.comparator.model.message.MessageResult
import io.fletchly.comparator.model.message.ToolCall
import io.fletchly.comparator.port.out.AIPort
import io.fletchly.comparator.port.out.LogPort
import io.fletchly.comparator.util.toJsonObject
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.http.path
import kotlinx.io.IOException

class OllamaAIProvider(
    private val baseUrl: Url,
    private val apiKey: String?,
    private val model: String,
    private val log: LogPort,
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

        TODO()
    }

    private fun buildChatRequest(systemPrompt: String, conversation: Conversation): ChatRequest {
        val systemMessage = ChatMessage.System(systemPrompt)
        val conversationMessages = conversation.messages
            .map { it.toChatMessage() }

        val messages = buildList {
            add(systemMessage)
            addAll(conversationMessages)
        }

        val tools: List<ChatTool>? = null

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

    private fun ToolCall.toChatToolCall() = ChatToolCall(
        function = ToolCallFunction(
            name = this.name,
            arguments = this.arguments?.toJsonObject()
        )
    )

    companion object {
        private const val TEMPERATURE = 0.5F
        private const val TOP_K = 40
        private const val TOP_P = 0.85F
        private const val NUM_PREDICT = 400
    }
}