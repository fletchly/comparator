package io.fletchly.comparator.adapter.ollama.dto

import io.fletchly.comparator.util.JsonSchema
import kotlinx.serialization.Serializable

/**
 * ChatTool definition
 *
 * @see <a href="https://docs.ollama.com/api/chat">Ollama Chat API docs</a>
 *
 * @property type type of tool (always `function`)
 * @property function tool function definition
 */
@Serializable
data class ChatTool(val function: ChatToolFunction) {
    @Suppress("unused")
    val type: String = "function"
}

/**
 * ChatTool function definition
 *
 * @see <a href="https://docs.ollama.com/api/chat">Ollama Chat API docs</a>
 *
 * @property name name of the function to call
 * @property description what the function does
 * @property parameters JSON Schema for the function parameters
 */
@Serializable
data class ChatToolFunction(
    val name: String,
    val parameters: JsonSchema,
    val description: String? = null
)