package io.fletchly.comparator.adapter.ollama.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Tool definition
 *
 * @see <a href="https://docs.ollama.com/api/chat">Ollama Chat API docs</a>
 *
 * @property type type of tool (always `function`)
 * @property function tool function definition
 */
@Serializable
data class Tool(val function: ToolFunction) {
    @Suppress("unused")
    val type: String = "function"
}

/**
 * Tool function definition
 *
 * @see <a href="https://docs.ollama.com/api/chat">Ollama Chat API docs</a>
 *
 * @property name name of the function to call
 * @property description what the function does
 * @property parameters JSON Schema for the function parameters
 */
@Serializable
data class ToolFunction(
    val name: String,
    val parameters: JsonObject,
    val description: String? = null
)