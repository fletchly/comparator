package io.fletchly.comparator.adapter.ollama.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Chat response
 *
 * @see <a href="https://docs.ollama.com/api/chat">Ollama Chat API docs</a>
 *
 * @property model model name used to generate this message
 * @property createdAt timestamp of response creation (ISO 8601)
 * @property message response message
 */
@Serializable
data class ChatResponse(
    val model: String?,
    @SerialName("created_at") val createdAt: String?,
    val message: ChatMessage.Assistant,
)
