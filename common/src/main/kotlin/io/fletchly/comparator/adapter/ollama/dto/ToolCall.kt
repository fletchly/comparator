package io.fletchly.comparator.adapter.ollama.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Tool call requests produced by the model
 *
 * @see <a href="https://docs.ollama.com/api/chat">Ollama Chat API docs</a>
 */
@Serializable
data class ToolCall(
    val function: ToolCallFunction?
)

/**
 * Tool call function definition
 *
 * @see <a href="https://docs.ollama.com/api/chat">Ollama Chat API docs</a>
 *
 * @property name name of the function to call
 * @property description what the function does
 * @property arguments JSON object of arguments to pass to the function
 */
@Serializable
data class ToolCallFunction(
    val name: String,
    val description: String?,
    val arguments: JsonObject?
)
