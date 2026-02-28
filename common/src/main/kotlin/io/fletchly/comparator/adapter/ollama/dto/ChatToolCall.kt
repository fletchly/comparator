package io.fletchly.comparator.adapter.ollama.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * ChatTool call requests produced by the model
 *
 * @see <a href="https://docs.ollama.com/api/chat">Ollama Chat API docs</a>
 */
@Serializable
data class ChatToolCall(
    val function: ToolCallFunction
)

/**
 * ChatTool call function definition
 *
 * @see <a href="https://docs.ollama.com/api/chat">Ollama Chat API docs</a>
 *
 * @property name name of the function to call
 * @property arguments JSON object of arguments to pass to the function
 */
@Serializable
data class ToolCallFunction(
    val name: String,
    val arguments: JsonObject
)
