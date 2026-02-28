package io.fletchly.comparator.adapter.ollama.dto

import kotlinx.serialization.Serializable

/**
 * Represents a request for a chat interaction, containing the necessary parameters
 * and configurations for generating a response.
 *
 * @see <a href="https://docs.ollama.com/api/chat">Ollama Chat API docs</a>
 *
 * @property model model identifier
 * @property messages chat history as an array of message objects (each with a role and content)
 * @property tools optional list of function tools the model may call during the chat
 * @property options runtime options that control text generation
 */
@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val tools: List<Tool>?,
    val options: ChatOptions,
    ) {
    @Suppress("unused")
    val stream = false

    @Suppress("unused")
    val think = false
}

