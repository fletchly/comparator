package io.fletchly.comparator.adapter.ollama.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Chat history message
 *
 * @see <a href="https://docs.ollama.com/api/chat">Ollama Chat API docs</a>
 *
 * @property content message text content
 * @property role author of the message.
 */
sealed interface ChatMessage {
    val content: String
    val role: String

    /**
     * User message
     *
     * @property role always `user`
     */
    @Serializable
    data class User(
        override val content: String,
    ): ChatMessage {
        override val role: String = "user"
    }

    /**
     * Assistant message
     *
     * @property toolCalls tool call requests produced by the model
     * @property role always `assistant`
     */
    @Serializable
    data class Assistant(
        override val content: String,

        @SerialName("tool_calls")
        val toolCalls: List<ToolCall>
    ): ChatMessage {
        override val role: String = "assistant"
    }

    /**
     * Tool message
     *
     * @property role always `tool`
     */
    @Serializable
    data class Tool(
        override val content: String,
    ): ChatMessage {
        override val role: String = "tool"
    }

    /**
     * System message
     *
     * @property role always `system`
     */
    @Serializable
    data class System(
        override val content: String,
    ): ChatMessage {
        override val role: String = "system"
    }
}
